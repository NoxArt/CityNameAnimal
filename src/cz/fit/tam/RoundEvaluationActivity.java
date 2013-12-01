package cz.fit.tam;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import cz.fit.tam.model.Game;

public class RoundEvaluationActivity extends Activity {

	private Game currentGame = null;
	private Integer newRound = null;
	private String newLetter = null;
	private Integer timeOfStart = null;

	public Game getCurrentGame() {
		return currentGame;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentGame = (Game) getIntent().getSerializableExtra(
				getResources().getString(R.string.gameStr));
		newRound = (Integer) getIntent().getSerializableExtra(
				getResources().getString(R.string.roundNum));
		newLetter = (String) getIntent().getSerializableExtra(
				getResources().getString(R.string.firstLetter));
		timeOfStart = (Integer) getIntent().getSerializableExtra(
				getResources().getString(R.string.timeStamp));
		setContentView(R.layout.roundevaluation);
		setEventClickListeners();
	}

	private void setEventClickListeners() {
		Button btnNewGame = (Button) findViewById(R.id.goToIntro);
		btnNewGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (currentGame.isAdmin()) {
					StopGameAsyncTask stopTask = new StopGameAsyncTask();
					stopTask.execute();
				} else {
					LeaveGameAsyncTask leaveTask = new LeaveGameAsyncTask();
					leaveTask.execute();
				}

				Intent myIntent1 = new Intent(RoundEvaluationActivity.this,
						MainActivity.class);
				RoundEvaluationActivity.this.startActivity(myIntent1);
			}
		});
	}

	@Override
	public void onBackPressed() {

	}

	private class LeaveGameAsyncTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... input) {
			try {
				RoundEvaluationActivity.this.getCurrentGame().leave();
			} catch (Game.NotConnectedException e) {
				Log.e("ERROR", "Leaving when not connected");
			} catch (Exception e) {
				Log.e("ERROR", e.getMessage());
			}
			return true;
		}

	}

	private class StopGameAsyncTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... input) {
			RoundEvaluationActivity.this.getCurrentGame().stop();
			return true;
		}
	}

}
