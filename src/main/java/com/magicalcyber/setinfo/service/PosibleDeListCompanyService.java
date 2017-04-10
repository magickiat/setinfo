package com.magicalcyber.setinfo.service;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.magicalcyber.setinfo.util.DbUtil;

public class PosibleDeListCompanyService {

	private static final Logger log = LoggerFactory.getLogger(PosibleDeListCompanyService.class);

	public static void main(String[] args) throws Exception {
		File input = new File("input", "de-list-company.xls");
		String url = "https://www.set.or.th/th/company/files/PossibleDelistingCompanies270317th.xls";
		FileUtils.copyURLToFile(new URL(url), input);

		if (input.exists()) {
			DataFormatter df = new DataFormatter();

			Workbook wb = WorkbookFactory.create(input);
			Sheet sheet = wb.getSheetAt(0);
			int totalRow = sheet.getPhysicalNumberOfRows();

			DbUtil db = new DbUtil();
			try (Connection con = db.createConnection()) {
				
				PreparedStatement pstmt = con.prepareStatement("insert into posible_delist_company(symbol) values(?)");
				
				for (int index = 0; index < totalRow; index++) {
					Row row = sheet.getRow(index);
					if (row == null) {
						continue;
					}

					Cell cell = row.getCell(2);
					if (cell == null) {
						continue;
					}

					String data = df.formatCellValue(cell);
					data = data.trim();
					if (data.length() == 0 || data.startsWith("ชื่อย่อ")) {
						continue;
					}

					log.info(data);
					pstmt.setString(1, data);
					pstmt.executeUpdate();
				}
			}

		}
	}

}
