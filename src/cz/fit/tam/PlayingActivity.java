package cz.fit.tam;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.tam.model.Game;
import cz.fit.tam.model.GameProperties;

public class PlayingActivity extends Activity {

	private Game currentGame = null;
	private int fID = 0;
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(2);

	private ScheduledFuture timeLimitHandler = null;
	private Handler handler = null;
	private int currentTotalSeconds = 0;
	private TextView minutesView = null;
	private TextView secondsView = null;
	private Integer currentRoundNum = null;

	private boolean mestoCatActive = false;
	private boolean jmenoCatActive = false;
	private boolean zvireCatActive = false;
	private boolean vecCatActive = false;
	private boolean rostlinaCatActive = false;

	private Game getCurrentGame() {
		return currentGame;
	}

	private Integer getCurrentRoundNum() {
		return currentRoundNum;
	}

	/*
	 * Vrací slová zadal uživatel. Pořadí v poli - jmeno, mesto, zvire, vec,
	 * rostlina. Pokud nějakou s kategorii v tuto chvili nehráme, tak pořadi se
	 * nemění, jen že toto slovo v poli nebude. Třeba, nehráme zviře, dostaneme
	 * jmeno, mesto, vec, rostlina
	 */
	private String[] getEnteredWords() {
		int numOfCategories = currentGame.getProperties().getCategories().length;
		String[] userInput = new String[numOfCategories];
		int i = 0;
		if (mestoCatActive) {
			userInput[i] = ((EditText) findViewById(R.id.inputMesto)).getText()
					.toString();
			i++;
		}
		if (jmenoCatActive) {
			userInput[i] = ((EditText) findViewById(R.id.inputJmeno)).getText()
					.toString();
			i++;
		}
		if (zvireCatActive) {
			userInput[i] = ((EditText) findViewById(R.id.inputAnimal))
					.getText().toString();
			i++;
		}
		if (vecCatActive) {
			userInput[i] = ((EditText) findViewById(R.id.inputVec)).getText()
					.toString();
			i++;
		}
		if (rostlinaCatActive) {
			userInput[i] = ((EditText) findViewById(R.id.inputRostlina))
					.getText().toString();
			i++;
		}
		return userInput;
	}

	private void startTimeHandler() {
		final Runnable beeper = new Runnable() {
			public void run() {
				currentTotalSeconds--;

				if (currentTotalSeconds >= 0) {
					// Run on UI thread
					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							updateTimeLimits();
						}
					});
				} else {
					// stop executing every second
					scheduler.schedule(new Runnable() {
						public void run() {
							timeLimitHandler.cancel(true);
						}
					}, 0, TimeUnit.SECONDS);
					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showMessage(getResources().getString(
									R.string.noTimeLeft));
						}
					});
				}
			}
		};
		handler = new Handler();
		minutesView = (TextView) findViewById(R.id.timeLimitMinutes);
		secondsView = (TextView) findViewById(R.id.timeLimitSeconds);
		// schedule to update time every second
		timeLimitHandler = scheduler.scheduleAtFixedRate(beeper, 0, 1,
				TimeUnit.SECONDS);

	}

	private void showMessage(String message) {
		Toast toast = new Toast(this);
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private void updateTimeLimits() {
		int minutes = currentTotalSeconds / 60;
		int seconds = currentTotalSeconds % 60;
		minutesView.setText(String.valueOf(minutes));
		secondsView.setText(String.valueOf(seconds));
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playing);

		currentGame = (Game) getIntent().getSerializableExtra(
				getResources().getString(R.string.gameStr));
		String currentLetter = (String) getIntent().getSerializableExtra(
				getResources().getString(R.string.firstLetter));
		currentRoundNum = (Integer) getIntent().getSerializableExtra(
				getResources().getString(R.string.roundNum));
		setGameInfo(currentGame.getProperties(), currentLetter);

		startTimeHandler();
		setEventListeners();
	}

	private void setEventListeners() {
		Button submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SendWordsAsyncTask sendWordsTask = new SendWordsAsyncTask();
				sendWordsTask.execute(PlayingActivity.this);
			}
		});
	}

	private void setGameInfo(GameProperties gameProps, String currentLetter) {
		TextView letterTextView = (TextView) findViewById(R.id.currentLetter);
		letterTextView.setText(currentLetter);
		currentTotalSeconds = gameProps.getTimeLimit();
		int minutes = currentTotalSeconds / 60;
		int seconds = currentTotalSeconds % 60;
		TextView minutesText = (TextView) findViewById(R.id.timeLimitMinutes);
		TextView secondsText = (TextView) findViewById(R.id.timeLimitSeconds);
		minutesText.setText(String.valueOf(minutes));
		secondsText.setText(String.valueOf(seconds));
		/* Set categories */
		RelativeLayout categoryLayout = null;
		String[] categories = gameProps.getCategories();
		for (int i = 0; i < categories.length; i++) {
			if (categories[i].equals(getResources().getString(R.string.mesto))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutMesto);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				mestoCatActive = true;
			} else if (categories[i].equals(getResources().getString(
					R.string.jmeno))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutJmeno);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				jmenoCatActive = true;
			} else if (categories[i].equals(getResources().getString(
					R.string.zvire))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutAnimal);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				zvireCatActive = true;
			} else if (categories[i].equals(getResources().getString(
					R.string.vec))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutVec);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				vecCatActive = true;
			} else if (categories[i].equals(getResources().getString(
					R.string.rostlina))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutRostlina);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				rostlinaCatActive = true;
			}
		}

	}

	private class SendWordsAsyncTask extends
			AsyncTask<PlayingActivity, Void, Boolean> {

		protected Boolean doInBackground(PlayingActivity... activity) {
			Log.i("current_round_num",
					String.valueOf(activity[0].getCurrentRoundNum()));
			activity[0].getCurrentGame().sendWords(
					activity[0].getCurrentRoundNum(),
					activity[0].getEnteredWords());
			return true;
		}
	}
}
