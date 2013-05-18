package com.sensorcon.sensordronecontrol;

import android.app.Application;

import com.sensorcon.sensordrone.Drone;

/**
 * We will make a class that extends Application to put our Drone in, that way
 * we can use it in multiple activities (used here for the main screen and
 * graphing).
 */
public class DroneApplication extends Application {
	public Drone myDrone;

	// Set some streaming rates, so we can switch back to a default rate when
	// coming back from graphing.
	public int defaultRate = 1000;
	public int streamingRate;

	@Override
	public void onCreate() {
		super.onCreate();

		myDrone = new Drone();
		streamingRate = defaultRate;
	}

}
