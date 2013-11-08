/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fit.tam.model;

/**
 *
 * @author Nox
 */
public class Admin extends Player {
	
	private Token adminToken;

	public Admin(String name) {
		super(name);
	}
	
	public Admin(Token adminToken, String name, Token token, Integer id) {
		super(name, token, id);
		this.adminToken = adminToken;
	}

	public Token getAdminToken() {
		return adminToken;
	}
	
	@Override
	public boolean isAdmin() {
		return true;
	}
	
}
