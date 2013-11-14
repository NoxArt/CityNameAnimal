package cz.fit.tam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.NumberPicker;

public class NewGameActivity extends Activity{
	final private int ZERO = 0;
	final private int ONE = 1;
	final private int FIFTY_NINE = 59;
	final private int THIRTY = 30;
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame);
        
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
        
    }
}
