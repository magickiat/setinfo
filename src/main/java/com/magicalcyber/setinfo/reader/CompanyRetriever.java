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
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.magicalcyber.setinfo.bean.Company;
import com.magicalcyber.setinfo.bean.Finance;
import com.magicalcyber.setinfo.bean.FinanceStat;
import com.magicalcyber.setinfo.service.CompanyService;
import com.magicalcyber.setinfo.util.DbUtil;

public class CompanyRetriever {

	private static final String INSERT_FINANCE = "insert into finance(symbol, year, assets, liabilities, equity,  paid_up_capital, revenue, net_profit, esp_baht, roa, roe, net_profit_margin) "
			+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String INSERT_FINANCE_STAT = "insert into finance_stat(symbol, year, stat_date, last_price, market_cap, fs_period_as_of, pe, pbv, book_value_per_share, dvd_yield_percent) "
			+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final int INDEX_SYMBOL = 0;
	private static final int INDEX_NAME = 1;
	private static final int INDEX_MARKET = 2;
	private static final int INDEX_INDUSTRY = 3;
	private static final int INDEX_SECTOR = 4;

	private static final Logger log = LoggerFactory.getLogger(CompanyRetriever.class);

	private static final DbUtil db = new DbUtil();

	public static void main(String[] args) throws Exception {

		// ArrayList<Company> companyList = new CompanyRetriever().retrieve();
		CompanyService service = new CompanyService();
		List<Company> allCompany = service.listAllCompany();
		if (allCompany.size() > 0) {
			saveCompanyFinance(allCompany);
		}

	}

	public static void saveCompanyFinance(List<Company> companyList) throws SQLException, Exception {

		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("log", "error.txt")), true);

		try (Connection connection = db.createConnection()) {

			FinanceReader reader = new FinanceReader();
			FinanceRetriever retriever = new FinanceRetriever();

			// clear old data
			connection.createStatement().executeUpdate("truncate table finance_stat");
			connection.createStatement().executeUpdate("truncate table finance");

			for (Company company : companyList) {
				log.info("---> finance: " + company.getSymbol());

				String data = retriever.retrieve(company.getSymbol());

				try {
					Company companyFinance = reader.extract(data);

					// // save finance
					HashMap<Integer, Finance> finances = companyFinance.getFinances();

					if (finances != null && !finances.isEmpty()) {

						PreparedStatement pstmt = connection.prepareStatement(INSERT_FINANCE);

						Set<Entry<Integer, Finance>> entrySet = finances.entrySet();
						for (Entry<Integer, Finance> entry : entrySet) {
							Finance finance = entry.getValue();
							setDataFinance(company, pstmt, finance);
							pstmt.executeUpdate();
						}

					}

					// save finance stat
					HashMap<Integer, FinanceStat> financeStats = companyFinance.getFinanceStats();
					if (financeStats != null && !financeStats.isEmpty()) {
						PreparedStatement pstmt = connection.prepareStatement(INSERT_FINANCE_STAT);
						Set<Entry<Integer, FinanceStat>> entrySet = financeStats.entrySet();
						for (Entry<Integer, FinanceStat> entry : entrySet) {
							FinanceStat financeStat = entry.getValue();
							int year = entry.getKey();
							setDataFinanceStat(company, pstmt, financeStat, year);
							pstmt.executeUpdate();
						}

					}
				} catch (Exception ex) {
					ex.printStackTrace();
					log.error("company error: " + company.getSymbol() + "\t" + ex.getMessage());
					writer.println(company.getSymbol() + "\t" + ex.getMessage());
				}
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
			}
		}

		writer.close();
	}

	private static void setDataFinanceStat(Company company, PreparedStatement pstmt, FinanceStat financeStat, int year)
			throws SQLException {
		int count = 1;
		pstmt.setString(count++, company.getSymbol());
		pstmt.setInt(count++, year);

		if (financeStat.getStatDate() != null) {
			pstmt.setDate(count++, new java.sql.Date(financeStat.getStatDate().getTime()));
		} else {
			pstmt.setDate(count++, null);
		}

		pstmt.setBigDecimal(count++, financeStat.getLastPrice());
		pstmt.setBigDecimal(count++, financeStat.getMarketCap());

		if (financeStat.getFsPeriodAsOf() != null) {
			pstmt.setDate(count++, new java.sql.Date(financeStat.getFsPeriodAsOf().getTime()));
		} else {
			pstmt.setDate(count++, null);
		}

		pstmt.setBigDecimal(count++, financeStat.getPe());
		pstmt.setBigDecimal(count++, financeStat.getPbv());
		pstmt.setBigDecimal(count++, financeStat.getBookValuePerShare());
		pstmt.setBigDecimal(count++, financeStat.getDvdYieldPercent());
	}

	private static void setDataFinance(Company company, PreparedStatement pstmt, Finance finance) throws SQLException {
		int index = 1;
		pstmt.setString(index++, company.getSymbol());
		pstmt.setInt(index++, finance.getYear());
		pstmt.setBigDecimal(index++, finance.getAssets());
		pstmt.setBigDecimal(index++, finance.getLiabilities());
		pstmt.setBigDecimal(index++, finance.getEquity());
		pstmt.setBigDecimal(index++, finance.getPaidUpCapital());
		pstmt.setBigDecimal(index++, finance.getRevenue());
		pstmt.setBigDecimal(index++, finance.getNetProfit());
		pstmt.setBigDecimal(index++, finance.getEspBath());
		pstmt.setBigDecimal(index++, finance.getRoa());
		pstmt.setBigDecimal(index++, finance.getRoe());
		pstmt.setBigDecimal(index++, finance.getNetProfitMargin());
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

	public static void updateFinance(Company company) throws Exception {
		try (Connection connection = db.createConnection()) {
			// clear old data
			connection.createStatement().executeUpdate("DELETE FROM finance WHERE symbol = '" + company.getSymbol() + "'");
			connection.createStatement()
					.executeUpdate("DELETE FROM finance_stat WHERE symbol = '" + company.getSymbol() + "'");

			// insert new data
			PreparedStatement pstmtFinance = connection.prepareStatement(INSERT_FINANCE);
			PreparedStatement pstmtStat = connection.prepareStatement(INSERT_FINANCE_STAT);

			Set<Entry<Integer, Finance>> entrySet = company.getFinances().entrySet();
			for (Entry<Integer, Finance> entry : entrySet) {
				setDataFinance(company, pstmtFinance, entry.getValue());
				pstmtFinance.executeUpdate();
			}

			Set<Entry<Integer, FinanceStat>> entrySet2 = company.getFinanceStats().entrySet();
			for (Entry<Integer, FinanceStat> entry : entrySet2) {
				setDataFinanceStat(company, pstmtStat, entry.getValue(), entry.getKey());
				pstmtStat.executeUpdate();
			}

		}
	}
}
