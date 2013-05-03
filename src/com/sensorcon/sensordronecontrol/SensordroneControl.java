package com.sensorcon.sensordronecontrol;



import java.util.EventObject;

import com.sensorcon.sensordronecontrol.R;
import com.sensorcon.sensordrone.Drone.DroneEventListener;
import com.sensorcon.sensordrone.Drone.DroneStatusListener;
import com.sensorcon.sdhelper.ConnectionBlinker;
import com.sensorcon.sdhelper.SDBatteryStreamer;
import com.sensorcon.sdhelper.SDHelper;
import com.sensorcon.sdhelper.SDStreamer;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
// Don't let eclipse import android.widget.TableLayout.LayoutParams for your TableRows!
import android.widget.TableRow.LayoutParams;

/**
 * Sensordrone Control for the Sensordrone
 * 
 * Built against Android-10
 * 
 * Build using SDAndroidLib 1.1.1
 * 
 * @author Sensorcon, Inc
 *
 */
public class SensordroneControl extends Activity {

	/*
	 * Preferences
	 */
	SharedPreferences sdcPreferences;
	
	/*
	 * We put our Drone object in a class that extends Application so it
	 * can be accessed in multiple activities.
	 */
	protected DroneApplication droneApp;
	
	/*
	 * A class which store useful Alert Dialogs
	 */
	public AlertInfo myInfo;
	
	/*
	 * Because Android will destroy and re-create things on events like orientation changes,
	 * we will need a way to store our objects and return them in such a case. 
	 * 
	 * A simple and straightforward way to do this is to create a class which has all of the objects
	 * and values we want don't want to get lost. When our orientation changes, it will reload our
	 * class, and everything will behave as normal! See onRetainNonConfigurationInstance in the code
	 * below for more information.
	 * 
	 * A lot of the GUI set up will be here, and initialized via the Constructor
	 */
	public final class Storage {

		// A ConnectionBLinker from the SDHelper Library
		public ConnectionBlinker myBlinker;

		// Our Listeners
		public DroneEventListener deListener;
		public DroneStatusListener dsListener;
		public String MAC = "";

		// An int[] that will hold the QS_TYPEs for our sensors of interest
		public int[] qsSensors;

		// Text to display
		public String[] sensorNames= {
				"Temperature (Ambient)",
				"Humidity",
				"Pressure",
				"Object Temperature (IR)",
				"Illuminance (calculated)",
				"Precision Gas (CO equivalent)",
				"Proximity Capacitance",
				"External Voltage (0-3V)",
				"Altitude (calculated)"
		};

		// Figure out how many sensors we have based on the length of our labels
		public int numberOfSensors = sensorNames.length;

		// GUI Stuff
		public TableLayout onOffLayout;
		public ToggleButton[] toggleButtons = new ToggleButton[numberOfSensors];
		public TextView tvConnectionStatus;
		public TextView tvConnectInfo;
		public TextView[] tvSensorValues = new TextView[numberOfSensors];
		public TextView[] tvLabel = new TextView[numberOfSensors];
		public LinearLayout logoLayout;
		public TextView logoText;
		public ImageView logoImage;
		public TableRow[] sensorRow = new TableRow[numberOfSensors];
		
		// This is added for the battery voltage
		public TableRow bvRow;
		public ToggleButton bvToggle;
		public TextView bvLabel;
		public TextView bvValue;
		
		// Another object from the SDHelper library. It helps us set up our pseudo streaming
		public SDStreamer[] streamerArray = new SDStreamer[numberOfSensors];

		// We only want to notify of a low battery once,
		// but the event might be triggered multiple times.
		// We use this to try and show it only once
		public boolean lowbatNotify;


