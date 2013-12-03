package cz.fit.tam;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cz.fit.tam.model.Game;

public class RoundEvaluationActivity extends Activity {

	private Game currentGame = null;
	private Integer newRound = null;
	private String newLetter = null;
	private Integer secondsLeft = null;
	Map<String, List<Integer>> roundEvaluation = null;
	Map<String, List<String>> roundEvaluationWords = null;
	Map<String, Integer> wholeGameEvaluation = null;
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
		roundEvaluation = (Map<String, List<Integer>>) getIntent()
				.getSerializableExtra(
						getResources().getString(R.string.roundEvaluation));
		roundEvaluationWords = (Map<String, List<String>>) getIntent()
				.getSerializableExtra(
						getResources().getString(R.string.roundEvaluationWords));
		setContentView(R.layout.roundevaluation);
		Date time = new java.util.Date((long) timeOfStart * 1000);
		Date currentTime = new Date();
		secondsLeft = (int) (time.getTime() - currentTime.getTime()) / 1000;
		setBasicViewInfo(secondsLeft);
		startTimeHandler();
		GetScores scores = new GetScores();
		scores.execute();
		setEventClickListeners();
	}

	private TextView newPlayerNameInstance(String playerName) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		params.setMargins(0, 0, 0, 0);
		TextView playerNameView = new TextView(this);
		playerNameView.setLayoutParams(params);
		playerNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		playerNameView
				.setTextColor(getResources().getColor(R.color.dirtyWhite));
		playerNameView.setText(playerName);
		playerNameView.setGravity(Gravity.CENTER_HORIZONTAL);
		return playerNameView;
	}

	private int sumValuesInArrayList(List<Integer> values) {
		int sum = 0;
		for (Integer value : values) {
			if (value != null) {
				sum += value;
			}
		}
		return sum;
	}

	private TextView newRoundScoreViewInstantce(List<Integer> scores) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		params.setMargins(5, 0, 0, 0);
		TextView roundScores = new TextView(this);
		roundScores.setLayoutParams(params);
		int sumRoundScore = sumValuesInArrayList(scores);
		Log.i("SUM SCORE", String.valueOf(sumRoundScore));
		roundScores.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		roundScores.setTextColor(getResources().getColor(R.color.green));
		roundScores.setText("(" + String.valueOf(sumRoundScore) + ")");
		return roundScores;
	}

	private TextView newWholeGameScoreViewInstance(Integer wholeGameScore) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		params.setMargins(10, 0, 0, 0);
		TextView roundScores = new TextView(this);
		roundScores.setLayoutParams(params);
		Log.i("WHOLE GAME SCORE", String.valueOf(wholeGameScore));
		roundScores.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		roundScores.setTextColor(getResources().getColor(R.color.red));
		roundScores.setText(String.valueOf(wholeGameScore));
		return roundScores;
	}

	private LinearLayout newLinearHorizontalContainerInstance() {
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		return linearLayout;
	}

	private RelativeLayout newRelativeHorizontalContainerInstance() {
		RelativeLayout relativeLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams relativeParamsCat = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		return relativeLayout;
	}

	private TextView newScoreViewInstance(Integer score) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		params.setMargins(10, 0, 0, 0);
		TextView scoreView = new TextView(this);
		scoreView.setLayoutParams(params);
		scoreView.setTextColor(getResources().getColor(R.color.blue));
		scoreView.setText(String.valueOf(score));
		return scoreView;
	}

	private TextView newScoreWordViewInstance(String word) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		params.setMargins(0, 0, 20, 0);
		TextView scoreView = new TextView(this);
		scoreView.setLayoutParams(params);
		scoreView.setTextColor(getResources().getColor(R.color.dirtyWhite));
		scoreView.setText(word);
		return scoreView;
	}

	private TextView newCategoryView(String categoryName) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));

		TextView categoryView = new TextView(this);
		categoryView.setLayoutParams(params);
		categoryView.setGravity(Gravity.LEFT);
		categoryView.setTextColor(getResources().getColor(R.color.blue));
		categoryView.setText(categoryName);
		return categoryView;
	}

	private void setEvaluationInfo() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		TextView playerName = null;
		TextView playerScoreRound = null;
		TextView wholeGameScore = null;
		LinearLayout playerNameScoreCont = null;
		RelativeLayout scoreRowContainer = null;

		TextView playerCategoryName = null;
		TextView playerScore = null;
		TextView playerScoreWord = null;

		Iterator it = wholeGameEvaluation.entrySet().iterator();
		String[] categories = getCurrentGame().getProperties().getCategories();

		/* Layout params for category */
		RelativeLayout.LayoutParams relativeParamsCat = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParamsCat.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		relativeParamsCat.setMargins(5, 0, 0, 0);
		/* Layout params for score */
		RelativeLayout.LayoutParams relativeParamsScore = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParamsScore.addRule(RelativeLayout.CENTER_HORIZONTAL);
		/* Layout params for score word */
		RelativeLayout.LayoutParams relativeParamsWord = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParamsWord.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		relativeParamsWord.setMargins(0, 0, 0, 0);

		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			List<Integer> evaluations = (List<Integer>) roundEvaluation
					.get((String) pairs.getKey());
			List<String> evaluationsWords = roundEvaluationWords
					.get((String) pairs.getKey());
			playerNameScoreCont = newLinearHorizontalContainerInstance();
			playerName = newPlayerNameInstance((String) pairs.getKey());
			playerScoreRound = newRoundScoreViewInstantce(evaluations);
			wholeGameScore = newWholeGameScoreViewInstance((Integer) pairs
					.getValue());
			playerNameScoreCont.addView(playerName);
			playerNameScoreCont.addView(playerScoreRound);
			playerNameScoreCont.addView(wholeGameScore);

			linearLayout.addView(playerNameScoreCont);

			for (int i = 0; i < evaluationsWords.size(); i++) {
				scoreRowContainer = newRelativeHorizontalContainerInstance();
				playerCategoryName = newCategoryView(categories[i]);
				playerScore = newScoreViewInstance(evaluations.get(i));
				playerScoreWord = newScoreWordViewInstance(evaluationsWords
						.get(i));
				scoreRowContainer
						.addView(playerCategoryName, relativeParamsCat);
				scoreRowContainer.addView(playerScore, relativeParamsScore);
				scoreRowContainer.addView(playerScoreWord, relativeParamsWord);
				linearLayout.addView(scoreRowContainer);
			}
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			it.remove();
		}
	}

	private void setBasicViewInfo(int startInSeconds) {
		TextView timeLeftText = (TextView) findViewById(R.id.timeLeftText);
		TextView timeLeft = (TextView) findViewById(R.id.timeLeft);
		if (newRound > getCurrentGame().getProperties().getRoundLimit()) {
			timeLeft.setVisibility(TextView.GONE);
			timeLeftText.setVisibility(TextView.GONE);
		} else {
			timeLeft.setVisibility(TextView.VISIBLE);
			timeLeftText.setVisibility(TextView.VISIBLE);
			timeLeft.setText(String.valueOf(startInSeconds));
		}
		Button btnGoToMenu = new Button(this);
		btnGoToMenu.setText(getResources().getString(R.string.endGame));

	}

	private void setEventClickListeners() {
		Button btnGoToIntro = (Button) findViewById(R.id.endGame);
		btnGoToIntro.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (getCurrentGame().isAdmin()) {
					StopGameAsyncTask stopGameTask = new StopGameAsyncTask();
					stopGameTask.execute();
				} else {
					LeaveGameAsyncTask leaveGameTask = new LeaveGameAsyncTask();
					leaveGameTask.execute();
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

	private class GetScores extends AsyncTask<Void, Void, Map<String, Integer>> {

		protected Map<String, Integer> doInBackground(Void... input) {
			return RoundEvaluationActivity.this.getCurrentGame().getScores();
		}

		protected void onPostExecute(Map<String, Integer> scores) {
			wholeGameEvaluation = sortByMapByValue(scores);
			setEvaluationInfo();
		}

	}

	private static Map<String, Integer> sortByMapByValue(
			Map<String, Integer> unsortMap) {

		List list = new LinkedList(unsortMap.entrySet());

		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// put sorted list into map again
		// LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
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
