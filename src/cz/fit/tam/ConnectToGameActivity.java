package cz.fit.tam;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import cz.fit.tam.model.GameClient;
import cz.fit.tam.model.GameProperties;

/*
 * @author Ievgen
 */
public class ConnectToGameActivity extends Activity {

    
	private GameClient gameClient = null;
	private List<GameProperties> gameProps = null;

	public GameClient getGameClient() {
		return gameClient;
	}

	public void setGameProperties(List<GameProperties> gameProperties) {
		this.gameProps = gameProperties;
		int orientation = getResources().getConfiguration().orientation;
		getResources().getConfiguration();
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			setGamesInfoPortrait(gameProps);
		} else {
			setGamesInfoLandscape(gameProps);
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connecttogame);
		setEventClickListeners();
		String serverUrl = getResources().getString(R.string.serverUrl);
		try {
			gameClient = new GameClient(serverUrl, null);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		GetGamesAsyncTask getGames = new GetGamesAsyncTask();
		getGames.execute(this);
	}

	private void setGamesInfoPortrait(List<GameProperties> props) {
		TableLayout tableGamesInfo = (TableLayout) findViewById(R.id.tableGamesInfo);

		/* Make all columns have the same width */
		LayoutParams layoutParams = new LayoutParams();
		layoutParams.weight = 1f;
		layoutParams.width = 0;

		for (GameProperties prop : props) {
			TableRow gameInfoRow = new TableRow(this);
			TextView gameName = new TextView(this);
			TextView playerNum = new TextView(this);
			Button connectToGameButton = new Button(this);

			gameName.setText(prop.getName());
			playerNum.setText(String.valueOf(prop.getPlayerCount()));
			connectToGameButton.setText(getResources().getString(
					R.string.connect));
			connectToGameButton.setId(prop.getId());
			gameInfoRow.addView(gameName, layoutParams);
			gameInfoRow.addView(playerNum, layoutParams);
			gameInfoRow.addView(connectToGameButton);
			tableGamesInfo.addView(gameInfoRow);
			setConnectClickEventListenter(connectToGameButton);
		}
	}

	private void setGamesInfoLandscape(List<GameProperties> props) {
		TableLayout tableGamesInfo = (TableLayout) findViewById(R.id.tableGamesInfo);

		/* Make all columns have the same width */
		LayoutParams layoutParams = new LayoutParams();
		layoutParams.weight = 1f;
		layoutParams.width = 0;

		for (GameProperties prop : props) {
			TableRow gameInfoRow = new TableRow(this);
			TextView gameName = new TextView(this);
			TextView evaluation = new TextView(this);
			TextView limit = new TextView(this);
			TextView kategorie = new TextView(this);
			TextView playerNum = new TextView(this);
			Button connectToGameButton = new Button(this);

			gameName.setText(prop.getName());
			evaluation.setText(prop.getEvaluation());
			limit.setText(String.valueOf(prop.getRoundLimit()));
			String[] categories = prop.getCategories();
			String categoriesShort = "";
			for (int i = 0; i < categories.length; i++) {
				if (i > 0) {
					categoriesShort += " ," + categories[i].substring(0, 1);
				} else {
					categoriesShort += categories[i].substring(0, 1);
				}

			}
			kategorie.setText(categoriesShort);
			playerNum.setText(String.valueOf(prop.getPlayerCount()));
			connectToGameButton.setText(getResources().getString(
					R.string.connect));
			connectToGameButton.setId(prop.getId());
			gameInfoRow.addView(gameName, layoutParams);
			gameInfoRow.addView(evaluation, layoutParams);
			gameInfoRow.addView(limit, layoutParams);
			gameInfoRow.addView(kategorie, layoutParams);
			gameInfoRow.addView(playerNum, layoutParams);
			gameInfoRow.addView(connectToGameButton);

			tableGamesInfo.addView(gameInfoRow);
			setConnectClickEventListenter(connectToGameButton);
		}
	}

	private void setEventClickListeners() {
		Button btnNewGame = (Button) findViewById(R.id.newGame);
		btnNewGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent1 = new Intent(ConnectToGameActivity.this,
						NewGameActivity.class);
				ConnectToGameActivity.this.startActivity(myIntent1);
			}

		});
	}

	private boolean isInputValid(TextView userName) {
		if ((userName == null) || "".equals(userName.getText().toString())) {
			String text = getResources().getString(R.string.enterPlayerName);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void setConnectClickEventListenter(Button button) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView userName = (TextView) findViewById(R.id.game_playerName);
				if (isInputValid(userName)) {
					int gameId = v.getId();
					gameClient.setPlayerName(userName.getText().toString());
					GameProperties props = getGamePropertiesById(gameId);
					Intent myIntent1 = new Intent(ConnectToGameActivity.this,
							WaitForGameActivity.class);
					myIntent1.putExtra(getResources().getString(R.string.gamePropertiesStr), (Serializable) props);
					myIntent1.putExtra(getResources().getString(R.string.gameClientStr), (Serializable) gameClient);
					ConnectToGameActivity.this.startActivity(myIntent1);
				}
			}
		});
	}
	
	private GameProperties getGamePropertiesById (Integer gameId) {
		for (GameProperties props: gameProps) {
			if (props.getId().equals(gameId)) {
				return props;
			}
		}
		return null;
	}

	private class GetGamesAsyncTask extends
			AsyncTask<ConnectToGameActivity, Void, List<GameProperties>> {
		ConnectToGameActivity activity = null;

		protected List<GameProperties> doInBackground(
				ConnectToGameActivity... activity) {
			this.activity = activity[0];
			GameClient gameClient = activity[0].getGameClient();
			return gameClient.getGames();
		}

		protected void onPostExecute(List<GameProperties> result) {
			activity.setGameProperties(result);
		}
	}

}
