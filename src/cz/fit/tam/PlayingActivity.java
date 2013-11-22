package cz.fit.tam;

import cz.fit.tam.model.Game;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlayingActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playing);
		/*
		 * currentGame = (Game) getIntent().getSerializableExtra(
		 * getResources().getString(R.string.gameStr));
		 * setGameInfo(currentGame.getProperties());
		 * 
		 * Button startNewGame = (Button) findViewById(R.id.startGame); if
		 * (currentGame.isAdmin()) { startNewGame.setVisibility(View.VISIBLE); }
		 * setEventListeners(); getNewMessages();
		 */
	}

}
