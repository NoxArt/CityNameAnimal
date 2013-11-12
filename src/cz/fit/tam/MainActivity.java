package cz.fit.tam;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
    }
}
