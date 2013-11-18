package cz.fit.tam.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class GameProperties implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7592330807248991049L;
	public static final String EVALUATION_AUTO = "auto";
	public static final String EVALUATION_MANUAL = "manual";

	private Integer id;

	private String language;

	private String name;

	private Integer playerLimit;

	private Integer playerCount;

	private Integer timeLimit;

	private Integer roundLimit;

	private String evaluation;

	private String[] categories;

	public GameProperties(String language, String name, Integer playerLimit,
			Integer playerCount, Integer timeLimit, Integer roundLimit,
			String evaluation, String[] categories) {
		this.language = language;
		this.name = name;
		this.playerLimit = playerLimit;
		this.playerCount = playerCount;
		this.timeLimit = timeLimit;
		this.roundLimit = roundLimit;
		this.evaluation = evaluation;
		this.categories = categories;
	}

	public GameProperties(Integer id, String language, String name,
			Integer playerLimit, Integer playerCount, Integer timeLimit,
			Integer roundLimit, String evaluation, String[] categories) {
		this.id = id;
		this.language = language;
		this.name = name;
		this.playerLimit = playerLimit;
		this.playerCount = playerCount;
		this.timeLimit = timeLimit;
		this.roundLimit = roundLimit;
		this.evaluation = evaluation;
		this.categories = categories;
	}

	public GameProperties(String language, String name, int playerLimit,
			int playerCount, int timeLimit, int roundLimit, String evaluation,
			String categories) {
		this.language = language;
		this.name = name;
		this.playerLimit = Integer.valueOf(playerLimit);
		this.playerCount = Integer.valueOf(playerCount);
		this.timeLimit = Integer.valueOf(timeLimit);
		this.roundLimit = Integer.valueOf(roundLimit);
		this.evaluation = evaluation;
		this.categories = categories.split("|");
	}

	public void setId(Integer id) throws Exception {
		if (this.id != null) {
			throw new Exception("ID already set");
		}

		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public String getLanguage() {
		return language;
	}

	public String getName() {
		return name;
	}

	public Integer getPlayerLimit() {
		return playerLimit;
	}

	public Integer getPlayerCount() {
		return playerCount;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public Integer getRoundLimit() {
		return roundLimit;
	}

	public String getEvaluation() {
		return evaluation;
	}

	public String[] getCategories() {
		return categories;
	}

	public void incrementNumberOfPlayers() {
		if (playerCount != null) {
			playerCount++;
		} else {
			playerCount = 1;
		}
	}

	public static GameProperties jsonToGame(JSONObject json)
			throws JSONException {
		return new GameProperties(Integer.valueOf(json.getString("id")),
				json.getString("language"), json.getString("name"),
				Integer.valueOf(json.getString("player_total")),
				Integer.valueOf(json.getString("player_count")),
				Integer.valueOf(json.getString("time_limit")),
				Integer.valueOf(json.getString("round_limit")),
				json.getString("evaluation"), json.getString("categories")
						.split(","));
	}

}
