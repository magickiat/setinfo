package com.magicalcyber.setinfo.financestat;

import java.io.File;

import org.junit.Test;

import com.magicalcyber.setinfo.bean.Company;
import com.magicalcyber.setinfo.reader.FinanceReader;
import com.magicalcyber.setinfo.reader.FinanceRetriever;

public class StatProcessorTest {

	@Test
	public void testStat_CPF_mustHaveFour() throws Exception{
		File input = new File("input", "CPF.txt");
		
		FinanceRetriever ret = new FinanceRetriever();
		
		
	}
}
