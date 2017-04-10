package com.magicalcyber.setinfo.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
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

	public Company extract(String html) {

		Company company = new Company();
		Set<Finance> list = new HashSet<>();

		Document document = Jsoup.parse(html, "UTF-8");
		document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

		Elements table = document.select("table");

		// log.info(table.size() + "");
		Elements rowHead = table.select("thead").get(0).select("tr th");
		// log.info(rowHead.html());

		// key in col index in table-html
		HashMap<Integer, Finance> finances = new HashMap<>();

		// key in year normal
		HashMap<Integer, Integer> yearMap = new HashMap<>();
		if (rowHead.size() > 2) {

			// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new
			// Locale("th", "TH"));

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
				list.add(finance);

				// log.info(finance.getYear() + "");
				// vertical
				finances.put(index, finance);

				yearMap.put(index, finance.getYear());
			}

		} else {
			log.warn("No data");
			return company;
		}

		// find data
		Elements rowBody = table.select("tbody").get(0).select("tr");
		for (int dataIndex = 0; dataIndex < rowBody.size(); dataIndex++) {
			Element element = rowBody.get(dataIndex);
			Elements dataList = element.select("td");

			for (int valueIndex = 1; valueIndex < dataList.size() - 1; valueIndex++) {
				BigDecimal value = getData(dataList, valueIndex);

				switch (dataIndex) {
				case INDEX_ASSETS:
					finances.get(valueIndex).setAssets(value);
					break;

				case INDEX_LIABILITIES:
					finances.get(valueIndex).setLiabilities(value);
					break;

				case INDEX_EQUITY:
					finances.get(valueIndex).setEquity(value);
					break;

				case INDEX_PAID_UP_CAPITAL:
					finances.get(valueIndex).setPaidUpCapital(value);
					break;

				case INDEX_REVENUE:
					finances.get(valueIndex).setRevenue(value);
					break;

				case INDEX_NET_PROFIT:
					finances.get(valueIndex).setNetProfit(value);
					break;

				case INDEX_EPS_BAHT:
					finances.get(valueIndex).setEspBath(value);
					break;

				case INDEX_ROA:
					finances.get(valueIndex).setRoa(value);
					break;

				case INDEX_ROE:
					finances.get(valueIndex).setRoe(value);
					break;

				case INDEX_NET_PROFIT_MARGIN:
					finances.get(valueIndex).setNetProfitMargin(value);
					break;

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
		return company;
	}

	public Company read(File input) throws Exception {
		Company company = new Company();

		log.info("begin read");
		if (input.exists()) {
			company = extract(FileUtils.readFileToString(input, StandardCharsets.UTF_8));
		} else {
			throw new FileNotFoundException();
		}

		return company;
	}

	private BigDecimal getData(Elements dataList, int index) {
		Element data = dataList.get(index);
//		log.info("data = " + data);
		
		try{
			return new BigDecimal(DataUtil.cleanHtmlData(data.text()));
		}catch(Exception e){
//			log.error("##### " + e.getMessage());
			return null;
		}
	}
}
