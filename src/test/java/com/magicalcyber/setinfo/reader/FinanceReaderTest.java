package com.magicalcyber.setinfo.reader;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinanceReaderTest {

	private static final Logger log = LoggerFactory.getLogger(FinanceReaderTest.class);

	@Test
	public void testFinance_CPF_mustFoundFour() throws Exception {
		FinanceReader reader = new FinanceReader();
		Company company = reader.read(new File("input", "CPF.txt"));

		assertEquals(4, company.getFinances().size());
		assertEquals(new BigDecimal("365003.12"), company.getFinances().get(2556).getAssets());
		assertEquals(new BigDecimal("252797.61"), company.getFinances().get(2557).getLiabilities());
		assertEquals(new BigDecimal("116364.68"), company.getFinances().get(2558).getEquity());

		Set<Entry<Integer, Finance>> entrySet = company.getFinances().entrySet();
		for (Entry<Integer, Finance> entry : entrySet) {
			log.info(entry.getValue().toString());
		}
	}

	@Test
	public void testFinance_WORLD_naMustSkip() throws Exception {
		FinanceReader reader = new FinanceReader();
		Company company = reader.read(new File("input", "WORLD.txt"));

		assertEquals(4, company.getFinances().size());
		assertEquals(null, company.getFinances().get(2556).getRoe());
	}
}
