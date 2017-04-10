package com.magicalcyber.setinfo.reader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUtil {

	private static final Logger log = LoggerFactory.getLogger(DbUtil.class);

	public DbUtil() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new IllegalArgumentException("Driver mysql not found");
		}
	}

	public Connection createConnection() throws Exception {
		return DriverManager.getConnection("jdbc:mysql://localhost/setinfo?useSSL=false", "root", "P@ssw0rd");
	}

	public void saveCompanyFinance(List<Company> companies) throws Exception {
		Connection connection = createConnection();
		try {

			PreparedStatement pstmt = connection
					.prepareStatement("insert into finance(symbol, year, assets) values(?, ?, ?)");

			PreparedStatement pstmtDel = connection.prepareStatement("delete from finance where symbol = ?");

			for (Company company : companies) {

				// clear data
				pstmtDel.setString(1, company.getSymbol());
				pstmtDel.executeUpdate();

				// insert new data
				pstmt.setString(1, company.getSymbol());
				Set<Entry<Integer, Finance>> entrySet = company.getFinances().entrySet();
				for (Entry<Integer, Finance> entry : entrySet) {
					Finance finance = entry.getValue();
					pstmt.setInt(2, finance.getYear());
					pstmt.setBigDecimal(3, finance.getAssets());
					pstmt.executeUpdate();
				}

			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
