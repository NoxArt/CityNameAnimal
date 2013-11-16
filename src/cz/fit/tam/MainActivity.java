package cz.fit.tam;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*Make first letter white in Mesto-jmeno-rostlina-zvire-vec text views*/
        ArrayList<TextView> answerGroups = new ArrayList<TextView>();
        answerGroups.add((TextView)findViewById(R.id.mestoTextView));
        answerGroups.add((TextView)findViewById(R.id.jmenoTextView));
        answerGroups.add((TextView)findViewById(R.id.rostlinaTextView));
        answerGroups.add((TextView)findViewById(R.id.zvireTextView));
        answerGroups.add((TextView)findViewById(R.id.vecTextView));
        String currentText = null;
        SpannableString currentTextSpannable = null;
        for (TextView textView: answerGroups) {
        	currentText = textView.getText().toString();
        	currentTextSpannable = new SpannableString(currentText); 	
        	currentTextSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dirtyWhite)), 0, 1, 0);
        	textView.setText(currentTextSpannable);
        	
        }
        
        setEventClickListeners(savedInstanceState);
        
    }
    
    private void setEventClickListeners(Bundle savedInstanceState) {
    	Button btnNewGame = (Button) findViewById(R.id.newGame);
    	btnNewGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent myIntent1 = new Intent(MainActivity.this, NewGameActivity.class);
			    MainActivity.this.startActivity(myIntent1);
			}
		});
    	
    	Button btnConnectToGame = (Button) findViewById(R.id.connectToGame);
    	btnConnectToGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent1 = new Intent(MainActivity.this, ConnectToGameActivity.class);
			    MainActivity.this.startActivity(myIntent1);	
			}
		});
    }
}

