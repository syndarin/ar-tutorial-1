package com.syndarin.artutorial1;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ART1 extends Activity implements SurfaceHolder.Callback, SensorEventListener {

	private final static String tag = ART1.class.getSimpleName();

	private SurfaceView mSurface;
	private SurfaceHolder mHolder;
	private Camera mCamera;
	
	private SensorManager mSensor;
	
	private float azimuth;
	private float pitch;
	private float roll;
	
	private float accX;
	private float accY;
	private float accZ;
	
	private boolean isPreviewing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_art1);
		
		mSurface = (SurfaceView) findViewById(R.id.surface);
		mHolder = mSurface.getHolder();
		mHolder.addCallback(this);
	
		mSensor = (SensorManager) getSystemService(SENSOR_SERVICE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mSensor.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensor.registerListener(this, mSensor.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
		mSensor.registerListener(this, mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(tag, "onSurfaceChanged");

		if (mCamera != null && isPreviewing) {
			mCamera.stopPreview();
		}

		Camera.Parameters parameters = mCamera.getParameters();
		Camera.Size size = getBestPreviewSize(width, height, parameters);

		if (size != null) {
			parameters.setPreviewSize(size.width, size.height);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
			isPreviewing = true;
		}
	}

	private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {

		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int currentCamArea = result.width * result.height;
					int newCamArea = size.width * size.height;
					if (newCamArea > currentCamArea) {
						result = size;
					}
				}
			}
		}

		return result;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(tag, "onSurfaceCreated");
		try {
			mCamera = Camera.open();
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(tag, "onSurfaceDestroyed");
		if (isPreviewing && mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.i(tag, "onSensorChanged");
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			float[] data = event.values;
			azimuth = data[0];
			pitch = data[1];
			roll = data[2];
			
			Log.i(tag, String.format("azimuth - %.2f, pitch - %.2f, roll - %.2f", azimuth, pitch, roll));
		} else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			float[] data = event.values;
			accX = data[0];
			accY = data[1];
			accZ = data[2];
			
			Log.i(tag, String.format("accX - %.2f, accY - %.2f, accZ - %.2f", accX, accY, accZ));
		}
	}

}
