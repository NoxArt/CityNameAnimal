package cz.fit.tam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cz.fit.tam.model.Game;
import cz.fit.tam.model.GameProperties;

public class PlayingActivity extends Activity {

	private Game currentGame = null;
	int fID = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playing);
		currentGame = (Game) getIntent().getSerializableExtra(
				getResources().getString(R.string.gameStr));
		String currentLetter = (String) getIntent().getSerializableExtra(
				getResources().getString(R.string.firstLetter));
		setGameInfo(currentGame.getProperties(), currentLetter);
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

	private void setGameInfo(GameProperties gameProps, String currentLetter) {
		TextView letterTextView = (TextView) findViewById(R.id.currentLetter);
		letterTextView.setText(currentLetter);
		gameProps.getTimeLimit();

		RelativeLayout categoryLayout = null;
		String[] categories = gameProps.getCategories();
		for (int i = 0; i < categories.length; i++) {
			if (categories[i].equals(getResources().getString(R.string.mesto))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutMesto);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
			} else if (categories[i].equals(getResources().getString(
					R.string.jmeno))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutJmeno);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
			} else if (categories[i].equals(getResources().getString(
					R.string.zvire))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutAnimal);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
			} else if (categories[i].equals(getResources().getString(
					R.string.vec))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutVec);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
			} else if (categories[i].equals(getResources().getString(
					R.string.rostlina))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutRostlina);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
			}
		}
	}

	public int findUnusedId() {
		while (findViewById(++fID) != null)
			;
		return fID;
	}
}
