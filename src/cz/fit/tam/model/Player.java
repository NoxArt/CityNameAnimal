/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fit.tam.model;

/**
 *
 * @author Nox
 */
public class Player {
	
	private String name;
	private Token token;
	private Integer id;

	public Player(String name) {
		this.name = name;
	}

	public Player(String name, Token token, Integer id) {
		this.name = name;
		this.token = token;
		this.id = id;
	}
	
	public void connected(Token token, Integer id) {
		this.token = token;
		this.id = id;
	}
	
	public boolean isConnected() {
		return this.id == null;
	}

	public String getName() {
		return name;
	}

	public Token getToken() {
		return token;
	}

	public Integer getId() {
		return id;
	}
	
	public boolean isAdmin() {
		return false;
	}
	
}
