package com.magicalcyber.setinfo.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.magicalcyber.setinfo.bean.Company;
import com.magicalcyber.setinfo.bean.Finance;
import com.magicalcyber.setinfo.bean.FinanceStat;
import com.magicalcyber.setinfo.util.DateUtil;

public class FinanceReader {

	private static final int INDEX_ASSETS = 1; // สินทรัพย์รวม
	private static final int INDEX_LIABILITIES = 2; // หนี้สินรวม
	private static final int INDEX_EQUITY = 3; // ส่วนของผู้ถือหุ้น
	private static final int INDEX_PAID_UP_CAPITAL = 4; // มูลค่าหุ้นที่เรียกชำระแล้ว
	private static final int INDEX_REVENUE = 5; // รายได้รวม
	private static final int INDEX_NET_PROFIT = 6; // กำไรสุทธิ
	private static final int INDEX_EPS_BAHT = 7; // กำไรต่อหุ้น (บาท)
	// header - 8
	private static final int INDEX_ROA = 9;
	private static final int INDEX_ROE = 10;
	private static final int INDEX_NET_PROFIT_MARGIN = 11; // อัตรากำไรสุทธิ(%)

	public static void main(String[] args) throws Exception {

		// String url =
		// "https://www.set.or.th/set/companyhighlight.do?symbol=PB&ssoPageId=5&language=th&country=TH";

		FinanceReader reader = new FinanceReader();
		reader.read(new File("input", "CPF.txt"));
	}

	private static final Logger log = LoggerFactory.getLogger(FinanceReader.class);

	public Company extract(String html) throws Exception {

		Company company = new Company();

		Document document = Jsoup.parse(html, "UTF-8");
		document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

		Elements table = document.select("table");

		setFinance(company, table);
		setFinanceStat(company, table);

		return company;
	}

	private void setFinanceStat(Company company, Elements table) throws Exception {
		HashMap<Integer, FinanceStat> financeStat = new HashMap<>();
		Elements rowHead = table.select("thead").get(1).select("tr th");
		log.info("setFinanceStat");
		// log.info("### " + rowHead.html());
		HashMap<Integer, Integer> yearMap = new HashMap<>();

		for (int index = 1; index < rowHead.size(); index++) {
			FinanceStat stat = new FinanceStat();
			String strDate = DataUtil.cleanHtmlData(rowHead.get(index).text());
			Date statDate = DateUtil.sdf.parse(strDate);
			stat.setStatDate(statDate);
			financeStat.put(index, stat);

			yearMap.put(index, Integer.parseInt(strDate.substring(strDate.length() - 4)));
		}

		Elements rowBody = table.select("tbody").get(1).select("tr");
		for (int rowIndex = 0; rowIndex < rowBody.size(); rowIndex++) {
			Element element = rowBody.get(rowIndex);
			// log.info("---- " + element.html());
			Elements data = element.select("td");
			for (int dataIndex = 1; dataIndex < data.size(); dataIndex++) {
				switch (rowIndex) {
				case 0:
					financeStat.get(dataIndex).setLastPrice(getData(data, dataIndex));
					break;
				case 1:
					financeStat.get(dataIndex).setMarketCap(getData(data, dataIndex));
					break;
				case 2:
					// log.info("last price: " + value);
					String strData = DataUtil.cleanHtmlData(data.get(dataIndex).text());
					if (strData != null && strData.trim().length() > 4) {
						try {
							Date date = DateUtil.sdf.parse(strData);
							financeStat.get(dataIndex).setFsPeriodAsOf(date);
						} catch (Exception e) {
							log.warn("skip fs_period because: " + e.getMessage());
						}
					}

					break;
				case 3:
					financeStat.get(dataIndex).setPe(getData(data, dataIndex));
					break;
				case 4:
					financeStat.get(dataIndex).setPbv(getData(data, dataIndex));
					break;
				case 5:
					financeStat.get(dataIndex).setBookValuePerShare(getData(data, dataIndex));
					break;
				case 6:
					financeStat.get(dataIndex).setDvdYieldPercent(getData(data, dataIndex));
					break;
				}
			}
		}

		// convert yearIndex to yearFinance
		HashMap<Integer, FinanceStat> yearFinanceMap = new HashMap<>();
		Set<Entry<Integer, FinanceStat>> entrySet = financeStat.entrySet();
		for (Entry<Integer, FinanceStat> entry : entrySet) {
			// map key from col-index (2, 3, ...) to year (2558, 2559, ...)
			yearFinanceMap.put(yearMap.get(entry.getKey()), entry.getValue());
		}

		company.setFinanceStats(yearFinanceMap);
	}

