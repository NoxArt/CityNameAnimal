package cz.fit.tam;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.fit.tam.model.Game;

public class RoundEvaluationActivity extends Activity {

	private Game currentGame = null;
	private Integer newRound = null;
	private String newLetter = null;
	private Integer secondsLeft = null;
	Map<String, List<String>> roundEvaluation = null;
	Map<String, List<String>> roundEvaluationWords = null;
	TextView timeLeftView = null;

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	private ScheduledFuture timeLimitHandler = null;
	private Runnable timeDecrementer = null;

	private Handler handler = null;

	public Game getCurrentGame() {
		return currentGame;
	}

	private void startTimeHandler() {
		timeDecrementer = new Runnable() {
			public void run() {
				secondsLeft--;
				if (secondsLeft >= 0) {
					// Run on UI thread
					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							updateTimeLeft();
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
							if (newRound > currentGame.getProperties()
									.getRoundLimit()) {
								Log.i("LAST", "LAST");
							} else {
								startNewRound();
							}
						}
					});
				}
			}
		};
		handler = new Handler();
		timeLeftView = (TextView) findViewById(R.id.timeLeft);
		// schedule to update time every second
		timeLimitHandler = scheduler.scheduleAtFixedRate(timeDecrementer, 0, 1,
				TimeUnit.SECONDS);

	}

	private void startNewRound() {
		Intent myIntent1 = new Intent(RoundEvaluationActivity.this,
				PlayingActivity.class);
		myIntent1.putExtra(getResources().getString(R.string.roundNum),
				(Serializable) newRound);
		myIntent1.putExtra(getResources().getString(R.string.firstLetter),
				(Serializable) newLetter);
		myIntent1.putExtra(getResources().getString(R.string.gameStr),
				(Serializable) currentGame);
		startActivity(myIntent1);
	}

	private void updateTimeLeft() {
		timeLeftView.setText(String.valueOf(secondsLeft));
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentGame = (Game) getIntent().getSerializableExtra(
				getResources().getString(R.string.gameStr));
		newRound = (Integer) getIntent().getSerializableExtra(
				getResources().getString(R.string.roundNum));
		newLetter = (String) getIntent().getSerializableExtra(
				getResources().getString(R.string.firstLetter));
		Integer timeOfStart = (Integer) getIntent().getSerializableExtra(
				getResources().getString(R.string.timeStamp));
		roundEvaluation = (Map<String, List<String>>) getIntent()
				.getSerializableExtra(
						getResources().getString(R.string.roundEvaluation));
		roundEvaluationWords = (Map<String, List<String>>) getIntent()
				.getSerializableExtra(
						getResources().getString(R.string.roundEvaluationWords));
		setContentView(R.layout.roundevaluation);
		Date time = new java.util.Date((long) timeOfStart * 1000);
		Date currentTime = new Date();
		secondsLeft = (int) (time.getTime() - currentTime.getTime()) / 1000;
		setEvaluationInfo(secondsLeft);
		startTimeHandler();
		// setEventClickListeners();
	}

	private void setEvaluationInfo(int startInSeconds) {
		TextView timeLeft = (TextView) findViewById(R.id.timeLeft);
		timeLeft.setText(String.valueOf(startInSeconds));
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		TextView playerName = null;
		TextView playerScore = new TextView(this);

		Iterator it = roundEvaluation.entrySet().iterator();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		params.setMargins(0, 10, 0, 0);
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			playerName = new TextView(this);
			playerName.setLayoutParams(params);
			playerName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			playerName
					.setTextColor(getResources().getColor(R.color.dirtyWhite));
			playerName.setText((String) pairs.getKey());
			List<String> evaluations = (List<String>) pairs.getValue();
			List<String> evaluationsWords = roundEvaluationWords
					.get((String) pairs.getKey());
			linearLayout.addView(playerName);
			for (int i = 0; i < evaluationsWords.size(); i++) {
				playerScore = new TextView(this);
				playerScore.setTextColor(getResources().getColor(
						R.color.dirtyWhite));
				String evaluationWordScore = evaluationsWords.get(i) + ": "
						+ evaluations.get(i);
				playerScore.setText(evaluationWordScore);
				linearLayout.addView(playerScore);
			}
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			it.remove(); // avoids a ConcurrentModificationException
		}
		Button btnGoToMenu = new Button(this);
		btnGoToMenu.setText(getResources().getString(R.string.intro));
		// LayoutInflater inflater = (LayoutInflater) this
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// private void setEventClickListeners() {
	// Button btnNewGame = (Button) findViewById(R.id.goToIntro);
	// btnNewGame.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// if (currentGame.isAdmin()) {
	// StopGameAsyncTask stopTask = new StopGameAsyncTask();
	// stopTask.execute();
	// } else {
	// LeaveGameAsyncTask leaveTask = new LeaveGameAsyncTask();
	// leaveTask.execute();
	// }
	//
	// Intent myIntent1 = new Intent(RoundEvaluationActivity.this,
	// MainActivity.class);
	// RoundEvaluationActivity.this.startActivity(myIntent1);
	// }
	// });
	// }

	@Override
	public void onBackPressed() {

	}

	// private class LeaveGameAsyncTask extends AsyncTask<Void, Void, Boolean> {
	//
	// protected Boolean doInBackground(Void... input) {
	// try {
	// RoundEvaluationActivity.this.getCurrentGame().leave();
	// } catch (Game.NotConnectedException e) {
	// Log.e("ERROR", "Leaving when not connected");
	// } catch (Exception e) {
	// Log.e("ERROR", e.getMessage());
	// }
	// return true;
	// }
	//
	// }
	//
	// private class StopGameAsyncTask extends AsyncTask<Void, Void, Boolean> {
	//
	// protected Boolean doInBackground(Void... input) {
	// RoundEvaluationActivity.this.getCurrentGame().stop();
	// return true;
	// }
	// }

}
