package com.sensorcon.sensordronecontrol;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class PrefsActivity extends Activity {

	SharedPreferences unitPreferences;
	Editor prefEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs);

		unitPreferences  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		prefEditor = unitPreferences.edit();

		/*
		 * Get current values
		 */
		int currentTemperatureUnit = unitPreferences.getInt(SDPreferences.TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
		int currentPressureUnit = unitPreferences.getInt(SDPreferences.PRESSURE_UNIT, SDPreferences.PASCAL);
		int currentIRTemperatureUnit = unitPreferences.getInt(SDPreferences.IR_TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
		int currentAltitudeUnit = unitPreferences.getInt(SDPreferences.ALTITUDE_UNIT, SDPreferences.FEET);

		LinearLayout prefLayout = (LinearLayout)findViewById(R.id.prefLayout); 


		/*
		 *  Ambient Temperature
		 */
		TextView tempPref = new TextView(getApplicationContext());
		tempPref.setText("Ambient Temperature");
		prefLayout.addView(tempPref);
		// Radio Buttons
		RadioGroup temperatureGroup = new RadioGroup(getApplicationContext());
		RadioButton tempCelcius = new RadioButton(getApplicationContext());
		tempCelcius.setText("Celcius");
		tempCelcius.setTextColor(Color.BLACK);
		RadioButton tempFareneit = new RadioButton(getApplicationContext());
		tempFareneit.setText("Farenheit");
		tempFareneit.setTextColor(Color.BLACK);
		RadioButton tempKelvin = new RadioButton(getApplicationContext());
		tempKelvin.setText("Kelvin");
		tempKelvin.setTextColor(Color.BLACK);
		temperatureGroup.addView(tempFareneit);
		temperatureGroup.addView(tempCelcius);
		temperatureGroup.addView(tempKelvin);
		prefLayout.addView(temperatureGroup);
		// What is selected?
		if (currentTemperatureUnit == SDPreferences.FARENHEIT) {
			tempFareneit.setChecked(true);
		} else if (currentTemperatureUnit == SDPreferences.CELCIUS) {
			tempCelcius.setChecked(true);
		} else if (currentTemperatureUnit == SDPreferences.KELVIN) {
			tempKelvin.setChecked(true);
		}
		// On Select
		tempCelcius.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.TEMPERATURE_UNIT,SDPreferences.CELCIUS);
				prefEditor.commit();
				Log.d("PREFS", "Celcius");
			}
		});
		tempFareneit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
				prefEditor.commit();

				Log.d("PREFS", "Farenheit");
			}
		});
		tempKelvin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.TEMPERATURE_UNIT, SDPreferences.KELVIN);
				prefEditor.commit();
				Log.d("PREFS", "Kelvin");
			}
		});

		/*
		 * Pressure
		 */
		TextView pressureTV = new TextView(getApplicationContext());
		pressureTV.setText("Pressure Units");
		prefLayout.addView(pressureTV);
		RadioGroup pressureGroup = new RadioGroup(getApplicationContext());
		prefLayout.addView(pressureGroup);
		RadioButton presPascal = new RadioButton(getApplicationContext());
		presPascal.setText("Pacal");
		presPascal.setTextColor(Color.BLACK);
		RadioButton presKPascal = new RadioButton(getApplicationContext());
		presKPascal.setText("KiloPascal");
		presKPascal.setTextColor(Color.BLACK);
		RadioButton presAtm = new RadioButton(getApplicationContext());
		presAtm.setText("Atmosphere");
		presAtm.setTextColor(Color.BLACK);
		RadioButton presmmHg = new RadioButton(getApplicationContext());
		presmmHg.setText("mm Hg");
		presmmHg.setTextColor(Color.BLACK);
		RadioButton presInHg = new RadioButton(getApplicationContext());
		presInHg.setText("In Hg");
		presInHg.setTextColor(Color.BLACK);
		pressureGroup.addView(presPascal);
		pressureGroup.addView(presKPascal);
		pressureGroup.addView(presAtm);
		pressureGroup.addView(presmmHg);
		pressureGroup.addView(presInHg);
		// Which one is already selected?
		if (currentPressureUnit == SDPreferences.PASCAL) {
			presPascal.setChecked(true);
		} else if (currentPressureUnit == SDPreferences.KILOPASCAL) {
			presKPascal.setChecked(true);
		} else if (currentPressureUnit == SDPreferences.ATMOSPHERE) {
			presAtm.setChecked(true);
		} else if (currentPressureUnit == SDPreferences.MMHG) {
			presmmHg.setChecked(true);
		} else if (currentPressureUnit == SDPreferences.INHG) {
			presInHg.setChecked(true);
		}

		// Listeners
		presPascal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT, SDPreferences.PASCAL);
				prefEditor.commit();
			}
		});
		presKPascal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT, SDPreferences.KILOPASCAL);
				prefEditor.commit();
			}
		});
		presAtm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT, SDPreferences.ATMOSPHERE);
				prefEditor.commit();
			}
		});
		presmmHg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT, SDPreferences.MMHG);
				prefEditor.commit();
			}
		});
		presInHg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT, SDPreferences.INHG);
				prefEditor.commit();
			}
		});


		/*
		 * IR Temperature
		 */
		TextView irTV = new TextView(getApplicationContext());
		irTV.setText("IR Temperature");
		prefLayout.addView(irTV);
		RadioGroup irGroup = new RadioGroup(getApplicationContext());
		prefLayout.addView(irGroup);
		RadioButton irFarenheit = new RadioButton(getApplicationContext());
		irFarenheit.setText("Farenheit");
		irFarenheit.setTextColor(Color.BLACK);
		RadioButton irCelcius = new RadioButton(getApplicationContext());
		irCelcius.setText("Celcius");
		irCelcius.setTextColor(Color.BLACK);
		RadioButton irKelvin = new RadioButton(getApplicationContext());
		irKelvin.setText("Kelvin");
		irKelvin.setTextColor(Color.BLACK);
		irGroup.addView(irFarenheit);
		irGroup.addView(irCelcius);
		irGroup.addView(irKelvin);
		// What was selected?
		if (currentIRTemperatureUnit == SDPreferences.FARENHEIT) {
			irFarenheit.setChecked(true);
		} else if (currentIRTemperatureUnit == SDPreferences.CELCIUS) {
			irCelcius.setChecked(true);
		} else if (currentIRTemperatureUnit == SDPreferences.KELVIN) {
			irKelvin.setChecked(true);
		}

		// Set listeners
		irFarenheit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.IR_TEMPERATURE_UNIT, SDPreferences.FARENHEIT);
				prefEditor.commit();
			}
		});
		irCelcius.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.IR_TEMPERATURE_UNIT, SDPreferences.CELCIUS);
				prefEditor.commit();
			}
		});
		irKelvin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.IR_TEMPERATURE_UNIT, SDPreferences.KELVIN);
				prefEditor.commit();
			}
		});

		/*
		 * Altitude
		 */
		TextView altitudeTV = new TextView(getApplicationContext());
		altitudeTV.setText("Altitude");
		prefLayout.addView(altitudeTV);
		RadioGroup altGroup = new RadioGroup(getApplicationContext());
		prefLayout.addView(altGroup);
		RadioButton altFeet = new RadioButton(getApplicationContext());
		altFeet.setText("Feet");
		altFeet.setTextColor(Color.BLACK);
		RadioButton altMile = new RadioButton(getApplicationContext());
		altMile.setText("Mile");
		altMile.setTextColor(Color.BLACK);
		RadioButton altMeter = new RadioButton(getApplicationContext());
		altMeter.setText("Meter");
		altMeter.setTextColor(Color.BLACK);
		RadioButton altKmeter = new RadioButton(getApplicationContext());
		altKmeter.setText("Kilometer");
		altKmeter.setTextColor(Color.BLACK);
		altGroup.addView(altFeet);
		altGroup.addView(altMile);
		altGroup.addView(altMeter);
		altGroup.addView(altKmeter);
		// Which one was selected?
		if (currentAltitudeUnit == SDPreferences.FEET) {
			altFeet.setChecked(true);
		} else if (currentAltitudeUnit == SDPreferences.MILES) {
			altMile.setChecked(true);
		} else if (currentAltitudeUnit == SDPreferences.METER) {
			altMeter.setChecked(true);
		} else if (currentAltitudeUnit == SDPreferences.KILOMETER) {
			altKmeter.setChecked(true);
		}
		// Blurble
		altFeet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.ALTITUDE_UNIT, SDPreferences.FEET);
				prefEditor.commit();
			}
		});
		altMile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.ALTITUDE_UNIT, SDPreferences.MILES);
				prefEditor.commit();
			}
		});
		altMeter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.ALTITUDE_UNIT, SDPreferences.METER);
				prefEditor.commit();
			}
		});
		altKmeter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.ALTITUDE_UNIT, SDPreferences.KILOMETER);
				prefEditor.commit();
			}
		});






	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prefs, menu);
		return true;
	}

}
