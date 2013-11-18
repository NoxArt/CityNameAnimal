package cz.fit.tam;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.tam.model.Game;
import cz.fit.tam.model.GameClient;
import cz.fit.tam.model.GameProperties;

/*
 * @author Ievgen
 */
public class WaitForGameActivity extends Activity {
	public static final String GAME_PROP_STR = "Game properties";
	Game currentGame = null;

	public Game getCurrentGame() {
		return currentGame;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waitingforgame);
		currentGame = (Game) getIntent().getSerializableExtra(
				getResources().getString(R.string.gameStr));
		setGameInfo(currentGame.getProperties());
	}

	public void onStop() {
		super.onStop();
		if (currentGame.isAdmin()) {
			StopGameAsyncTask stopTask = new StopGameAsyncTask();
			stopTask.execute(this);
		} else {
			LeaveGameAsyncTask leaveTask = new LeaveGameAsyncTask();
			leaveTask.execute(this);
		}
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

		private WaitForGameActivity activityWait = null;

		protected Boolean doInBackground(WaitForGameActivity... activity) {
			activityWait = activity[0];
			try {
				activity[0].getCurrentGame().leave();
			} catch (Exception e) {
				Toast.makeText(activity[0], "ERROR " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}

	}

	private class StopGameAsyncTask extends
			AsyncTask<WaitForGameActivity, Void, Boolean> {

		private WaitForGameActivity activityWait = null;

		protected Boolean doInBackground(WaitForGameActivity... activity) {
			activityWait = activity[0];
			try {
				activity[0].getCurrentGame().stop();
			} catch (Exception e) {
				Toast.makeText(activity[0], "ERROR " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}

	}
}
