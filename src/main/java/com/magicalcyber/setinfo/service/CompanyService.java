package com.magicalcyber.setinfo.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.magicalcyber.setinfo.bean.Company;
import com.magicalcyber.setinfo.util.DbUtil;

public class CompanyService {

	private DbUtil db = new DbUtil();

	public List<Company> listAllCompany() throws Exception {
		ArrayList<Company> listCompany = new ArrayList<>();

		try (Connection con = db.createConnection()) {
			ResultSet rs = con.createStatement().executeQuery("select * from company");
			while(rs.next()){
				Company comp = new Company();
				comp.setSymbol(rs.getString("symbol"));
				comp.setName(rs.getString("name"));
				listCompany.add(comp);
			}
		}

		return listCompany;
	}
}
