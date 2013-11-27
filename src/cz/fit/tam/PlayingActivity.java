package cz.fit.tam;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
import cz.fit.tam.model.GameClient;
import cz.fit.tam.model.GameClient.NotConnectedException;
import cz.fit.tam.model.GameProperties;
import cz.fit.tam.model.Message;

public class PlayingActivity extends Activity {

	private Game currentGame = null;
	private int fID = 0;
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(2);

	private ScheduledFuture timeLimitHandler = null;
	private ScheduledFuture newRoundHandler = null;
	private Runnable beeper = null;
	private Runnable newRoundRunnable = null;

	private Handler handler = null;
	private int currentTotalSeconds = 0;
	private TextView minutesView = null;
	private TextView secondsView = null;
	private Integer currentRoundNum = null;
	private String currentLetter = null;

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
			if (userInput[i] == null) {
				userInput[i] = "";
			}
			i++;
		}
		if (jmenoCatActive) {
			userInput[i] = ((EditText) findViewById(R.id.inputJmeno)).getText()
					.toString();
			if (userInput[i] == null) {
				userInput[i] = "";
			}
			i++;

		}
		if (zvireCatActive) {
			Log.i("zvire", "category active");
			userInput[i] = ((EditText) findViewById(R.id.inputAnimal))
					.getText().toString();
			Log.i("zvire", userInput[i]);
			if (userInput[i] == null) {
				userInput[i] = "";
			}
			i++;

		}
		if (vecCatActive) {
			userInput[i] = ((EditText) findViewById(R.id.inputVec)).getText()
					.toString();
			if (userInput[i] == null) {
				userInput[i] = "";
			}
			i++;
		}
		if (rostlinaCatActive) {
			userInput[i] = ((EditText) findViewById(R.id.inputRostlina))
					.getText().toString();
			if (userInput[i] == null) {
				userInput[i] = "";
			}
			i++;
		}
		return userInput;
	}

	private void startTimeHandler() {
		beeper = new Runnable() {
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
							PlayingActivity.this.sendEnteredWords();
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

	/*
	 * Gets new messages every second
	 */
	private void getNewMessages() {
		newRoundRunnable = new Runnable() {

			@Override
			public void run() {
				GetNewMessagesAsyncTask getNewMessagesTask = new GetNewMessagesAsyncTask();
				getNewMessagesTask.execute(PlayingActivity.this);
			}
		};

		newRoundHandler = scheduler.scheduleAtFixedRate(newRoundRunnable, 0,
				1000, TimeUnit.MILLISECONDS);
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

	public void onStop() {
		super.onStop();
		scheduler.schedule(new Runnable() {
			public void run() {
				timeLimitHandler.cancel(true);
				newRoundHandler.cancel(true);
			}
		}, 0, TimeUnit.SECONDS);
	}

	public void onRestart() {
		super.onRestart();
		timeLimitHandler = scheduler.scheduleAtFixedRate(beeper, 0, 1,
				TimeUnit.SECONDS);
		newRoundHandler = scheduler.scheduleAtFixedRate(newRoundRunnable, 0, 1,
				TimeUnit.SECONDS);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		scheduler.schedule(new Runnable() {
			public void run() {
				timeLimitHandler.cancel(true);
				newRoundHandler.cancel(true);
			}
		}, 0, TimeUnit.SECONDS);
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
		setGameInfo(currentGame.getProperties(), currentLetter, currentRoundNum);

		startTimeHandler();
		getNewMessages();
		setEventListeners();
	}

	private void stopTimeHandler() {
		scheduler.schedule(new Runnable() {
			public void run() {
				timeLimitHandler.cancel(true);
			}
		}, 0, TimeUnit.SECONDS);
	}

	private void startNewRound(String currentLetter, Integer newRoundNum) {
		stopTimeHandler();
		Log.i("before sending new messages, round_num",
				String.valueOf(this.currentRoundNum));
		this.currentLetter = currentLetter;

		sendEnteredWordsStartNewRound(newRoundNum); // Before starting
													// round, send words.
		// The one who
		// ended the round first, sends his results twice,
		// but this shouldn't be a problem
		// We set new game info after sending words in onPostExecutedActivity. I
		// know it's confusing. Sorry.
		// If we start round in this activity, we have a problem because sending
		// is asyncronous
	}

	private void sendEnteredWords() {
		SendWordsAsyncTask sendWordsTask = new SendWordsAsyncTask();
		sendWordsTask.execute(this.currentRoundNum);
	}

	private void sendEnteredWordsStartNewRound(Integer newRoundNum) {
		SendWordsStartNewRound sendWordsTask = new SendWordsStartNewRound();
		sendWordsTask.execute(newRoundNum);
	}

	private void setEventListeners() {
		Button submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PlayingActivity.this.sendEnteredWords();
			}
		});
	}

	private void setGameInfo(GameProperties gameProps, String currentLetter,
			Integer currentRoundNum) {
		TextView fillingOutTextView = (TextView) findViewById(R.id.fillingOut);
		String fillingOutText = getResources().getString(R.string.fillingout)
				+ String.valueOf(currentRoundNum) + "/"
				+ String.valueOf(gameProps.getRoundLimit());
		fillingOutTextView.setText(fillingOutText);
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
		EditText categoryEdit = null;
		for (int i = 0; i < categories.length; i++) {
			if (categories[i].equals(getResources().getString(R.string.mesto))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutMesto);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				categoryEdit = (EditText) findViewById(R.id.inputMesto);
				categoryEdit.setText("");
				mestoCatActive = true;
			} else if (categories[i].equals(getResources().getString(
					R.string.jmeno))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutJmeno);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				categoryEdit = (EditText) findViewById(R.id.inputJmeno);
				categoryEdit.setText("");
				jmenoCatActive = true;
			} else if (categories[i].equals(getResources().getString(
					R.string.zvire))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutAnimal);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				categoryEdit = (EditText) findViewById(R.id.inputAnimal);
				categoryEdit.setText("");
				zvireCatActive = true;
			} else if (categories[i].equals(getResources().getString(
					R.string.vec))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutVec);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				categoryEdit = (EditText) findViewById(R.id.inputVec);
				categoryEdit.setText("");
				vecCatActive = true;
			} else if (categories[i].equals(getResources().getString(
					R.string.rostlina))) {
				categoryLayout = (RelativeLayout) findViewById(R.id.layoutRostlina);
				categoryLayout.setVisibility(RelativeLayout.VISIBLE);
				categoryEdit = (EditText) findViewById(R.id.inputRostlina);
				categoryEdit.setText("");
				rostlinaCatActive = true;
			}
		}

	}

	private class SendWordsAsyncTask extends AsyncTask<Integer, Void, Boolean> {

		protected Boolean doInBackground(Integer... round) {
			Log.i("current_round_num_while_sending", String.valueOf(round[0]));
			PlayingActivity.this.getCurrentGame().sendWords(round[0],
					PlayingActivity.this.getEnteredWords());
			return true;
		}
	}

	private class SendWordsStartNewRound extends
			AsyncTask<Integer, Void, Boolean> {
		Integer newRound = null;

		protected Boolean doInBackground(Integer... round) {
			Log.i("current_round_num_while_sending", String.valueOf(round[0]));
			newRound = round[0];
			PlayingActivity.this.getCurrentGame().sendWords(
					PlayingActivity.this.getCurrentRoundNum(),
					PlayingActivity.this.getEnteredWords());
			return true;
		}

		protected void onPostExecute(Boolean result) {
			PlayingActivity.this.currentRoundNum = newRound;
			setGameInfo(currentGame.getProperties(), currentLetter, newRound);
			startTimeHandler();
		}
	}

	private class GetNewMessagesAsyncTask extends
			AsyncTask<PlayingActivity, Void, List<Message>> {

		protected List<Message> doInBackground(PlayingActivity... activity) {
			List<Message> newMessages = null;
			try {
				newMessages = activity[0].getCurrentGame().getClient()
						.getNewMessages();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NotConnectedException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// Not printing out this info as exception is thrown every
				// second and it makes logs unreadable
				// e.printStackTrace();
			}
			return newMessages;
		}

		protected void onPostExecute(List<Message> result) {
			if (result != null) {
				for (Message message : result) {
					if ((message.getType().compareTo(
							GameClient.ROUND_STARTED_TYPE) == 0)) {
						try {
							JSONObject roundStarted = new JSONObject(
									message.getData());
							String firstLetter = (String) roundStarted
									.get("letter");
							Integer roundNum = (Integer) roundStarted
									.get("round");
							PlayingActivity.this.startNewRound(firstLetter,
									roundNum);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if ((message.getType().compareTo(
							GameClient.GAME_FINISHED_TYPE) == 0)) {
						Intent myIntent1 = new Intent(PlayingActivity.this,
								RoundEvaluationActivity.class);
						myIntent1.putExtra(
								getResources().getString(R.string.gameStr),
								(Serializable) currentGame);
						PlayingActivity.this.startActivity(myIntent1);
					}
				}
			}
		}
	}
}
