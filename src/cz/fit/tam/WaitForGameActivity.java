package cz.fit.tam;

import android.app.Activity;
import android.os.Bundle;

public class WaitForGameActivity extends Activity{
	public static final String GAME_PROP_STR = "Game properties";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waitingforgame);
	}

}
