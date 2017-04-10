package com.magicalcyber.setinfo.reader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FinanceRetriever {
	
	public String retrieve(String symbol) throws Exception {
		Document document = Jsoup.connect("https://www.set.or.th/set/companyhighlight.do?symbol="+symbol + "&ssoPageId=5&language=th&country=TH").get();
		Elements body = document.select("div.table-responsive");
		return body.html();
	}
	
}
