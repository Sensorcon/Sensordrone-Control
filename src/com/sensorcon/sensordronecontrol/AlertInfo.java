package com.sensorcon.sensordronecontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/*
 * This class just has the PopUp info used in Menus/Dialogs
 */
public class AlertInfo {

	/*
	 *  We will pass our main Class which extends activity, so we can use
	 *  runOnUiThread to make sure things properly display
	 */
	private Activity UIActivity;

	/*
	 * Default constructor, set up the UIActivity
	 */
	public AlertInfo(Activity uiActivity) {
		UIActivity = uiActivity;
	}


	/*
	 * Low battery indication
	 */
	public void lowBattery() {
		UIActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(UIActivity);
				alert.setTitle("Low Battery!");
				alert.setMessage("Your Sensordrone's battery is low. Please charge it up!");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//
					}
				});
				alert.show();
			}
		});
	}

	/*
	 * Explains connections to the user
	 */
	public void connectionInfo() {
		UIActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(UIActivity);
				alert.setTitle("Connections");
				alert.setMessage(
						"Scan to Connect:\n" +
								"This will start scanning for Sensordrones, " +
								"and populate a list. Selecting a Sensordrone from the list will " +
								"attempt to connect.\n\n" +
								//
								"Re-Connect:\n" +
								"Once you have connected with a Sensordrone, it remembers the last device " +
								"it was connected to. After disconnecting, clicking Re-Connect will attempt to re-connect with " +
								"it without having to scan for the device again. As of version 1.2, this is stored a preferences file" +
								" and you can use re-connect even if you've recently closed the app.\n\n" +
								//
								"Disconnect:\n" +
								"This will disconnect from the Sensordrone you are currently conencted to.\n\n" +
								"General Info:\n" +
								"If the connection to the Sensordrone is lost, this app will try " +
								"to automatically re-connect once.\n\n" +
								"If you Sensordrone's battery level drops too low, this app will automatically " +
								"disconnect, and ask you to charge it back up. On low battery, the Sensordrone's " +
								"LEDs will also start to flash red as a low battery indication."
						);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//
					}
				});
				alert.show();
			}
		});
	}

	/*
	 * Explain how getting data works
	 */
	public void dataInfo() {
		UIActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(UIActivity);
				alert.setTitle("Acquiring Data");
				alert.setMessage(
						"While you are connected to a Sensordrone, the On/Off " +
								"toggle buttons can be used to Start/Stop the acquisition " +
								"of data. When on, it will update the data about once a second. " +
								"\n\nThe On/Off buttons do nothing if you are not connected."
						);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//
					}
				});
				alert.show();
			}
		});
	}

	/*
	 * Explain how graphing works
	 */
	public void graphingInfo() {
		UIActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(UIActivity);
				alert.setTitle("Graphing");
				alert.setMessage(
						"When you are connected to a Sensordrone and are acquiring data, " +
						"you can view a graph of that data by tapping on the measured value. " +
						"The graph will display the last 30 measurements made starting from " +
						"when the graph is displayed. \n\n" +
						"If you are not currently acquiring data, tapping the value field " +
						"does nothing."
						);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//
					}
				});
				alert.show();
			}
		});
	}

	/*
	 * General info about the sensors
	 */
	public void sensorInfo() {
		UIActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(UIActivity);
				alert.setTitle("Sensor Info");
				alert.setMessage(
						"Temperature (Ambient):\n" +
						"This is the Temperature of the environement your Sensordrone " +
						"is currently in.\n\n" +
						//
						"Humidity:\n" +
						"This is the Percent Relative Humidity of the environement your Sensordrone " +
						"is currently in.\n\n" +
						//
						"Pressure:\n" +
						"This is the Pressure of the environement your Sensordrone " +
						"is currently in.\n\n" +
						//
						"Object Temperature (IR):\n" +
						"This is the (non-contact) temperature of the object you are pointing your " +
						"Sensordrone at.\n\n" +
						//
						"Illuminance:\n" +
						"The Sensordrone is eqiupped with an RGB color sensor. From these values, " +
						"you can calculate things like Color, Color Temeprature, and Illuminance. " +
						"The Lux value provided is calibrated for a (mostly) broadband light source " +
						"(like incandescent and flurescent bulbs), and won't " +
						"work well for narrow band light sources like LEDs " +
						"(without a separate calibration algorithm).\n\n" +
						//
						"Precision Gas:\n" +
						"The precision gas sensor responds to low concentrations of several gases " +
						"and is calibrated at the factory with carbon monoxide (CO).  " +
						"The units of ppm (parts per million) is a representation of the gas " +
						"concentration assuming the gas being measured is CO.\n\n"+
						//
						"Proximity Capacitance:\n" +
						"The Sensordrone has a proximity capacitance sensor located in the bottom " +
						"of it's plastic housing. It will react differently to different material. " +
						"Try a few different things out! It currently measures from 0 to 4 " +
						"picoFarad with a resolution of 1 femtoFarad.\n\n" +
						//
						"External Voltage:\n" +
						"This is the measured voltage from the Sensordrone's external ADC pin. " +
						"It accepts 0 to 3 Volts and will read in the millivolt range. The pin " +
						"is left floating, and will jump around if nothing is plugged into it.\n\n" +
						//
						"Altitude:\n" +
						"Altitude (above sea level) can be thought of as a psuedo-sensor. The altitude " +
						"is calculated from the current pressure referenced to pressure " +
						"at sea-level. Changes in the weather can cause changes in your calculated " +
						"altitude above sea leavel.\n\n" +
						"Battery Voltage:\n" +
						"This is the Sensordrone's current battery voltage level. The Sensordrone uses " +
						"a Lithium Polymer (LiPo) battery, and therefore will typically read between 4.2 " +
						"and 3.25 Volts (a low battery event is triggered in this app at 3.25 Volts)"
						);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//
					}
				});
				alert.show();
			}
		});
	}
	
	/*
	 * Notify if the connection wasn't successful
	 */
	public void connectFail() {
		UIActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(UIActivity);
				alert.setTitle("Couldn't connect!");
				alert.setMessage("Connection was not successful.\n Please try again!");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//
					}
				});
				alert.show();
			}
		});
	}
	
}
