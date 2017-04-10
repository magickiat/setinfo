package com.magicalcyber.setinfo.bean;

import java.math.BigDecimal;
import java.util.Date;

public class FinanceStat {
	private String symbol;
	private int year;
	private Date statDate;
	private BigDecimal lastPrice;
	private BigDecimal marketCap;
	private Date fsPeriodAsOf;
	private BigDecimal pe;
	private BigDecimal pbv;
	private BigDecimal bookValuePerShare;
	private BigDecimal dvdYieldPercent;

	public FinanceStat() {

	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(BigDecimal marketCap) {
		this.marketCap = marketCap;
	}

	public Date getFsPeriodAsOf() {
		return fsPeriodAsOf;
	}

	public void setFsPeriodAsOf(Date fsPeriodAsOf) {
		this.fsPeriodAsOf = fsPeriodAsOf;
	}

	public BigDecimal getPe() {
		return pe;
	}

	public void setPe(BigDecimal pe) {
		this.pe = pe;
	}

	public BigDecimal getPbv() {
		return pbv;
	}

	public void setPbv(BigDecimal pbv) {
		this.pbv = pbv;
	}

	public BigDecimal getBookValuePerShare() {
		return bookValuePerShare;
	}

	public void setBookValuePerShare(BigDecimal bookValuePerShare) {
		this.bookValuePerShare = bookValuePerShare;
	}

	public BigDecimal getDvdYieldPercent() {
		return dvdYieldPercent;
	}

	public void setDvdYieldPercent(BigDecimal dvdYieldPercent) {
		this.dvdYieldPercent = dvdYieldPercent;
	}

	public Date getStatDate() {
		return statDate;
	}

	public void setStatDate(Date statDate) {
		this.statDate = statDate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
