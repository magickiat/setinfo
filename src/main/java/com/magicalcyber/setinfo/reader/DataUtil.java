package com.magicalcyber.setinfo.reader;

public class DataUtil {

	public static String cleanHtmlData(String data){
		// remove "&nbsp;" by using "\u00a0" 
		return data.replace("\u00a0", "").replaceAll(",", "");
	}
}