	private void setFinance(Company company, Elements table) {
		// key in col index in table-html
		HashMap<Integer, Finance> finances = new HashMap<>();

		Elements rowHead = table.select("thead").get(0).select("tr th");

		// key in year normal
		HashMap<Integer, Integer> yearMap = new HashMap<>();
		if (rowHead.size() > 2) {

			for (int index = 1; index < rowHead.size() - 1; index++) {
				Element element = rowHead.get(index);
				String data = DataUtil.cleanHtmlData(element.text());

				if (data.length() < 4) {
					log.warn("Not found finance year for company: " + data);
					continue;
				}

				// log.info("data: " + data);
				String year = data.substring(data.length() - 4);

				Finance finance = new Finance();
				finance.setYear(Integer.parseInt(year));

				// log.info(finance.getYear() + "");
				// vertical
				finances.put(index, finance);

				yearMap.put(index, finance.getYear());
			}

			// find data
			Elements rowBody = table.select("tbody").get(0).select("tr");
			for (int dataIndex = 0; dataIndex < rowBody.size(); dataIndex++) {
				Element element = rowBody.get(dataIndex);
				Elements dataList = element.select("td");

				for (int valueIndex = 1; valueIndex < dataList.size() - 1; valueIndex++) {
					BigDecimal value = getData(dataList, valueIndex);
					Finance finance = finances.get(valueIndex);

					if (finance != null) {
						switch (dataIndex) {
						case INDEX_ASSETS:
							finance.setAssets(value);
							break;

						case INDEX_LIABILITIES:
							finance.setLiabilities(value);
							break;

						case INDEX_EQUITY:

							finance.setEquity(value);
							break;

						case INDEX_PAID_UP_CAPITAL:
							finance.setPaidUpCapital(value);
							break;

						case INDEX_REVENUE:
							finance.setRevenue(value);
							break;

						case INDEX_NET_PROFIT:
							finance.setNetProfit(value);
							break;

						case INDEX_EPS_BAHT:
							finance.setEspBath(value);
							break;

						case INDEX_ROA:
							finance.setRoa(value);
							break;

						case INDEX_ROE:
							finance.setRoe(value);
							break;

						case INDEX_NET_PROFIT_MARGIN:
							finance.setNetProfitMargin(value);
							break;

						}
					}

				}

				// log.info("*******************************************");

			}

			// convert yearIndex to yearFinance
			HashMap<Integer, Finance> yearFinanceMap = new HashMap<>();
			Set<Entry<Integer, Finance>> entrySet = finances.entrySet();
			for (Entry<Integer, Finance> entry : entrySet) {
				// map key from col-index (2, 3, ...) to year (2558, 2559, ...)
				yearFinanceMap.put(yearMap.get(entry.getKey()), entry.getValue());
			}

			company.setFinances(yearFinanceMap);
		} else {
			log.warn("No data");
			return;
		}

	}

	public Company read(File input) throws Exception {
		Company company = new Company();

		log.info("--------------------------------------------------------");
		log.info("begin read " + input.getName());
		log.info("--------------------------------------------------------");
		if (input.exists()) {
			company = extract(FileUtils.readFileToString(input, StandardCharsets.UTF_8));
		} else {
			throw new FileNotFoundException();
		}

		return company;
	}

	private BigDecimal getData(Elements dataList, int index) {
		Element data = dataList.get(index);
		// log.info("data = " + data);

		try {
			return new BigDecimal(DataUtil.cleanHtmlData(data.text()));
		} catch (Exception e) {
			// log.error("##### " + e.getMessage());
			return null;
		}
	}
}
