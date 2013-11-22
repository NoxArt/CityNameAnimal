package cz.fit.tam;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.tam.model.Game;
import cz.fit.tam.model.GameClient.NotConnectedException;
import cz.fit.tam.model.GameClient;
import cz.fit.tam.model.GameProperties;
import cz.fit.tam.model.Message;

/*
 * @author Ievgen
 */
public class WaitForGameActivity extends Activity {
	public static final String GAME_PROP_STR = "Game properties";
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	private ScheduledFuture beeperHandle = null;
	private List<String> chatMessagesList = new ArrayList<String>();
	private String lastNewMessage = "fakemessage";

	private Game currentGame = null;

	public String getLastNewMessage() {
		return lastNewMessage;
	}

	public Game getCurrentGame() {
		return currentGame;
	}

	/*
	 * Gets new messages every second
	 */
	public void getNewMessages() {
		final Runnable beeper = new Runnable() {
			public void run() {
				GetNewMessagesAsyncTask getNewMessagesTask = new GetNewMessagesAsyncTask();
				getNewMessagesTask.execute(WaitForGameActivity.this);
			}
		};
		beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 1000,
				TimeUnit.MILLISECONDS);

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waitingforgame);
		currentGame = (Game) getIntent().getSerializableExtra(
				getResources().getString(R.string.gameStr));
		setGameInfo(currentGame.getProperties());

		Button startNewGame = (Button) findViewById(R.id.startGame);
		if (currentGame.isAdmin()) {
			startNewGame.setVisibility(View.VISIBLE);
		}
		setEventListeners();
		getNewMessages();
	}

	public void onStop() {
		super.onStop();
		scheduler.schedule(new Runnable() {
			public void run() {
				beeperHandle.cancel(true);
			}
		}, 0, TimeUnit.SECONDS);
		if (currentGame.isAdmin()) {
			StopGameAsyncTask stopTask = new StopGameAsyncTask();
			stopTask.execute(this);
		} else {
			LeaveGameAsyncTask leaveTask = new LeaveGameAsyncTask();
			leaveTask.execute(this);
		}
	}

	private void setEventListeners() {
		Button btnNewMessage = (Button) findViewById(R.id.sendMessag);

		btnNewMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText newMessage = (EditText) findViewById(R.id.newMessage);
				String newMessageStr = newMessage.getText().toString();
				if ((newMessageStr != null) && (newMessageStr.length() > 0)) {
					WaitForGameActivity.this.sendMessage(getCurrentGame()
							.getClient().getPlayer().getName()
							+ ": " + newMessageStr);
				} else {
					String text = getResources().getString(
							R.string.too_short_message);
					Toast.makeText(WaitForGameActivity.this, text,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button btnStartGame = (Button) findViewById(R.id.startGame);
		btnStartGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StartGameAsyncTask startGameTask = new StartGameAsyncTask();
				startGameTask.execute(WaitForGameActivity.this);
			}
		});
	}

	private void sendMessage(String message) {
		lastNewMessage = message;
		SendMessageAsyncTask sendMessageTask = new SendMessageAsyncTask();
		sendMessageTask.execute(this);
	}

	private void setGameInfo(GameProperties gameProps) {
		TextView gameName = (TextView) findViewById(R.id.game_name);
		TextView limitPlayers = (TextView) findViewById(R.id.game_maxPlayers);
		TextView currentNumOfPlayers = (TextView) findViewById(R.id.playerCount);
		TextView evaluation = (TextView) findViewById(R.id.evaluation);
		TextView timeLimit = (TextView) findViewById(R.id.timeLimit);
		TextView categories = (TextView) findViewById(R.id.categories);

		gameName.setText(gameProps.getName());
		limitPlayers.setText(String.valueOf(gameProps.getPlayerLimit()));
		currentNumOfPlayers.setText(String.valueOf(gameProps.getPlayerCount()));
		evaluation.setText(gameProps.getEvaluation());
		timeLimit.setText(String.valueOf(gameProps.getTimeLimit()));
		String categoriesStr = arrayOfCategoriesToString(gameProps
				.getCategories());
		categories.setText(categoriesStr);

	}

	private String arrayOfCategoriesToString(String[] categories) {
		String returnVal = "";
		for (int i = 0; i < categories.length; i++) {
			if (i != 0) {
				returnVal += ", " + categories[i];
			} else {
				returnVal += categories[i];
			}
		}
		return returnVal;
	}

	private class LeaveGameAsyncTask extends
			AsyncTask<WaitForGameActivity, Void, Boolean> {

		protected Boolean doInBackground(WaitForGameActivity... activity) {
			try {
				activity[0].getCurrentGame().leave();
			} catch (Game.NotConnectedException e) {
				Log.e("ERROR", "Leaving when not connected");
			} catch (Exception e) {
				Toast.makeText(activity[0], "ERROR " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}

	}

	private class StopGameAsyncTask extends
			AsyncTask<WaitForGameActivity, Void, Boolean> {

		protected Boolean doInBackground(WaitForGameActivity... activity) {
			activity[0].getCurrentGame().stop();
			return true;
		}
	}

	private class StartGameAsyncTask extends
			AsyncTask<WaitForGameActivity, Void, Boolean> {

		protected Boolean doInBackground(WaitForGameActivity... activity) {
			activity[0].getCurrentGame().startGame();
			;
			return true;
		}
	}

	private class SendMessageAsyncTask extends
			AsyncTask<WaitForGameActivity, Void, Boolean> {

		private WaitForGameActivity activityWait = null;

		protected Boolean doInBackground(WaitForGameActivity... activity) {
			activityWait = activity[0];
			activity[0].getCurrentGame().getClient()
					.sendChatMessage(activityWait.getLastNewMessage());
			return true;
		}
	}

	private class GetNewMessagesAsyncTask extends
			AsyncTask<WaitForGameActivity, Void, List<Message>> {

		protected List<Message> doInBackground(WaitForGameActivity... activity) {
			List<Message> newMessages = null;
			try {
				newMessages = activity[0].getCurrentGame().getClient()
						.getNewMessages();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				// Not printing out this info as exception is thrown every
				// second and it makes logs unreadable
				// e.printStackTrace();
			}
			return newMessages;
		}

		protected void onPostExecute(List<Message> result) {
			if (result != null) {
				Log.i("new message", "new message");
				for (Message message : result) {
					Log.i("new message", message.getData());
					Log.i("new message", message.getType());
					if ((message.getType().compareTo(
							GameClient.ROUND_STARTED_TYPE) == 0)) {
						try {
							JSONObject roundStarted = new JSONObject(
									message.getData());
							String firstLetter = (String) roundStarted
									.get("letter");
							WaitForGameActivity.this.startRound(firstLetter);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if (message.getType().compareTo(
							GameClient.CHATMESSAGE_TYPE) == 0) {
						WaitForGameActivity.this.updateChatMessages(message
								.getData());

					}
				}
			}
		}
	}

	private void startRound(String firstLetter) {
		Intent myIntent1 = new Intent(WaitForGameActivity.this,
				PlayingActivity.class);
		myIntent1.putExtra(getResources().getString(R.string.gameStr),
				(Serializable) currentGame);
		myIntent1.putExtra(getResources().getString(R.string.firstLetter),
				(Serializable) firstLetter);
		WaitForGameActivity.this.startActivity(myIntent1);
	}

	private void updateChatMessages(String newMessage) {
		TextView chatMessages = (TextView) findViewById(R.id.chatMessages);
		chatMessagesList.add(newMessage);
		String messages = "";
		for (int j = chatMessagesList.size() - 1; j >= 0; j--) {
			messages += chatMessagesList.get(j) + "\n";
		}
		chatMessages.setText(messages);

	}
}
