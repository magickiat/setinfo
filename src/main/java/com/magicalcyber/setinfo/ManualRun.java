package com.magicalcyber.setinfo;

import java.io.File;

import com.magicalcyber.setinfo.bean.Company;
import com.magicalcyber.setinfo.reader.CompanyRetriever;
import com.magicalcyber.setinfo.reader.FinanceReader;

public class ManualRun {

	public static void main(String[] args) throws Exception {
		FinanceReader reader = new FinanceReader();
		Company company = reader.read(new File("input", "LE.txt"));
		company.setSymbol("L&E");
		CompanyRetriever.updateFinance(company);
	}

}