		/*
		 * Our TableRow layout
		 */
		public LayoutParams trLayout = new LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT
				);
		/*
		 * Our TextView label layout
		 */
		public LayoutParams tvLayout = new LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT, 
				0.45f
				);

		/*
		 * Our ToggleButton layout
		 */
		public LayoutParams tbLayout = new LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT, 
				0.1f
				);

		// Our constructor to set up the GUI
		public Storage(Context context, TableLayout mainLayout) {

			onOffLayout = mainLayout;

			qsSensors = new int[] {
					droneApp.myDrone.QS_TYPE_TEMPERATURE,
					droneApp.myDrone.QS_TYPE_HUMIDITY,
					droneApp.myDrone.QS_TYPE_PRESSURE,
					droneApp.myDrone.QS_TYPE_IR_TEMPERATURE,
					droneApp.myDrone.QS_TYPE_RGBC,
					droneApp.myDrone.QS_TYPE_PRECISION_GAS,
					droneApp.myDrone.QS_TYPE_CAPACITANCE,
					droneApp.myDrone.QS_TYPE_ADC,
					droneApp.myDrone.QS_TYPE_ALTITUDE
			};

			// This will Blink our Drone, once a second, Blue
			myBlinker = new ConnectionBlinker(droneApp.myDrone, 1000, 0, 0, 255);




			// Set up the TableRows
			for (int i = 0; i < numberOfSensors; i++) {

				// The clickListener will need a final type of i
				final int counter = i;

				sensorRow[i] = new TableRow(context);
				sensorRow[i].setPadding(10, 10, 10, 0);


				toggleButtons[i] = new ToggleButton(context);
				tvSensorValues[i] = new TextView(context);
				tvLabel[i] = new TextView(context);
				streamerArray[i] = new SDStreamer(droneApp.myDrone, qsSensors[i]);

				sensorRow[i].setLayoutParams(trLayout); // Set the layout
				tvLabel[i].setText(sensorNames[i]); // Set the text
				tvLabel[i].setLayoutParams(tvLayout); // Set the layout

				tvSensorValues[i].setBackgroundResource(R.drawable.valuegradient);
				tvSensorValues[i].setTextColor(Color.WHITE);
				tvSensorValues[i].setGravity(Gravity.CENTER);
				tvSensorValues[i].setText("--"); // Start off with -- for the sensor value on create
				tvSensorValues[i].setLayoutParams(tvLayout); // Set the layout

				// This ClickListener will handle Graphing
				tvSensorValues[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Only register a click if the sensor is enabled
						if (droneApp.myDrone.quickStatus(qsSensors[counter])) {
							Intent myIntent = new Intent(getApplicationContext(), GraphActivity.class);
							myIntent.putExtra("SensorName", sensorNames[counter]);
							myIntent.putExtra("quickInt", qsSensors[counter]);
							startActivity(myIntent);
						} else {
							//
						}

					}
				});

				toggleButtons[i].setLayoutParams(tbLayout); // Set the Layout of our ToggleButton
				//toggleButtons[i].setChecked(droneApp.myDrone.quickStatus(qsSensors[i])); // Set it clicked if the sensor is already enabled


				/*
				 * Add all of our UI elements to the TableRow.
				 * (Order is important!)
				 */
				sensorRow[i].addView(toggleButtons[i]);
				sensorRow[i].addView(tvLabel[i]);
				sensorRow[i].addView(tvSensorValues[i]);

				/*
				 * Set up our ToggleButtons to turn on/off our sensors and start psuedo-streaming
				 */
				toggleButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					/*
					 * The general behavior of the program is as follows:
					 * 
					 * When a sensor is enabled:
					 * 1) When this button is toggled, it executes the Drone qsEnable method 
					 * for the sensor. This updates the sensors status, and triggers its section
					 * of the DroneStatusListener. It also sets up the myStreamer object used 
					 * to make measurements at a specified interval.
					 * 2) When the DroneStatusListener is triggered, it runs the sensor's measurement method.
					 * When the measurement comes back, it triggers the DroneEventListener section 
					 * for that sensor. There, it updates the display with it's value, and uses the myStreamer
					 * handler to ask for a measurement again automatically at the defined interval.
					 * 2-b) This repeats until the myStreamer object is disabled.
					 * 
					 * When a sensor is disabled:
					 * 1) The mySteamer object is stopped, preventing more measurements from being requested.
					 * The Drone qsDisable method is called for the appropriate sensor (This also triggers
					 * the corresponding DroneStatusEvent!).
					 */

					/*
					 * Turn the sensors on/off
					 */
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// If the Sensordrone is not connected, don't allow toggling of the sensors
						if (!droneApp.myDrone.isConnected) {
							toggleButtons[counter].setChecked(false);
						} else {
							if (toggleButtons[counter].isChecked() ) {


								// Enable our steamer
								streamerArray[counter].enable();
								// Enable the sensor
								droneApp.myDrone.quickEnable(qsSensors[counter]);

							} else {
								// Stop taking measurements
								streamerArray[counter].disable();

								// Disable the sensor
								droneApp.myDrone.quickDisable(qsSensors[counter]);

							}
						}
					}
				});
			}

			// Connection Status TextView
			tvConnectionStatus = new TextView(context);
			tvConnectionStatus.setText("Disconnected");
			tvConnectionStatus.setTextColor(Color.WHITE);
			tvConnectionStatus.setTextSize(18);
			tvConnectionStatus.setPadding(10, 10, 10, 10);
			tvConnectionStatus.setGravity(Gravity.CENTER);
			// Tell people how to connect
			tvConnectInfo = new TextView(context);
			tvConnectInfo.setText("Connect from your device's menu");
			tvConnectInfo.setTextColor(Color.WHITE);
			tvConnectInfo.setTextSize(18);
			tvConnectInfo.setPadding(10, 10, 10, 10);
			tvConnectInfo.setGravity(Gravity.CENTER);
			tvConnectInfo.setVisibility(TextView.VISIBLE);
			

			// Our top Picture Thing
			logoLayout = new LinearLayout(context);
			logoText = new TextView(context);
			logoText.setBackgroundColor(Color.BLACK);
			logoText.setTextColor(Color.WHITE);
			logoText.setText("Sensordrone\t\t\nControl");
			logoText.setTextSize(22);
			logoImage = new ImageView(context);
			Drawable img = getResources().getDrawable(R.drawable.apollo1725);
			logoImage.setImageDrawable(img);
			logoLayout.addView(logoText);
			logoLayout.addView(logoImage);
			logoLayout.setGravity(Gravity.CENTER);
			logoLayout.setBackgroundResource(R.drawable.logogradient);
			logoLayout.setPadding(10,10,10,10);
			
			// Measuring battery voltage is not part of the API's quickSyetem, so we will have
			// to set up a table row manually here
			bvRow = new TableRow(context);
			bvRow.setLayoutParams(trLayout);
			bvRow.setPadding(10, 10, 10, 0);
			bvToggle = new ToggleButton(context);
			bvToggle.setLayoutParams(tbLayout);
			bvLabel = new TextView(context);
			bvLabel.setLayoutParams(tvLayout);
			bvLabel.setText("Battery Voltage");
			bvValue = new TextView(context);
			bvValue.setBackgroundResource(R.drawable.valuegradient);
			bvValue.setTextColor(Color.WHITE);
			bvValue.setGravity(Gravity.CENTER);
			bvValue.setLayoutParams(tvLayout);
			bvValue.setText("--");
			
			
			// Use our Battery Streamer from the SDHelper library
			final SDBatteryStreamer bvStreamer = new SDBatteryStreamer(droneApp.myDrone);
			
			// Set up our graphing
			bvValue.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Only graph if the toggle button is checked
					if (bvToggle.isChecked()){
						Intent myIntent = new Intent(getApplicationContext(), GraphActivity.class);
						myIntent.putExtra("SensorName", "Battery Voltage");
						// We'll use a made-up number outside of the range of the quickSystem
						// that we can parse for the battery voltage
						myIntent.putExtra("quickInt", 42); 
						startActivity(myIntent);
					}
				}
			});
			
			// Set up our toggle button
			bvToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// Don't do anything if not connected
					if (!droneApp.myDrone.isConnected) {
						bvToggle.setChecked(false);
					} else {
						if (bvToggle.isChecked() ) {
							// Enable our steamer
							bvStreamer.enable();
							// Measure the voltage once to trigger streaming
							droneApp.myDrone.measureBatteryVoltage();

						} else {
							// Stop taking measurements
							bvStreamer.disable();
							
						}
					}	
				}
			});
			
			// Add it all to the row. We'll add the row to the main layout in onCreate
			bvRow.addView(bvToggle);
			bvRow.addView(bvLabel);
			bvRow.addView(bvValue);
			

			/*
			 * Let's set up our Drone Event Listener.
			 * 
			 * See adcMeasured for the general flow for when a sensor is measured.
			 * 
			 */
			deListener = new DroneEventListener() {

				@Override
				public void adcMeasured(EventObject arg0) {
					// This is triggered the the external ADC pin is measured
					
					// Update our display with the measured value
					tvUpdate(tvSensorValues[7], String.format("%.3f", droneApp.myDrone.externalADC_Volts) + " V");
					// Ask for another measurement 
					// (droneApp.streamingRate has been set to 1 second, so every time the ADC is measured
					// it will measure again in one second)
					streamerArray[7].streamHandler.postDelayed(streamerArray[7], droneApp.streamingRate);
				}

				@Override
				public void altitudeMeasured(EventObject arg0) {
					int pref = sdcPreferences.getInt(SDPreferences.ALTITUDE_UNIT, SDPreferences.FEET);
					if (pref == SDPreferences.FEET) {
						tvUpdate(tvSensorValues[8], String.format("%.0f", droneApp.myDrone.altitude_Feet) + " Ft");
					} else if (pref == SDPreferences.MILES) {
						tvUpdate(tvSensorValues[8], String.format("%.02f", droneApp.myDrone.altitude_Feet * 0.000189394) + " Mi");
					} else if (pref == SDPreferences.METER) {
						tvUpdate(tvSensorValues[8], String.format("%.0f", droneApp.myDrone.altitude_Meters) + " m");
					} else if (pref == SDPreferences.KILOMETER) {
						tvUpdate(tvSensorValues[8], String.format("%.03f", droneApp.myDrone.altitude_Meters / 1000) + " km");
					}
					
					streamerArray[8].streamHandler.postDelayed(streamerArray[8], droneApp.streamingRate);

				}

				@Override
				public void capacitanceMeasured(EventObject arg0) {
					tvUpdate(tvSensorValues[6], String.format("%.0f", droneApp.myDrone.capacitance_femtoFarad) + " fF");
					streamerArray[6].streamHandler.postDelayed(streamerArray[6], droneApp.streamingRate);

				}

				@Override
				public void connectEvent(EventObject arg0) {
					
					// Since we are adding SharedPreferences to store unit preferences,
					// we might as well store the last MAC there. Now we can press re-connect
					// to always try and connect to the last Drone (not just the last one per
					// app instance)
					
					Editor prefEditor = sdcPreferences.edit();
					prefEditor.putString(SDPreferences.LAST_MAC, droneApp.myDrone.lastMAC);
					prefEditor.commit();
					
					// Things to do when we connect to a Sensordrone
					quickMessage("Connected!");
					tvUpdate(tvConnectionStatus, "Connected to: " + droneApp.myDrone.lastMAC);
					// Flash teh LEDs green
					myHelper.flashLEDs(droneApp.myDrone, 3, 100, 0, 255, 0);
					// Turn on our blinker
					myBlinker.enable();
					myBlinker.run();
					// People don't need to know how to connect if they are already connected
					box.tvConnectInfo.setVisibility(TextView.INVISIBLE);
					// Notify if there is a low battery
					lowbatNotify = true;
				}

				@Override
				public void connectionLostEvent(EventObject arg0) {

					// Things to do if we think the connection has been lost.
					
					// Turn off the blinker
					myBlinker.disable();

					// notify the user
					tvUpdate(tvConnectionStatus, "Connection Lost!"); 
					quickMessage("Connection lost! Trying to re-connect!");

					// Try to reconnect once, automatically
					if (droneApp.myDrone.btConnect(droneApp.myDrone.lastMAC)) {
						// A brief pause
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						connectionLostReconnect();
					} else {
						quickMessage("Re-connect failed");
						doOnDisconnect();
					}
				}

				@Override
				public void customEvent(EventObject arg0) {

				}

				@Override
				public void disconnectEvent(EventObject arg0) {
					// notify the user
					quickMessage("Disconnected!");	
					tvConnectionStatus.setText("Disconnected");
				}

				@Override
				public void oxidizingGasMeasured(EventObject arg0) {

				}

				@Override
				public void reducingGasMeasured(EventObject arg0) {

				}

				@Override
				public void humidityMeasured(EventObject arg0) {
					tvUpdate(tvSensorValues[1], String.format("%.1f", droneApp.myDrone.humidity_Percent) + " %");
					streamerArray[1].streamHandler.postDelayed(streamerArray[1], droneApp.streamingRate);

				}

				@Override
				public void i2cRead(EventObject arg0) {

				}

				@Override
				public void irTemperatureMeasured(EventObject arg0) {
					int pref = sdcPreferences.getInt(SDPreferences.IR_TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
					if (pref == SDPreferences.FARENHEIT) {
						tvUpdate(tvSensorValues[3], String.format("%.1f", droneApp.myDrone.irTemperature_Farenheit) + " \u00B0F");
					} else if (pref == SDPreferences.CELCIUS) {
						tvUpdate(tvSensorValues[3], String.format("%.1f", droneApp.myDrone.irTemperature_Celcius) + " \u00B0C");
					} else if (pref == SDPreferences.KELVIN) {
						tvUpdate(tvSensorValues[3], String.format("%.1f", droneApp.myDrone.irTemperature_Kelvin) + " K");
					}
					streamerArray[3].streamHandler.postDelayed(streamerArray[3], droneApp.streamingRate);

				}

				@Override
				public void precisionGasMeasured(EventObject arg0) {
					tvUpdate(tvSensorValues[5], String.format("%.1f", droneApp.myDrone.precisionGas_ppmCarbonMonoxide) + " ppm");
					streamerArray[5].streamHandler.postDelayed(streamerArray[5], droneApp.streamingRate);

				}

				@Override
				public void pressureMeasured(EventObject arg0) {
					int pref = sdcPreferences.getInt(SDPreferences.PRESSURE_UNIT, SDPreferences.PASCAL);
					if (pref == SDPreferences.PASCAL) {
						tvUpdate(tvSensorValues[2], String.format("%.0f", droneApp.myDrone.pressure_Pascals) + " Pa");
					} else if (pref == SDPreferences.KILOPASCAL) {
						tvUpdate(tvSensorValues[2], String.format("%.2f", droneApp.myDrone.pressure_Pascals / 1000) + " kPa");
					} else if (pref == SDPreferences.ATMOSPHERE) {
						tvUpdate(tvSensorValues[2], String.format("%.2f", droneApp.myDrone.pressure_Atmospheres) + " Atm");
					} else if (pref == SDPreferences.MMHG) {
						tvUpdate(tvSensorValues[2], String.format("%.0f", droneApp.myDrone.pressure_Torr) + " mmHg");
					} else if (pref == SDPreferences.INHG) {
						tvUpdate(tvSensorValues[2], String.format("%.2f", droneApp.myDrone.pressure_Torr * 0.0393700732914) + " inHg");
					}
					streamerArray[2].streamHandler.postDelayed(streamerArray[2], droneApp.streamingRate);

				}

				@Override
				public void rgbcMeasured(EventObject arg0) {
					// The Lux value is calibrated for a (mostly) broadband light source.
					// Pointing it at a narrow band light source (like and LED) 
					// will bias the color channels, and provide a "wonky" number. 
					// Just for a nice look, we won't show a negative number.
					String msg = "";
					if (droneApp.myDrone.rgbcLux >= 0) {
						msg = String.format("%.0f", droneApp.myDrone.rgbcLux) + " Lux";
					} else {
						msg = String.format("%.0f", 0.0) + " Lux";
					}
					tvUpdate(tvSensorValues[4], msg);
					streamerArray[4].streamHandler.postDelayed(streamerArray[4], droneApp.streamingRate);

				}

				@Override
				public void temperatureMeasured(EventObject arg0) {
					int pref = sdcPreferences.getInt(SDPreferences.TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
					if (pref == SDPreferences.FARENHEIT) {
						tvUpdate(tvSensorValues[0], String.format("%.1f", droneApp.myDrone.temperature_Farenheit) + "  \u00B0F");
					} else if (pref == SDPreferences.CELCIUS) {
						tvUpdate(tvSensorValues[0], String.format("%.1f", droneApp.myDrone.temperature_Celcius) + "  \u00B0C");
					} else if (pref == SDPreferences.KELVIN) {
						// There is an error in SDAndroidLib-1.1.1
						// It converts Kelvin by subtracting 273.15 from the Celcius value (instead of adding).
						// This will be fixed in the library in the future, but we fix it here for now
						tvUpdate(tvSensorValues[0], String.format("%.1f", droneApp.myDrone.temperature_Kelvin + 273.15 + 273.15) + "  K");
					}
					streamerArray[0].streamHandler.postDelayed(streamerArray[0], droneApp.streamingRate);

				}

				@Override
				public void uartRead(EventObject arg0) {

				}

				@Override
				public void unknown(EventObject arg0) {

				}

				@Override
				public void usbUartRead(EventObject arg0) {

				}
			};


			/*
			 * Set up our status listener
			 * 
			 * see adcStatus for the general flow for sensors.
			 */
			dsListener = new DroneStatusListener() {


				@Override
				public void adcStatus(EventObject arg0) {
					// This is triggered when the status of the external ADC has been
					// enable, disabled, or checked.
					
					// If status has been triggered to true (on)
					if (droneApp.myDrone.adcStatus) {
						// then start the streaming by taking the first measurement
						streamerArray[7].run();
					}
					// Don't do anything if false (off)
				}

				@Override
				public void altitudeStatus(EventObject arg0) {
					if (droneApp.myDrone.altitudeStatus) {
						streamerArray[8].run();
					}

				}

				@Override
				public void batteryVoltageStatus(EventObject arg0) {
					// This is triggered when the battery voltage has been measured.
					String bVoltage = String.format("%.2f", droneApp.myDrone.batteryVoltage_Volts) + " V";
					tvUpdate(bvValue, bVoltage);
					// Set up the next measurement
					bvStreamer.streamHandler.postDelayed(bvStreamer, droneApp.streamingRate);
				}

				@Override
				public void capacitanceStatus(EventObject arg0) {
					if (droneApp.myDrone.capacitanceStatus) {
						streamerArray[6].run();
					}
				}

				@Override
				public void chargingStatus(EventObject arg0) {


				}

				@Override
				public void customStatus(EventObject arg0) {


				}

				@Override
				public void humidityStatus(EventObject arg0) {
					if (droneApp.myDrone.humidityStatus) {
						streamerArray[1].run();
					}

				}

				@Override
				public void irStatus(EventObject arg0) {
					if (droneApp.myDrone.irTemperatureStatus) {
						streamerArray[3].run();
					}

				}

				@Override
				public void lowBatteryStatus(EventObject arg0) {
					// If we get a low battery, notify the user
					// and disconnect
					
					// This might trigger a lot (making a call the the LEDS will trigger it,
					// so the myBlinker will trigger this once a second.
					// calling myBlinker.disable() even sets LEDS off, which will trigger it...
					if (lowbatNotify) {
						lowbatNotify = false; // Set true again in connectEvent
						myBlinker.disable();
						doOnDisconnect(); // run our disconnect routine
						// Notify the user
						tvUpdate(tvConnectionStatus, "Low Battery: Disconnected!");
						myInfo.lowBattery();
					}

				}

				@Override
				public void oxidizingGasStatus(EventObject arg0) {


				}

				@Override
				public void precisionGasStatus(EventObject arg0) {
					if (droneApp.myDrone.precisionGasStatus) {
						streamerArray[5].run();
					}

				}

				@Override
				public void pressureStatus(EventObject arg0) {
					if (droneApp.myDrone.pressureStatus) {
						streamerArray[2].run();
					}

				}

				@Override
				public void reducingGasStatus(EventObject arg0) {


				}

				@Override
				public void rgbcStatus(EventObject arg0) {
					if (droneApp.myDrone.rgbcStatus) {
						streamerArray[4].run();
					}

				}

				@Override
				public void temperatureStatus(EventObject arg0) {
					if (droneApp.myDrone.temperatureStatus) {
						streamerArray[0].run();
					}

				}

				@Override
				public void unknownStatus(EventObject arg0) {


				}
			};
			

			/*
			 * Register the listeners
			 * 
			 * This is done once on create. Disable them in onDestroy
			 */
			droneApp.myDrone.registerDroneEventListener(deListener);
			droneApp.myDrone.registerDroneStatusListener(dsListener);
			

		} // Constructor



	}

	/*
	 * Our program will need one of these classes
	 */
	public Storage box;

	/*
	 * We use this so we can restore our data. Note that this has been deprecated as of 
	 * Android API 13. The official Android Developer's recommendation is 
	 * if you are targeting HONEYCOMB or later, consider instead using a 
	 * Fragment with Fragment.setRetainInstance(boolean)
	 * (Also available via the android-support libraries for older versions)
	 */
	@Override
	public Storage onRetainNonConfigurationInstance() {
		
		// Make a new Storage object from our old data
		Storage bin = box;
		// Return our old data
		return bin;
	}

	/*
	 * We will use some stuff from our Sensordrone Helper library
	 */
	public SDHelper myHelper = new SDHelper();

	
	@Override
	public void onDestroy() {
		super.onDestroy();


		if (isFinishing()) {
			// Try and nicely shut down
			doOnDisconnect();
			// A brief delay
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Unregister the listener
			droneApp.myDrone.unregisterDroneEventListener(box.deListener);
			droneApp.myDrone.unregisterDroneStatusListener(box.dsListener);

		} else { 
			//It's an orientation change.
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get out Application so we have access to our Drone
		droneApp = (DroneApplication)getApplication();

		// Set up out AlertInfo
		myInfo = new AlertInfo(this);

		// Initialize SharedPreferences
		sdcPreferences  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		
		/*
		 * If we have destroyed and recreated our activity, due to something like
		 * and orientation change, this will restore it.
		 * 
		 * We want to restore it, because our Drone object remembers important things...
		 * like if it was connected or not.
		 */
		box = (Storage) getLastNonConfigurationInstance();

		/*
		 * But what if this is the first time the app has loaded?
		 */
		if (box != null) {
			// Remove the (old) views so we can re-add them
			box.onOffLayout.removeAllViews();
			// It's very important that this get called again
			box.onOffLayout = (TableLayout)findViewById(R.id.tlOnOff);
			// Add in our top image
			box.onOffLayout.addView(box.logoLayout);
			// Re-add the TableRows
			for (int i=0; i < box.numberOfSensors; i++){
				box.onOffLayout.addView(box.sensorRow[i]);
			}
			box.onOffLayout.addView(box.bvRow);
			box.onOffLayout.addView(box.tvConnectionStatus);
			box.onOffLayout.addView(box.tvConnectInfo);
		}
		if (box == null) {

			// Set up a new box, and all of it's objects
			box = new Storage(this, (TableLayout)findViewById(R.id.tlOnOff));
			// Add in our top image
			box.onOffLayout.addView(box.logoLayout);
			// Add the TableRows to the TableLayout
			for (int i=0; i < box.numberOfSensors; i++){
				box.onOffLayout.addView(box.sensorRow[i]);
			}
			box.onOffLayout.addView(box.bvRow);
			box.onOffLayout.addView(box.tvConnectionStatus);
			box.onOffLayout.addView(box.tvConnectInfo);
		}

		// Set up our background
		Drawable bgGradient = getResources().getDrawable(R.drawable.tablegradient);
		box.onOffLayout.setBackgroundDrawable(bgGradient);
		box.onOffLayout.setPadding(10, 10, 10, 10);


	}

	/*
	 * A function to display Toast Messages.
	 * 
	 * By having it run on the UI thread, we will be sure that the message
	 * is displays no matter what thread tries to use it.
	 */
	public void quickMessage(final String msg) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});

	}

	/*
	 * A function to update a TextView
	 * 
	 * We have it run on the UI thread to make sure it safely updates.
	 */
	public void tvUpdate(final TextView tv, final String msg) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tv.setText(msg);
			}
		});
	}


	/**
	 * Things to do when we disconnect
	 */
	public void doOnDisconnect() {
		// Shut off any sensors that are on
		SensordroneControl.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				// Turn off myBlinker
				box.myBlinker.disable();
				
				// Make sure the LEDs go off
				if (droneApp.myDrone.isConnected) {
					droneApp.myDrone.setLEDs(0, 0, 0);
				}
				
				// Toggle all of our buttons from On to Off
				for (int i=0; i < box.numberOfSensors; i++) {
					// If it is on
					if (box.toggleButtons[i].isChecked()) {
						// Turn it off
						box.toggleButtons[i].performClick();
					}
				}
				
				// Don't forget the battery voltage button
				if (box.bvToggle.isChecked()){
					box.bvToggle.performClick();
				}

				// Only try and disconnect if already connected
				if (droneApp.myDrone.isConnected) {
					droneApp.myDrone.disconnect();
				}

				// Remind people how to connect
				box.tvConnectInfo.setVisibility(TextView.VISIBLE);
			}
		});


	}

	// Stuff to do when we're trying to reconnect on connection lost
	public void connectionLostReconnect() {
		// Re-Toggle and sensors that were on
		SensordroneControl.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				for (int i=0; i < box.numberOfSensors; i++) {
					// If it is on
					if (box.toggleButtons[i].isChecked()) {
						// Turn it off and back on
						// This will trigger a measurement which will
						// get the psuedo streaming going again
						box.toggleButtons[i].performClick();
						box.toggleButtons[i].performClick();
					}
				}
				// Don't forget the battery voltage button
				if (box.bvToggle.isChecked()){
					box.bvToggle.performClick();
					box.bvToggle.performClick();
				}
			}
		});
	}



	/*
	 * We will handle connect and disconnect in the menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menuConnect:
			if (!droneApp.myDrone.isConnected) {
				// This option is used to re-connect to the last connected MAC
				// from our SharedPreferences
				String prefLastMAC = sdcPreferences.getString(SDPreferences.LAST_MAC, "");
				if (!prefLastMAC.equals("")) {
					if (!droneApp.myDrone.btConnect(prefLastMAC)) {
						myInfo.connectFail();
					}
				} else {
					// Notify the user if no previous MAC was found.
					quickMessage("Last MAC not found... Please scan");
				} 
			} else {
				quickMessage("Already connected...");
			}
			break;

		case R.id.menuDisconnect:
			// Only disconnect if it's connected
			if (droneApp.myDrone.isConnected) {
				// Run our routine of things to do on disconnect
				doOnDisconnect();
			} else {
				quickMessage("Not currently connected..");
			}
			break;
		case R.id.menuScan:
			if (!droneApp.myDrone.isConnected) {
				myHelper.scanToConnect(droneApp.myDrone, SensordroneControl.this , this, false);
			} else {
				quickMessage("Please disconnect first");
			}
			break;
			
		case R.id.unitPrefs:
			Intent unitIntent = new Intent(getApplicationContext(), PrefsActivity.class);
			startActivity(unitIntent);
			break;

			
			//Help Menu items
		case R.id.infoConnections:
			myInfo.connectionInfo();
			break;
		case R.id.infoData:
			myInfo.dataInfo();
			break;
		case R.id.infoGraphing:
			myInfo.graphingInfo();
			break;
		case R.id.infoSensors:
			myInfo.sensorInfo();
			break;
		}
		return true;
	}

}
