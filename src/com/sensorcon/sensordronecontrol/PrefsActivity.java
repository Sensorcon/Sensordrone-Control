package com.sensorcon.sensordronecontrol;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class PrefsActivity extends Activity {

	/*
	 * Set up our Shared Preferences and its editor
	 */
	SharedPreferences unitPreferences;
	Editor prefEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs);

		/*
		 * Initialize the preferences and the editor
		 */
		unitPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		prefEditor = unitPreferences.edit();

		/*
		 * Get current values
		 */
		int currentTemperatureUnit = unitPreferences.getInt(
				SDPreferences.TEMPERATURE_UNIT, SDPreferences.FAHRENHEIT);
		int currentPressureUnit = unitPreferences.getInt(
				SDPreferences.PRESSURE_UNIT, SDPreferences.PASCAL);
		int currentIRTemperatureUnit = unitPreferences.getInt(
				SDPreferences.IR_TEMPERATURE_UNIT, SDPreferences.FAHRENHEIT);
		int currentAltitudeUnit = unitPreferences.getInt(
				SDPreferences.ALTITUDE_UNIT, SDPreferences.FEET);

		/*
		 * Get our main layout
		 */
		LinearLayout prefLayout = (LinearLayout) findViewById(R.id.prefLayout);

		// Formatting
		prefLayout.setBackgroundColor(Color.BLACK);
		int buttonTextColor = Color.WHITE;
		float propertyTextSize = 20;

		/*
		 * Ambient Temperature
		 */
		TextView temperatureTV = new TextView(getApplicationContext());
		temperatureTV.setText("Ambient Temperature");
		temperatureTV.setTextSize(propertyTextSize);
		prefLayout.addView(temperatureTV);
		// Radio Buttons
		RadioGroup temperatureGroup = new RadioGroup(getApplicationContext());
		RadioButton tempCelsius = new RadioButton(getApplicationContext());
		tempCelsius.setText("Celsius");
		tempCelsius.setTextColor(buttonTextColor);
		RadioButton tempFahrenheit = new RadioButton(getApplicationContext());
		tempFahrenheit.setText("Fahrenheit");
		tempFahrenheit.setTextColor(buttonTextColor);
		RadioButton tempKelvin = new RadioButton(getApplicationContext());
		tempKelvin.setText("Kelvin");
		tempKelvin.setTextColor(buttonTextColor);
		temperatureGroup.addView(tempFahrenheit);
		temperatureGroup.addView(tempCelsius);
		temperatureGroup.addView(tempKelvin);
		prefLayout.addView(temperatureGroup);
		// What is selected?
		if (currentTemperatureUnit == SDPreferences.FAHRENHEIT) {
			tempFahrenheit.setChecked(true);
		} else if (currentTemperatureUnit == SDPreferences.CELSIUS) {
			tempCelsius.setChecked(true);
		} else if (currentTemperatureUnit == SDPreferences.KELVIN) {
			tempKelvin.setChecked(true);
		}
		// On Select
		tempCelsius.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Shared preferences use a key, value system.
                // Our keys and values are statically defined in the
                // SDPreference class
                prefEditor.putInt(SDPreferences.TEMPERATURE_UNIT,
                        SDPreferences.CELSIUS);
                prefEditor.apply(); // Preferences aren't updated until there is
                // a commit/apply.
            }
        });
		tempFahrenheit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prefEditor.putInt(SDPreferences.TEMPERATURE_UNIT,
                        SDPreferences.FAHRENHEIT);
                prefEditor.apply();
            }
        });
		tempKelvin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.TEMPERATURE_UNIT,
						SDPreferences.KELVIN);
				prefEditor.apply();
			}
		});

		/*
		 * IR Temperature
		 */
		TextView irTV = new TextView(getApplicationContext());
		irTV.setText("IR Temperature");
		irTV.setTextSize(propertyTextSize);
		prefLayout.addView(irTV);
		// Radio buttons
		RadioGroup irGroup = new RadioGroup(getApplicationContext());
		prefLayout.addView(irGroup);
		RadioButton irFarenheit = new RadioButton(getApplicationContext());
		irFarenheit.setText("Farenheit");
		irFarenheit.setTextColor(buttonTextColor);
		RadioButton irCelcius = new RadioButton(getApplicationContext());
		irCelcius.setText("Celcius");
		irCelcius.setTextColor(buttonTextColor);
		RadioButton irKelvin = new RadioButton(getApplicationContext());
		irKelvin.setText("Kelvin");
		irKelvin.setTextColor(buttonTextColor);
		irGroup.addView(irFarenheit);
		irGroup.addView(irCelcius);
		irGroup.addView(irKelvin);
		// What was selected?
		if (currentIRTemperatureUnit == SDPreferences.FAHRENHEIT) {
			irFarenheit.setChecked(true);
		} else if (currentIRTemperatureUnit == SDPreferences.CELSIUS) {
			irCelcius.setChecked(true);
		} else if (currentIRTemperatureUnit == SDPreferences.KELVIN) {
			irKelvin.setChecked(true);
		}
		// Set listeners
		irFarenheit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.IR_TEMPERATURE_UNIT,
						SDPreferences.FAHRENHEIT);
				prefEditor.apply();
			}
		});
		irCelcius.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.IR_TEMPERATURE_UNIT,
						SDPreferences.CELSIUS);
				prefEditor.apply();
			}
		});
		irKelvin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.IR_TEMPERATURE_UNIT,
						SDPreferences.KELVIN);
				prefEditor.apply();
			}
		});

		/*
		 * Pressure
		 */
		TextView pressureTV = new TextView(getApplicationContext());
		pressureTV.setText("Pressure Units");
		pressureTV.setTextSize(propertyTextSize);
		prefLayout.addView(pressureTV);
		RadioGroup pressureGroup = new RadioGroup(getApplicationContext());
		prefLayout.addView(pressureGroup);
		RadioButton presPascal = new RadioButton(getApplicationContext());
		presPascal.setText("Pascal");
		presPascal.setTextColor(buttonTextColor);
        RadioButton presHPascal = new RadioButton(getApplicationContext());
        presHPascal.setText("Hectopascal");
        presHPascal.setTextColor(buttonTextColor);
		RadioButton presKPascal = new RadioButton(getApplicationContext());
		presKPascal.setText("Kilopascal");
		presKPascal.setTextColor(buttonTextColor);
		RadioButton presAtm = new RadioButton(getApplicationContext());
		presAtm.setText("Atmosphere");
		presAtm.setTextColor(buttonTextColor);
		RadioButton presmmHg = new RadioButton(getApplicationContext());
		presmmHg.setText("mmHg");
		presmmHg.setTextColor(buttonTextColor);
		RadioButton presInHg = new RadioButton(getApplicationContext());
		presInHg.setText("inHg");
		presInHg.setTextColor(buttonTextColor);
		pressureGroup.addView(presPascal);
        pressureGroup.addView(presHPascal);
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
		}  else if (currentPressureUnit == SDPreferences.HECTOPASCAL) {
            presHPascal.setChecked(true);
        }

		// Listeners
		presPascal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT,
						SDPreferences.PASCAL);
				prefEditor.apply();
			}
		});
        presHPascal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prefEditor.putInt(SDPreferences.PRESSURE_UNIT, SDPreferences.HECTOPASCAL);
                prefEditor.apply();
            }
        });
		presKPascal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT,
						SDPreferences.KILOPASCAL);
				prefEditor.apply();
			}
		});
		presAtm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT,
						SDPreferences.ATMOSPHERE);
				prefEditor.apply();
			}
		});
		presmmHg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT,
						SDPreferences.MMHG);
				prefEditor.apply();
			}
		});
		presInHg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.PRESSURE_UNIT,
						SDPreferences.INHG);
				prefEditor.apply();
			}
		});

		/*
		 * Altitude
		 */
		TextView altitudeTV = new TextView(getApplicationContext());
		altitudeTV.setText("Altitude");
		altitudeTV.setTextSize(propertyTextSize);
		prefLayout.addView(altitudeTV);
		// Radio buttons
		RadioGroup altGroup = new RadioGroup(getApplicationContext());
		prefLayout.addView(altGroup);
		RadioButton altFeet = new RadioButton(getApplicationContext());
		altFeet.setText("Feet");
		altFeet.setTextColor(buttonTextColor);
		RadioButton altMile = new RadioButton(getApplicationContext());
		altMile.setText("Mile");
		altMile.setTextColor(buttonTextColor);
		RadioButton altMeter = new RadioButton(getApplicationContext());
		altMeter.setText("Meter");
		altMeter.setTextColor(buttonTextColor);
		RadioButton altKmeter = new RadioButton(getApplicationContext());
		altKmeter.setText("Kilometer");
		altKmeter.setTextColor(buttonTextColor);
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
		// Listeners
		altFeet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.ALTITUDE_UNIT,
						SDPreferences.FEET);
				prefEditor.apply();
			}
		});
		altMile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.ALTITUDE_UNIT,
						SDPreferences.MILES);
				prefEditor.apply();
			}
		});
		altMeter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.ALTITUDE_UNIT,
						SDPreferences.METER);
				prefEditor.apply();
			}
		});
		altKmeter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prefEditor.putInt(SDPreferences.ALTITUDE_UNIT,
						SDPreferences.KILOMETER);
				prefEditor.apply();
			}
		});

	}

}
