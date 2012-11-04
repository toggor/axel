package com.example.accsel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager sensorManager;

	TextView xCoor; // declare X axis object
	TextView yCoor; // declare Y axis object
	TextView zCoor; // declare Z axis object
	TextView recNum;
	File dataDir;
	File saveFile;
	int state = 0;
	PrintWriter csvWriter;
	Time now = new Time();

	public void startListen(View view) {
		Log.v("Axel Listener", "Starting listener");
		if (state != 0) {
			Log.e("Start Listener", "State!=0, not starting!");
			return;
		}
		now.setToNow();
		String TimeStampDB = now.format("%F_%H-%M-%S");
		int FileNum = 0;
		try {
			FileNum = dataDir.list().length + 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			showError("Can not get Datadir size!");
			e.printStackTrace();
			return;
		}
		recNum.setText("Record # : " + FileNum);
		if (recNum.getVisibility() != android.view.View.VISIBLE)
			recNum.setVisibility(android.view.View.VISIBLE);
		saveFile = new File(dataDir.getAbsolutePath() + "/" + FileNum + "_"
				+ TimeStampDB + ".csv");
		if (!saveFile.exists())
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				showError("Can not create new file!");
				return;
			}

		try {
			csvWriter = new PrintWriter(new FileWriter(saveFile, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError("Can not create new writer!");
			return;
		}
		state = 1;
		sensorManager
				.registerListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void showError(String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(text).setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});

		builder.show();
	}

	public void stopListen(View view) {
		Log.v("Axel Listener", "Stopping listener");
		if (state != 1) {
			Log.e("Stop Listener", "State!=1, not stopping!");
			return;
		}
		sensorManager
				.unregisterListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
		csvWriter.close();
		state = 0;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("Create", "Creating...");

		setContentView(R.layout.activity_main);

		xCoor = (TextView) findViewById(R.id.xcoor); // create X axis object
		yCoor = (TextView) findViewById(R.id.ycoor); // create Y axis object
		zCoor = (TextView) findViewById(R.id.zcoor); // create Z axis object
		recNum = (TextView) findViewById(R.id.recnum);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/axel";
		dataDir = new File(extStorageDirectory);
		if (!dataDir.exists()) {
			try {
				dataDir.mkdir();
			} catch (Exception e) {
				showError("Can not create new Datadir on SD card!");
			}
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {

		// check sensor type
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

			// assign directions
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			writeToLog(x, y, z, event.timestamp);
			xCoor.setText("X: " + x);
			yCoor.setText("Y: " + y);
			zCoor.setText("Z: " + z);
		}
	}

	public void writeToLog(float x, float y, float z, long nanos)
	{
		try {
			csvWriter.printf("%f;%f;%f;%d%s", x, y, z, nanos, "\n");
		} catch (Exception er) {
			er.printStackTrace();
			stopListen(null);
			showError("Can not print to file");
		}

	}
}
