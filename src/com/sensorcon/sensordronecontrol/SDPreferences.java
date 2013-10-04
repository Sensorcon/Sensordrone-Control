package com.sensorcon.sensordronecontrol;

public class SDPreferences {

	/*
	 * Properties
	 */
	// Our last Sensordrone
	static String LAST_MAC = "LAST_MAC";
	
	// Ambient Temperature
	static String TEMPERATURE_UNIT = "TEMPERATURE_UNIT";
	// Pressure 
	static String PRESSURE_UNIT = "PRESSURE_UNIT";
	// IR
	static String IR_TEMPERATURE_UNIT = "IR_TEMPERATURE_UNIT";
	// Altitude
	static String ALTITUDE_UNIT = "ALTITUDE_UNIT";
	
	/*
	 * Units
	 * 
	 * 0 will be default value for any property!
	 */
	
	// Temperature
	static int FAHRENHEIT = 0;
	static int CELSIUS = 1;
	static int KELVIN = 2;
	
	// Pressure
	static int PASCAL = 0;
	static int KILOPASCAL = 1;
	static int ATMOSPHERE = 2;
	static int MMHG = 3;
	static int INHG = 4;
    static int HECTOPASCAL = 5;
	
	// Distance (Altitude)
	static int FEET = 0;
	static int MILES = 1;
	static int METER = 2;
	static int KILOMETER = 3;
	

    static String LAST_KNOWN_VERSION_CODE = "LAST_KNOWN_VERSION_CODE";
	
	
}
