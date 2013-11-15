package cz.fit.tam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class NewGameActivity extends Activity{
	final private int ZERO = 0;
	final private int ONE = 1;
	final private int FIFTY_NINE = 59;
    final private int TWENTYFOUR = 29;
	final private int THIRTY = 30;
	TextView numberOfCirclesText = null;
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame);
        /*Configure number pickers*/
        NumberPicker minutePicker = (NumberPicker) findViewById(R.id.minutePicker);
        NumberPicker secondPicker = (NumberPicker) findViewById(R.id.secondPicker);
        minutePicker.setMinValue(ZERO);
        secondPicker.setMinValue(ZERO);
        minutePicker.setMaxValue(FIFTY_NINE);
        secondPicker.setMaxValue(FIFTY_NINE);
        minutePicker.setWrapSelectorWheel(true);
        secondPicker.setWrapSelectorWheel(true);
        minutePicker.setValue(ONE);
        secondPicker.setValue(THIRTY);
        
        /*Configure seek bar, number of cirles*/
        SeekBar numberOfCirles = (SeekBar) findViewById(R.id.numberOfCirles);
        numberOfCirles.setMax(TWENTYFOUR);
        numberOfCirles.setProgress(ONE);
        numberOfCirclesText = (TextView) findViewById(R.id.numberOfCirclesText);
        numberOfCirclesText.setText(String.valueOf(ONE));
        numberOfCirles.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				numberOfCirclesText.setText(String.valueOf(progress+1));
			}
		});
        
    }
}
