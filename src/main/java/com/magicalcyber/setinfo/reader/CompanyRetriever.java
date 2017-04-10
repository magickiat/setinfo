package com.magicalcyber.setinfo.reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.magicalcyber.setinfo.bean.Company;
import com.magicalcyber.setinfo.bean.Finance;
import com.magicalcyber.setinfo.bean.FinanceStat;
import com.magicalcyber.setinfo.util.DbUtil;

public class CompanyRetriever {

	private static final int INDEX_SYMBOL = 0;
	private static final int INDEX_NAME = 1;
	private static final int INDEX_MARKET = 2;
	private static final int INDEX_INDUSTRY = 3;
	private static final int INDEX_SECTOR = 4;

	private static final Logger log = LoggerFactory.getLogger(CompanyRetriever.class);

	private static final DbUtil db = new DbUtil();

	public static void main(String[] args) throws Exception {

		ArrayList<Company> companyList = new CompanyRetriever().retrieve();
		if (companyList.size() > 0) {
			saveCompanyFinance(companyList);
		}

	}

	private static void saveCompanyFinance(ArrayList<Company> companyList) throws SQLException, Exception {

		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("log", "error.txt")), true);

		try (Connection connection = db.createConnection()) {

			FinanceReader reader = new FinanceReader();
			FinanceRetriever retriever = new FinanceRetriever();

			
			for (Company company : companyList) {
				log.info("---> finance: " + company.getSymbol());

				String data = retriever.retrieve(company.getSymbol());

				try {
					Company companyFinance = reader.extract(data);

					// save finance
					HashMap<Integer, Finance> finances = companyFinance.getFinances();
					if (!finances.isEmpty()) {
						PreparedStatement pstmt = connection.prepareStatement(
								"insert into finance(symbol, year, liabilities, equity, paid_up_capital, revenue, net_profit, esp_baht, roa, roe, net_profit_margin) "
										+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
						// clear old data
						connection.createStatement().executeUpdate("truncate table finance");
						
						Set<Entry<Integer, Finance>> entrySet = finances.entrySet();
						for (Entry<Integer, Finance> entry : entrySet) {
							Finance finance = entry.getValue();

							pstmt.setString(1, company.getSymbol());
							pstmt.setInt(2, finance.getYear());
							pstmt.setBigDecimal(3, finance.getLiabilities());
							pstmt.setBigDecimal(4, finance.getEquity());
							pstmt.setBigDecimal(5, finance.getPaidUpCapital());
							pstmt.setBigDecimal(6, finance.getRevenue());
							pstmt.setBigDecimal(7, finance.getNetProfit());
							pstmt.setBigDecimal(8, finance.getEspBath());
							pstmt.setBigDecimal(9, finance.getRoa());
							pstmt.setBigDecimal(10, finance.getRoe());
							pstmt.setBigDecimal(11, finance.getNetProfitMargin());
							pstmt.executeUpdate();
						}

					}

					// save finance stat
					HashMap<Integer, FinanceStat> financeStats = companyFinance.getFinanceStats();
					if (!financeStats.isEmpty()) {
						PreparedStatement pstmt = connection.prepareStatement(
								"insert into finance_stat(symbol, year, stat_date, last_price, market_cap, fs_period_as_of, pe, pbv, book_value_per_share, dvd_yield_percent) "
										+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
						// clear old data
						connection.createStatement().executeUpdate("truncate table finance_stat");
						
						Set<Entry<Integer, FinanceStat>> entrySet = financeStats.entrySet();
						for (Entry<Integer, FinanceStat> entry : entrySet) {
							FinanceStat financeStat = entry.getValue();
							pstmt.setString(1, company.getSymbol());
							pstmt.setInt(2, entry.getKey());
							pstmt.setDate(3, new java.sql.Date(financeStat.getStatDate().getTime()));
							pstmt.setBigDecimal(4, financeStat.getLastPrice());
							pstmt.setBigDecimal(5, financeStat.getMarketCap());
							pstmt.setDate(6, new java.sql.Date(financeStat.getFsPeriodAsOf().getTime()));
							pstmt.setBigDecimal(7, financeStat.getPe());
							pstmt.setBigDecimal(8, financeStat.getPbv());
							pstmt.setBigDecimal(9, financeStat.getBookValuePerShare());
							pstmt.setBigDecimal(10, financeStat.getDvdYieldPercent());
							pstmt.executeUpdate();
						}

					}
				} catch (Exception ex) {
					log.error("company error: " + company.getSymbol() + "\t" + ex.getMessage());
					writer.println(company.getSymbol() + "\t" + ex.getMessage());
				}
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
			}
		}

		writer.close();
	}

	public ArrayList<Company> retrieve() throws Exception {
		String filename = "company.html";
		File companyFile = new File("input", filename);
		URL fileUrlTH = new URL("https://www.set.or.th/dat/eod/listedcompany/static/listedCompanies_th_TH.xls");
		// URL fileUrlEN = new
		// URL("https://www.set.or.th/dat/eod/listedcompany/static/listedCompanies_en_US.xls");
		FileUtils.copyURLToFile(fileUrlTH, companyFile);
		Document document = Jsoup.parse(companyFile, "TIS-620");
		// log.info(document.html());

		ArrayList<Company> companyList = new ArrayList<>();

		try (Connection connection = db.createConnection()) {
			Elements rows = document.select("table tr");

			// clear company table
			connection.createStatement().executeUpdate("truncate table company");

			// insert company when has data - skip header
			if (rows.size() > 2) {
				PreparedStatement pstmt = connection.prepareStatement(
						"insert into company(symbol, name, market, industry, sector) values(?, ?, ?, ?, ?)");

				for (int index = 2; index < rows.size(); index++) {
					Elements cols = rows.get(index).select("td");
					String symbol = DataUtil.cleanHtmlData(cols.get(INDEX_SYMBOL).text());
					String name = DataUtil.cleanHtmlData(cols.get(INDEX_NAME).text());
					String market = DataUtil.cleanHtmlData(cols.get(INDEX_MARKET).text());
					String industry = DataUtil.cleanHtmlData(cols.get(INDEX_INDUSTRY).text());
					String sector = DataUtil.cleanHtmlData(cols.get(INDEX_SECTOR).text());

					companyList.add(new Company(symbol, name, market, industry, sector));

					pstmt.setString(1, symbol);
					pstmt.setString(2, name);
					pstmt.setString(3, market);
					pstmt.setString(4, industry);
					pstmt.setString(5, sector);
					pstmt.executeUpdate();

					log.info("save : " + symbol);
				}
			}

		}

		return companyList;
	}
}
