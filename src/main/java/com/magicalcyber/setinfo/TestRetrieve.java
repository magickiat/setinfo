package com.magicalcyber.setinfo;

import com.magicalcyber.setinfo.bean.Company;
import com.magicalcyber.setinfo.reader.FinanceReader;
import com.magicalcyber.setinfo.reader.FinanceRetriever;

public class TestRetrieve {

	public static void main(String[] args) throws Exception {
		String symbol = "L%26E";
		FinanceRetriever ret = new FinanceRetriever();
		String data = ret.retrieve(symbol);
		System.out.println(data);
		
		FinanceReader reader = new FinanceReader();
		Company company = reader.extract(data);
		
	}

}
