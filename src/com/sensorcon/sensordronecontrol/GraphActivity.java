package com.sensorcon.sensordronecontrol;


import java.util.Arrays;
import java.util.EventObject;
import java.util.LinkedList;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.SimpleXYSeries.ArrayFormat;
import com.androidplot.xy.XYPlot;
import com.sensorcon.sensordrone.Drone.DroneEventListener;
import com.sensorcon.sensordrone.Drone.DroneStatusListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class GraphActivity extends Activity {

	/*
	 * Preferences for Units
	 */
	SharedPreferences unitPreferences;

	protected DroneApplication droneApp;
	private float valueToWatch;
	private int sensorToWatch;
	public int upperBound;
	public int lowerBound;
	public int currentUpper;
	public int currentLower;
	public int[] savedRange;

	/*
	 * We user our DroneEentListener to load data into the graph. Check
	 * temperatureMeasured() for the general idea of how it works.
	 */
	private DroneEventListener geListener = new DroneEventListener() {

		@Override
		public void usbUartRead(EventObject arg0) {


		}

		@Override
		public void unknown(EventObject arg0) {


		}

		@Override
		public void uartRead(EventObject arg0) {


		}

		@Override
		public void temperatureMeasured(EventObject arg0) {
			// If this is the sensor we are graphing
			if (sensorToWatch == droneApp.myDrone.QS_TYPE_TEMPERATURE) {
				// then set the variable
				int pref = unitPreferences.getInt(SDPreferences.TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
				if (pref == SDPreferences.FARENHEIT) {
					valueToWatch = droneApp.myDrone.temperature_Farenheit;
				} else if (pref == SDPreferences.CELCIUS) {
					valueToWatch = droneApp.myDrone.temperature_Celcius;
				} else if (pref == SDPreferences.KELVIN) {
					// There is an error in SDAndroidLib-1.1.1
					// It converts Kelvin by subtracting 273.15 from the Celcius value (instead of adding).
					// This will be fixed in the library in the future, but we fix it here for now
					valueToWatch = (float) (droneApp.myDrone.temperature_Kelvin + (273.15 * 2));
				}
				// and add the data to the graph
				addData(valueToWatch);
			}
		}

		@Override
		public void rgbcMeasured(EventObject arg0) {
			if (sensorToWatch == droneApp.myDrone.QS_TYPE_RGBC) {
				valueToWatch = droneApp.myDrone.rgbcLux;
				addData(valueToWatch);
			}
		}

		@Override
		public void pressureMeasured(EventObject arg0) {
			if (sensorToWatch == droneApp.myDrone.QS_TYPE_PRESSURE) {
				
				int pref = unitPreferences.getInt(SDPreferences.PRESSURE_UNIT, SDPreferences.PASCAL);
				if (pref == SDPreferences.PASCAL) {
					valueToWatch = droneApp.myDrone.pressure_Pascals;
				} else if (pref == SDPreferences.KILOPASCAL) {
					valueToWatch = droneApp.myDrone.pressure_Pascals / 1000;
				} else if (pref == SDPreferences.ATMOSPHERE) {
					valueToWatch = droneApp.myDrone.pressure_Atmospheres;
				} else if (pref == SDPreferences.MMHG) {
					valueToWatch = droneApp.myDrone.pressure_Torr;
				} else if (pref == SDPreferences.INHG) {
					valueToWatch = (float) (droneApp.myDrone.pressure_Torr * 0.0393700732914);
				}
				
				addData(valueToWatch);
			}
		}

		@Override
		public void precisionGasMeasured(EventObject arg0) {
			if (sensorToWatch == droneApp.myDrone.QS_TYPE_PRECISION_GAS) {
				valueToWatch = droneApp.myDrone.precisionGas_ppmCarbonMonoxide;
				addData(valueToWatch);
			}
		}

		@Override
		public void irTemperatureMeasured(EventObject arg0) {
			if (sensorToWatch == droneApp.myDrone.QS_TYPE_IR_TEMPERATURE) {
				int pref = unitPreferences.getInt(SDPreferences.IR_TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
				if (pref == SDPreferences.FARENHEIT) {
					valueToWatch = droneApp.myDrone.irTemperature_Farenheit;
				} else if (pref == SDPreferences.CELCIUS) {
					valueToWatch = droneApp.myDrone.irTemperature_Celcius;
				} else if (pref == SDPreferences.KELVIN) {
					valueToWatch = droneApp.myDrone.irTemperature_Kelvin;
				}
				addData(valueToWatch);
			}
		}

		@Override
		public void i2cRead(EventObject arg0) {


		}

		@Override
		public void humidityMeasured(EventObject arg0) {
			if (sensorToWatch == droneApp.myDrone.QS_TYPE_HUMIDITY) {	
				valueToWatch = droneApp.myDrone.humidity_Percent;
				addData(valueToWatch);
			}
		}

		@Override
		public void reducingGasMeasured(EventObject arg0) {


		}

		@Override
		public void oxidizingGasMeasured(EventObject arg0) {


		}

		@Override
		public void disconnectEvent(EventObject arg0) {


		}

		@Override
		public void customEvent(EventObject arg0) {


		}

		@Override
		public void connectionLostEvent(EventObject arg0) {


		}

		@Override
		public void connectEvent(EventObject arg0) {


		}

		@Override
		public void capacitanceMeasured(EventObject arg0) {
			if (sensorToWatch == droneApp.myDrone.QS_TYPE_CAPACITANCE) {
				valueToWatch = droneApp.myDrone.capacitance_femtoFarad;
				addData(valueToWatch);
			}
		}

		@Override
		public void altitudeMeasured(EventObject arg0) {

			if (sensorToWatch == droneApp.myDrone.QS_TYPE_ALTITUDE) {
				int pref = unitPreferences.getInt(SDPreferences.ALTITUDE_UNIT, SDPreferences.FEET);
				if (pref == SDPreferences.FEET) {
					valueToWatch = droneApp.myDrone.altitude_Feet;
				} else if (pref == SDPreferences.MILES) {
					valueToWatch = (float) (droneApp.myDrone.altitude_Feet * 0.000189394);
				} else if (pref == SDPreferences.METER) {
					valueToWatch = droneApp.myDrone.altitude_Meters;
				} else if (pref == SDPreferences.KILOMETER) {
					valueToWatch = droneApp.myDrone.altitude_Meters / 1000;
				}
				
				
				addData(valueToWatch);
			}
		}

		@Override
		public void adcMeasured(EventObject arg0) {
			if (sensorToWatch == droneApp.myDrone.QS_TYPE_ADC) {
				valueToWatch =  droneApp.myDrone.externalADC_Volts;
				addData(valueToWatch);
			}
		}
	};

	// We'll need to use our DroneStatusListener for graphing battery voltage
	private DroneStatusListener gsListener = new DroneStatusListener() {
		
		@Override
		public void unknownStatus(EventObject arg0) {
			
		}
		
		@Override
		public void temperatureStatus(EventObject arg0) {
			
		}
		
		@Override
		public void rgbcStatus(EventObject arg0) {
			
		}
		
		@Override
		public void reducingGasStatus(EventObject arg0) {
			
		}
		
		@Override
		public void pressureStatus(EventObject arg0) {
			
		}
		
		@Override
		public void precisionGasStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void oxidizingGasStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void lowBatteryStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void irStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void humidityStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void customStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void chargingStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void capacitanceStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void batteryVoltageStatus(EventObject arg0) {
			// We hard-coded in 42 since battery voltage is not in the API's quickSystem
			if (sensorToWatch == 42) {
				valueToWatch =  droneApp.myDrone.batteryVoltage_Volts;
				addData(valueToWatch);
			}
		}
		
		@Override
		public void altitudeStatus(EventObject arg0) {
			
			
		}
		
		@Override
		public void adcStatus(EventObject arg0) {
			
			
		}
	};

	private static final int HISTORY_SIZE = 30; // Display last 30 measurements
	private XYPlot dronePlot = null;
	SimpleXYSeries droneValues = null;
	private LinkedList<Number> droneHistory;


	@Override
	protected void onDestroy() {
		super.onDestroy();
		// The listeners are registered on every create, so remove them on every destroy
		droneApp.myDrone.unregisterDroneEventListener(geListener);
		droneApp.myDrone.unregisterDroneStatusListener(gsListener);

		// If we are closing the graph, then we want to restore the Streaming Rate
		if (isFinishing()) {
			droneApp.streamingRate = droneApp.defaultRate;
		}
	}
	
	@Override
	public int[] onRetainNonConfigurationInstance() {
		// We use this so if we set the range, it doesn't reset on orientation change
		int []oldRange = new int[] {currentLower, currentUpper};
		return oldRange;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);

		// Use our Drone
		droneApp = (DroneApplication)getApplication();



		// Get info from the Intent that started us
		Intent myIntent = getIntent();
		String myLabel = myIntent.getStringExtra("SensorName");
		sensorToWatch = myIntent.getIntExtra("quickInt", 0);
		
		// Set up preferences for units
		unitPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		// Graph data
		droneHistory = new LinkedList<Number>();
		droneValues= new SimpleXYSeries(myLabel);

		// Graph formatting
		dronePlot = (XYPlot)findViewById(R.id.dynamicPlot);
		LineAndPointFormatter lF = new LineAndPointFormatter(Color.rgb(0, 0, 0), Color.rgb(0, 255, 0), null);
		dronePlot.addSeries(droneValues, lF);
		dronePlot.setDomainStepValue(1);
		dronePlot.setTicksPerDomainLabel(10);


		// Start with typical bounds for a particular sensor
		// These will be the default Range. Can be changed via the Menu
		if (sensorToWatch == droneApp.myDrone.QS_TYPE_TEMPERATURE) {
			int prefs = unitPreferences.getInt(SDPreferences.TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
			if (prefs == SDPreferences.FARENHEIT) {
				upperBound = 120;
				lowerBound = 20;
			} else if (prefs == SDPreferences.CELCIUS) {
				upperBound = 50;
				lowerBound = -7;
			} else if (prefs == SDPreferences.KELVIN) {
				upperBound = 322;
				lowerBound = 266;
			}

		} else if (sensorToWatch == droneApp.myDrone.QS_TYPE_HUMIDITY) {
			upperBound = 90;
			lowerBound = 10;
		} else if (sensorToWatch == droneApp.myDrone.QS_TYPE_PRESSURE) {
			int prefs = unitPreferences.getInt(SDPreferences.PRESSURE_UNIT, SDPreferences.PASCAL);
			if (prefs == SDPreferences.PASCAL) {
				upperBound = 100500;
				lowerBound = 98000;
			} else if ( prefs == SDPreferences.KILOPASCAL) {
				upperBound = 101;
				lowerBound = 98;
			} else if (prefs == SDPreferences.ATMOSPHERE) {
				upperBound = 1;
				lowerBound = 0;
			} else if (prefs == SDPreferences.MMHG) {
				upperBound = 757;
				lowerBound = 735;
			} else if (prefs == SDPreferences.INHG) {
				upperBound = 30;
				lowerBound = 28;
			}
			
		} else if (sensorToWatch == droneApp.myDrone.QS_TYPE_IR_TEMPERATURE) {
			int prefs = unitPreferences.getInt(SDPreferences.IR_TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
			if (prefs == SDPreferences.FARENHEIT) {
				upperBound = 120;
				lowerBound = 20;
			} else if (prefs == SDPreferences.CELCIUS) {
				upperBound = 50;
				lowerBound = -7;
			} else if (prefs == SDPreferences.KELVIN) {
				upperBound = 322;
				lowerBound = 266;
			}
		} else if (sensorToWatch == droneApp.myDrone.QS_TYPE_RGBC) {
			upperBound = 1000;
			lowerBound = 0;
		} else if (sensorToWatch == droneApp.myDrone.QS_TYPE_PRECISION_GAS) {
			upperBound = 60;
			lowerBound = 0;
		} else if (sensorToWatch == droneApp.myDrone.QS_TYPE_CAPACITANCE) {
			upperBound = 3000;
			lowerBound = 2000;
		} else if (sensorToWatch == droneApp.myDrone.QS_TYPE_ADC) {
			upperBound = 3;
			lowerBound = 0;
		} else if (sensorToWatch == droneApp.myDrone.QS_TYPE_ALTITUDE) {
			int prefs = unitPreferences.getInt(SDPreferences.ALTITUDE_UNIT, SDPreferences.FEET);
			if (prefs == SDPreferences.FEET) {
				upperBound = 1000;
				lowerBound = 300;
			} else if (prefs == SDPreferences.MILES) {
				upperBound = 1;
				lowerBound = 0;
			} else if (prefs == SDPreferences.METER) {
				upperBound = 305;
				lowerBound = 91;
			} else if (prefs == SDPreferences.KILOMETER) {
				upperBound = 1;
				lowerBound = 0;
			}
			
		} else if (sensorToWatch == 42) { // Hard coded value for battery voltage
			upperBound = 5;
			lowerBound = 3;
		} else {
			upperBound = 100;
			lowerBound = 0;
		}

		// Get the saved ranges (if any)
		savedRange = (int[]) getLastNonConfigurationInstance();
		
		if(savedRange != null) {
			// Load the saved ranges
			currentUpper = savedRange[1];
			currentLower = savedRange[0];
		} else {
			// Use default ranges
			currentUpper = upperBound;
			currentLower = lowerBound;
		}
		
		// Quickly pre-load 30 points on the graph (due to the way it ranges).
		initializeData(currentLower);

		// Set the graph boundary modes and X,Y labels
		dronePlot.setRangeLowerBoundary(currentLower, BoundaryMode.FIXED);
		dronePlot.setRangeUpperBoundary(currentUpper, BoundaryMode.FIXED);
		dronePlot.setDomainLabel("Last 30 Measurements");
		dronePlot.setRangeLabel("Sensor Value");

		// Register the listeners
		droneApp.myDrone.registerDroneEventListener(geListener);
		droneApp.myDrone.registerDroneStatusListener(gsListener);

	}

	// This updates our data and redraws the graph
	public void addData(float data) {


		Number[] numbers = {data};
		droneValues.setModel(Arrays.asList(numbers), ArrayFormat.Y_VALS_ONLY);
		// If our data size is larger than our history, remove the oldest.
		if(droneHistory.size() > HISTORY_SIZE) {
			droneHistory.removeFirst();
		}
		droneHistory.addLast(numbers[0]);
		droneValues.setModel(droneHistory, ArrayFormat.Y_VALS_ONLY);
		dronePlot.redraw();


	}

	/*
	 * Quickly load in some data. The graph grows/stretched until the max HISTRY_SIZE is reached,
	 * so we load in some points to keep it from looking strange at first.
	 */
	public void initializeData(float data) {
		for (int i=0; i < HISTORY_SIZE; i++) {
			Number[] numbers = {data};
			droneValues.setModel(Arrays.asList(numbers), ArrayFormat.Y_VALS_ONLY);
			if(droneHistory.size() > HISTORY_SIZE) {
				droneHistory.removeFirst();
			}
			droneHistory.addLast(numbers[0]);
			droneValues.setModel(droneHistory, ArrayFormat.Y_VALS_ONLY);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.rate100:
			droneApp.streamingRate = 100; //(milliseconds)
			break;
		case R.id.rate300:
			droneApp.streamingRate = 300; //(milliseconds)
			break;
		case R.id.rate1000:
			droneApp.streamingRate = 1000; //(milliseconds)
			break;
		case R.id.menuUpper:
			changeUpperRange();
			break;
		case R.id.menuLower:
			changeLowerRange();
			break;
		case R.id.menuTip:
			infoPopUp();
			break;

		}
		return true;
	}

	/*
	 * A method to change the upper range of the graph
	 */
	public void changeUpperRange() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Upper Range");
		alert.setMessage("Set upper limit");

		// Set an EditText view to get user input 
		final EditText upper = new EditText(this);
		// Allow negative numbers by adding | InputType.TYPE_NUMBER_FLAG_SIGNED
		upper.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		alert.setView(upper);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					currentUpper = Integer.parseInt(upper.getText().toString());
				} catch(NumberFormatException nfe) {
					System.out.println("Could not parse " + nfe);
				}

				if (currentUpper > currentLower) {
					dronePlot.setRangeUpperBoundary(currentUpper, BoundaryMode.FIXED);
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do Nothing
			}
		});

		alert.show();
	}
	
	/*
	 * A method to change the lower range of the graph.
	 */
	public void changeLowerRange() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Lower Range");
		alert.setMessage("Set lower limit");

		// Set an EditText view to get user input 
		final EditText lower = new EditText(this);
		// Allow negative numbers by adding | InputType.TYPE_NUMBER_FLAG_SIGNED
		lower.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		alert.setView(lower);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					currentLower = Integer.parseInt(lower.getText().toString());
				} catch(NumberFormatException nfe) {
				}

				if (currentLower < currentUpper) {
					dronePlot.setRangeLowerBoundary(currentLower, BoundaryMode.FIXED);
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do Nothing
			}
		});

		alert.show();
	}
	
	/*
	 * Display a tip about graphing performance.
	 */
	public void infoPopUp() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Tip");
		alert.setMessage("For the fastest data rate, ensure only this sensor is enabled before graphing");


		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
	}
	
	
}
