package com.magicalcyber.setinfo.bean;

import java.util.HashMap;

public class Company {
	private String symbol;
	private String name;
	private String market;
	private String industry;
	private String sector;
	private HashMap<Integer, Finance> finances;
	private HashMap<Integer, FinanceStat> financeStats;

	public Company() {
	}

	public Company(String symbol, String name, String market, String industry, String sector) {
		this.symbol = symbol;
		this.name = name;
		this.market = market;
		this.industry = industry;
		this.sector = sector;
	}

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

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public HashMap<Integer, FinanceStat> getFinanceStats() {
		return financeStats;
	}

	public void setFinanceStats(HashMap<Integer, FinanceStat> financeStats) {
		this.financeStats = financeStats;
	}

	@Override
	public String toString() {
		return "Company [symbol=" + symbol + ", name=" + name + ", market=" + market + ", industry=" + industry
				+ ", sector=" + sector + ", finances=" + finances + "]";
	}

}
