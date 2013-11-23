package cz.fit.tam;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
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
		setGameInfo(currentGame.getProperties(), currentLetter);

		startTimeHandler();
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
