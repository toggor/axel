package com.example.accsel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager sensorManager;

	TextView xCoor; // declare X axis object
	TextView yCoor; // declare Y axis object
	TextView zCoor; // declare Z axis object

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopListen();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		xCoor = (TextView) findViewById(R.id.xcoor); // create X axis object
		yCoor = (TextView) findViewById(R.id.ycoor); // create Y axis object
		zCoor = (TextView) findViewById(R.id.zcoor); // create Z axis object

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// add listener. The listener will be HelloAndroid (this) class
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		/*
		 * More sensor speeds (taken from api docs) SENSOR_DELAY_FASTEST get
		 * sensor data as fast as possible SENSOR_DELAY_GAME rate suitable for
		 * games SENSOR_DELAY_NORMAL rate (default) suitable for screen
		 * orientation changes
		 */
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void stopListen() {
		Log.v("Axel Listener", "Stoping listener");
		sensorManager.unregisterListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

	}

	public void onSensorChanged(SensorEvent event) {

		// check sensor type
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			// assign directions
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			
			 try { writeToLog(x, y, z); } catch (IOException e) { 
				 e.printStackTrace(); 
				 stopListen(); }
			 
			xCoor.setText("X: " + x);
			yCoor.setText("Y: " + y);
			zCoor.setText("Z: " + z);
		}
	}

	public void writeToLog(float x, float y, float z) throws IOException {
		File myFile;
		PrintWriter csvWriter;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		String TimeStampDB = sdf.format(cal.getTime());
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/axel";
		File e = new File(extStorageDirectory);
		if (!e.exists())
			e.mkdir();
		try {
			//String oneLineStringBuffer="";//= new StringBuffer();

			myFile = new File(extStorageDirectory + "/Export_" + TimeStampDB
					+ ".csv");
			if (!myFile.exists()) {
				myFile.createNewFile();
			}
			csvWriter = new PrintWriter(new FileWriter(myFile, true));

			/* 2. append to stringBuffer */
			//oneLineStringBuffer.concat(x + "," + y + "," + z);
			//oneLineStringBuffer.concat("\n");

			/* 3. print to csvWriter */
			csvWriter.print(x + ";" + y + ";" + z+"\n");

			csvWriter.close();
		} catch (Exception er) {
			er.printStackTrace();
		}

	}
}
