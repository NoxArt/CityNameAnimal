package cz.fit.tam.model;

public class GameProperties {
	
	private String language;
	
	private String name;
	
	private Integer playerLimit;
	
	private Integer playerCount;
	
	private Integer timeLimit;
	
	private Integer roundLimit;
	
	private String evaluation;
	
	private String[] categories;

	public GameProperties(
		String language,
		String name,
		Integer playerLimit,
		Integer playerCount,
		Integer timeLimit,
		Integer roundLimit,
		String evaluation,
		String[] categories
	) {
		this.language = language;
		this.name = name;
		this.playerLimit = playerLimit;
		this.playerCount = playerCount;
		this.timeLimit = timeLimit;
		this.roundLimit = roundLimit;
		this.evaluation = evaluation;
		this.categories = categories;
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
	
	
	
}
