package cz.fit.tam;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.tam.model.Game;
import cz.fit.tam.model.GameClient;
import cz.fit.tam.model.GameProperties;

/*
 * @author Ievgen
 */
public class NewGameActivity extends TamActivity {
	final private int SECONDS_MINUTES_MIN_VALUE = 0;
	final private int SEEK_BAR_DEFAULT = 0;
	final private int NUMBER_OF_CIRLES_DEFAULT = 1;
	final private int MINUTES_DEFAULT = 1;
	final private int SECONDS_DEFAULT = 30;
	final private int SECONDS_MINUTES_MAX_VALUE = 59;
	final private int TWENTY_NINE = 29;

	TextView numberOfCirclesText = null;
	private Game newGame = null;

	public Game getNewGame() {
		return newGame;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.newgame);
		/* Configure number pickers */
		configureNumberPickers();

		configureSeekBar();
		setEventClickListeners();
		setDefaultValues();
	}

	private void setEventClickListeners() {
		Button btnNewGame = (Button) findViewById(R.id.createGame);

		btnNewGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/* Get user input */
				String gameName = ((TextView) findViewById(R.id.game_name))
						.getText().toString();
				String playerName = ((TextView) findViewById(R.id.game_playerName))
						.getText().toString();
				/* Get user evaluation input */
				String evaluation = null;
				boolean auto = ((RadioButton) findViewById(R.id.radio_auto))
						.isChecked();
				if (auto == true) {
					evaluation = GameProperties.EVALUATION_AUTO;
				} else {
					evaluation = GameProperties.EVALUATION_MANUAL;
				}
				/* Get user's time limit input */
				int minuteLimit = ((NumberPicker) findViewById(R.id.minutePicker))
						.getValue();
				int secondLimit = ((NumberPicker) findViewById(R.id.secondPicker))
						.getValue();
				int totalSeconds = minuteLimit * 60 + secondLimit;
				/* Get user's round limit input */
				int roundLimit = ((SeekBar) findViewById(R.id.numberOfCirles))
						.getProgress() + 1;

				int numOfCategories = 0;
				ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
				checkboxes.add((CheckBox) findViewById(R.id.chkBxCity));
				checkboxes.add((CheckBox) findViewById(R.id.chkBxName));
				checkboxes.add((CheckBox) findViewById(R.id.chkBxAnimal));
				checkboxes.add((CheckBox) findViewById(R.id.chkBxThing));
				checkboxes.add((CheckBox) findViewById(R.id.chkBxPlant));

				/* Count number of categories user has selected */
				for (CheckBox chkBx : checkboxes) {
					if (chkBx.isChecked()) {
						numOfCategories++;
					}
				}
				String[] categories = new String[numOfCategories];
				/* Get categories which user has selected to array */
				int j = 0;
				for (int i = 0; i < checkboxes.size(); i++) {
					if (checkboxes.get(i).isChecked()) {
						categories[j] = checkboxes.get(i).getText().toString();
						j++;
					}
				}

				/* Get user's player limit input */
				int playerLimit = Integer
						.parseInt(((TextView) findViewById(R.id.game_maxPlayers))
								.getText().toString());

				/* Save preferences */
				SharedPreferences.Editor edit = getPreferences().edit();
				edit.putString("game_gameName", gameName);
				edit.putString("game_playerName", playerName);
				edit.putInt("game_playerLimit", playerLimit);
				edit.putBoolean("game_evaluation", auto);
				edit.putInt("game_minuteLimit", minuteLimit);
				edit.putInt("game_secondLimit", secondLimit);
				edit.putInt("game_roundLimit", roundLimit - 1);
				edit.commit();

				/* If user's input is valid, create game, go to next activity */
				if (isUserInputValid(gameName, playerName, categories,
						(TextView) findViewById(R.id.game_maxPlayers))) {

					GameProperties newGameProperties = new GameProperties("cz",
							gameName, playerLimit, null, totalSeconds,
							roundLimit, evaluation, categories);

					String serverUrl = getResources().getString(
							R.string.serverUrl);
					try {
						GameClient gameClient = new GameClient(serverUrl,
								playerName);
						newGame = new Game(newGameProperties, gameClient);
						CreateGameAsyncTask createGameThread = new CreateGameAsyncTask();
						createGameThread.execute(NewGameActivity.this);

					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		});

	}

	private boolean isUserInputValid(String gameName, String playerName,
			String[] categories, TextView numOfPlayers) {
		if ("".equals(gameName) || gameName == null) {
			String text = getResources().getString(R.string.enterGameName);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			return false;
		}

		if ("".equals(playerName) || playerName == null) {
			String text = getResources().getString(R.string.enterPlayerName);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			return false;
		}

		if (categories.length == 0) {
			String text = getResources().getString(R.string.chooseGameCategory);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		try {
			int numOfPlayersInt = Integer.parseInt(numOfPlayers.getText()
					.toString());
			if (numOfPlayersInt < 2) {
				String text = getResources().getString(
						R.string.enterMorePlayers);
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				return false;
			}
		} catch (Exception e) {
			String text = getResources().getString(R.string.enterMorePlayers);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;

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

	private void displayErrorMessage(String error) {
		Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
	}

	private void setDefaultValues() {
		SharedPreferences pref = getPreferences();

		if (pref.contains("game_gameName")) {
			((TextView) findViewById(R.id.game_name)).setText(pref.getString(
					"game_gameName", ""));
		}

		if (pref.contains("game_playerName")) {
			((TextView) findViewById(R.id.game_playerName)).setText(pref
					.getString("game_playerName", ""));
		}

		if (pref.contains("game_playerLimit")) {
			Integer playerLimit = pref.getInt("game_playerLimit", 0);

			if (playerLimit > 0) {
				((TextView) findViewById(R.id.game_maxPlayers))
						.setText(playerLimit.toString());
			}
		}

		if (pref.contains("game_evaluation")
				&& pref.getBoolean("game_evaluation", true) == true) {
			((RadioButton) findViewById(R.id.radio_auto)).toggle();
		}

		if (pref.contains("game_minuteLimit")) {
			((NumberPicker) findViewById(R.id.minutePicker)).setValue(pref
					.getInt("game_minuteLimit", MINUTES_DEFAULT));
		}

		if (pref.contains("game_secondLimit")) {
			((NumberPicker) findViewById(R.id.secondPicker)).setValue(pref
					.getInt("game_secondLimit", SECONDS_DEFAULT));
		}

		if (pref.contains("game_roundLimit")) {
			((SeekBar) findViewById(R.id.numberOfCirles)).setProgress(pref
					.getInt("game_roundLimit", 1));
		}
	}

	private class CreateGameAsyncTask extends
			AsyncTask<NewGameActivity, Void, Boolean> {
		private Game newGame = null;

		protected Boolean doInBackground(NewGameActivity... activity) {
			newGame = activity[0].getNewGame();
			try {
				activity[0].getNewGame().create();
			} catch (Exception e) {
				displayErrorMessage("ERROR " + e.getClass().getName());
			}
			return true;
		}

		protected void onPostExecute(Boolean result) {

			Intent myIntent1 = new Intent(NewGameActivity.this,
					WaitForGameActivity.class);
			myIntent1.putExtra(getResources().getString(R.string.gameStr),
					(Serializable) newGame);
			NewGameActivity.this.startActivity(myIntent1);
		}
	}
}