package com.magicalcyber.setinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.magicalcyber.setinfo.reader.Company;
import com.magicalcyber.setinfo.reader.FinanceReader;
import com.magicalcyber.setinfo.reader.FinanceRetriever;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		
		//TODO 
		// step 1: load all company from database
		// step 2: loop to retrieve finance data
		// step 3: save to database

		String symbol = "CPF";
		FinanceRetriever retriever = new FinanceRetriever();
		String financeInfo = retriever.retrieve(symbol);
		// log.info(financeInfo);
		
		FinanceReader reader = new FinanceReader();
		Company company = reader.extract(financeInfo);
		company.setSymbol(symbol);
		
		log.info(company.toString());
		
		
	}

}
