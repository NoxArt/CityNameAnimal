package cz.fit.tam;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import android.app.Activity;
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
import cz.fit.tam.model.GameProperties;
import cz.fit.tam.model.Message;

/*
 * @author Ievgen
 */
public class WaitForGameActivity extends Activity {
	public static final String GAME_PROP_STR = "Game properties";
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(2);
	private ScheduledFuture beeperHandle = null;
	private List<Message> chatMessagesList = new ArrayList<Message>();
	private String lastNewMessage = "fakemessage";

	public String getLastNewMessage() {
		return lastNewMessage;
	}

	Game currentGame = null;

	public Game getCurrentGame() {
		return currentGame;
	}

	/*
	 * Gets new messages every second
	 */
	public void getNewMessages() {
		final Runnable beeper = new Runnable() {
			public void run() {
				GetChatMessagesAsyncTask getChatMessagesTask = new GetChatMessagesAsyncTask();
				getChatMessagesTask.execute(WaitForGameActivity.this);
			}
		};
		beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 1,
				TimeUnit.SECONDS);

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waitingforgame);
		currentGame = (Game) getIntent().getSerializableExtra(
				getResources().getString(R.string.gameStr));
		setGameInfo(currentGame.getProperties());
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
		Button btnNewMessage = (Button) findViewById(R.id.sendMessage);

		btnNewMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText newMessage = (EditText) findViewById(R.id.newMessage);
				String newMessageStr = newMessage.getText().toString();
				if ((newMessageStr != null) && (newMessageStr.length() > 0)) {
					WaitForGameActivity.this.sendMessage(newMessageStr);
				} else {
					String text = getResources().getString(
							R.string.too_short_message);
					Toast.makeText(WaitForGameActivity.this, text,
							Toast.LENGTH_SHORT).show();
				}
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

	private class GetChatMessagesAsyncTask extends
			AsyncTask<WaitForGameActivity, Void, List<Message>> {

		private WaitForGameActivity activityWait = null;

		protected List<Message> doInBackground(WaitForGameActivity... activity) {
			activityWait = activity[0];
			List<Message> newMessages = null;
			try {
				newMessages = activity[0].getCurrentGame().getClient()
						.getNewChatMessages();
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
				WaitForGameActivity.this.updateChatMessages(result);
			}
		}
	}

	private void updateChatMessages(List<Message> result) {
		TextView chatMessages = (TextView) findViewById(R.id.chatMessages);
		chatMessagesList.addAll(result);
		String messages = "";
		for (int i = chatMessagesList.size(); i > (chatMessagesList.size() - 5); i--) {
			messages += chatMessagesList.get(i) + "\n";
		}
		chatMessages.setText(messages);

	}
}
