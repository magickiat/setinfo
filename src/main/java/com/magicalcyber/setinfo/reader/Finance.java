package com.magicalcyber.setinfo.reader;

import java.math.BigDecimal;

public class Finance {

	private Integer year;
	private BigDecimal assets;
	private BigDecimal liabilities;
	private BigDecimal equity;
	private BigDecimal paidUpCapital;
	private BigDecimal revenue;
	private BigDecimal netProfit;
	private BigDecimal espBath;

	private BigDecimal roa;
	private BigDecimal roe;
	private BigDecimal netProfitMargin;

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public BigDecimal getAssets() {
		return assets;
	}

	public void setAssets(BigDecimal assets) {
		this.assets = assets;
	}

	public BigDecimal getLiabilities() {
		return liabilities;
	}

	public void setLiabilities(BigDecimal liabilities) {
		this.liabilities = liabilities;
	}

	public BigDecimal getEquity() {
		return equity;
	}

	public void setEquity(BigDecimal equity) {
		this.equity = equity;
	}

	public BigDecimal getRoe() {
		return roe;
	}

	public void setRoe(BigDecimal roe) {
		this.roe = roe;
	}

	public BigDecimal getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(BigDecimal netProfit) {
		this.netProfit = netProfit;
	}

	public BigDecimal getEspBath() {
		return espBath;
	}

	public void setEspBath(BigDecimal espBath) {
		this.espBath = espBath;
	}

	public BigDecimal getPaidUpCapital() {
		return paidUpCapital;
	}

	public void setPaidUpCapital(BigDecimal paidUpCapital) {
		this.paidUpCapital = paidUpCapital;
	}

	public BigDecimal getRevenue() {
		return revenue;
	}

	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}

	public BigDecimal getRoa() {
		return roa;
	}

	public void setRoa(BigDecimal roa) {
		this.roa = roa;
	}

	public BigDecimal getNetProfitMargin() {
		return netProfitMargin;
	}

	public void setNetProfitMargin(BigDecimal netProfitMargin) {
		this.netProfitMargin = netProfitMargin;
	}

	@Override
	public String toString() {
		return "Finance [year=" + year + ", assets=" + assets + ", liabilities=" + liabilities + ", equity=" + equity
				+ ", paidUpCapital=" + paidUpCapital + ", revenue=" + revenue + ", netProfit=" + netProfit
				+ ", espBath=" + espBath + ", roa=" + roa + ", roe=" + roe + ", netProfitMargin=" + netProfitMargin
				+ "]";
	}
}
