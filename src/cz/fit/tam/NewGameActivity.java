package cz.fit.tam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class NewGameActivity extends Activity {
	final private int SECONDS_MINUTES_MIN_VALUE = 0;
	final private int SEEK_BAR_DEFAULT = 0;
	final private int NUMBER_OF_CIRLES_DEFAULT = 1;
	final private int MINUTES_DEFAULT = 1;
	final private int SECONDS_DEFAULT = 30;
	final private int SECONDS_MINUTES_MAX_VALUE = 59;
	final private int TWENTY_NINE = 29;

	TextView numberOfCirclesText = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.newgame);
		/* Configure number pickers */
		configureNumberPickers();

		configureSeekBar();

	}

	private void configureNumberPickers() {
		NumberPicker minutePicker = (NumberPicker) findViewById(R.id.minutePicker);
		NumberPicker secondPicker = (NumberPicker) findViewById(R.id.secondPicker);
		minutePicker.setMinValue(SECONDS_MINUTES_MIN_VALUE);
		secondPicker.setMinValue(SECONDS_MINUTES_MIN_VALUE);
		minutePicker.setMaxValue(SECONDS_MINUTES_MAX_VALUE);
		secondPicker.setMaxValue(SECONDS_MINUTES_MAX_VALUE);
		minutePicker.setWrapSelectorWheel(true);
		secondPicker.setWrapSelectorWheel(true);
		minutePicker.setValue(MINUTES_DEFAULT);
		secondPicker.setValue(SECONDS_DEFAULT);

	}

	private void configureSeekBar() {
		/* Configure seek bar, number of cirles */
		/*
		 * Note: min value of SeekBar is 0. But we can't play 0 circles. That's
		 * why we add +1 whenever we get SeekBar's value
		 */
		SeekBar numberOfCirles = (SeekBar) findViewById(R.id.numberOfCirles);
		numberOfCirles.setMax(TWENTY_NINE);
		numberOfCirles.setProgress(SEEK_BAR_DEFAULT);
		numberOfCirclesText = (TextView) findViewById(R.id.numberOfCirclesText);
		numberOfCirclesText.setText(String.valueOf(NUMBER_OF_CIRLES_DEFAULT));
		numberOfCirles
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						numberOfCirclesText.setText(String
								.valueOf(progress + 1));
					}
				});
	}
}
