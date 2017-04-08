package com.magicalcyber.setinfo.reader;

import java.util.HashMap;

public class Company {
	private String symbol;
	private String name;
	private HashMap<Integer, Finance> finances;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<Integer, Finance> getFinances() {
		return finances;
	}

	public void setFinances(HashMap<Integer, Finance> finances) {
		this.finances = finances;
	}

}
