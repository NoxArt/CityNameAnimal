/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fit.tam.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 
 * @author Nox
 */
public class Token implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4078349728434426668L;
	private String value;
	private Calendar validUntil;

	public Token(String value) {
		this.value = value;
		this.validUntil = new GregorianCalendar();
		this.validUntil.add(Calendar.DATE, 1);
	}

	public Token(String value, Calendar validUntil) {
		this.value = value;
		this.validUntil = validUntil;
	}

	public String getValue() {
		return value;
	}

	public boolean isValid() {
		return (new GregorianCalendar()).before(this.validUntil);
	}

}
