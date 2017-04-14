package com.magicalcyber.setinfo.reader;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigDecimal;

import org.junit.Test;

import com.magicalcyber.setinfo.bean.Company;
import com.magicalcyber.setinfo.util.DateUtil;

public class FinanceReaderTest {

	@Test
	public void testFinance_CPF_mustFoundFour() throws Exception {
		FinanceReader reader = new FinanceReader();
		Company company = reader.read(new File("input", "CPF.txt"));

		assertEquals(4, company.getFinances().size());
		assertEquals(new BigDecimal("365003.12"), company.getFinances().get(2556).getAssets());
		assertEquals(new BigDecimal("252797.61"), company.getFinances().get(2557).getLiabilities());
		assertEquals(new BigDecimal("116364.68"), company.getFinances().get(2558).getEquity());

		assertEquals(5, company.getFinanceStats().size());
		assertEquals(new BigDecimal("32.00"), company.getFinanceStats().get(2556).getLastPrice());
		assertEquals(new BigDecimal("210995.17"), company.getFinanceStats().get(2557).getMarketCap());
		assertEquals("30/09/2558", DateUtil.sdf.format(company.getFinanceStats().get(2558).getFsPeriodAsOf()));
		assertEquals(new BigDecimal("15.74"), company.getFinanceStats().get(2559).getPe());
		assertEquals(new BigDecimal("2.33"), company.getFinanceStats().get(2556).getPbv());
		assertEquals(new BigDecimal("13.76"), company.getFinanceStats().get(2556).getBookValuePerShare());
		assertEquals(new BigDecimal("3.44"), company.getFinanceStats().get(2556).getDvdYieldPercent());
		
	}

	@Test
	public void testFinance_WORLD_naMustSkip() throws Exception {
		FinanceReader reader = new FinanceReader();
		Company company = reader.read(new File("input", "WORLD.txt"));

		assertEquals(4, company.getFinances().size());
		assertEquals(null, company.getFinances().get(2556).getRoe());
	}
	
	@Test
	public void testFinance_DTAC_mustHaveFour() throws Exception {
		String symbol = "DTAC";
		FinanceRetriever ret = new FinanceRetriever();
		String data = ret.retrieve(symbol);
		System.out.println(data);
		
		FinanceReader reader = new FinanceReader();
		Company company = reader.extract(data);

		assertEquals(4, company.getFinances().size());
//		assertEquals(null, company.getFinances().get(2556).getRoe());
	}
}
