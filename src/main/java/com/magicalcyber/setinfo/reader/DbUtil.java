package com.magicalcyber.setinfo.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DbUtil {

	public static final String DB_NAME = "setinfo";

	public void saveCompanyFinance(List<Company> companies) {

		MongoClient mongoClient = new MongoClient();
		try {
			MongoDatabase database = mongoClient.getDatabase(DB_NAME);
			MongoCollection<BsonDocument> collection = database.getCollection(Company.class.getSimpleName(), BsonDocument.class);

			// clear collection
			collection.drop();
			database.createCollection(Company.class.getSimpleName());

			// insert
			for (Company company : companies) {
				BsonDocument docCompany = new BsonDocument().append("symbol", new BsonString(company.getSymbol()))
						.append("name", new BsonString(company.getName()));

				HashMap<Integer, Finance> finances = company.getFinances();
				List<BasicDBObject> financeList = new ArrayList<>();
				Set<Entry<Integer, Finance>> entrySet = finances.entrySet();
				for (Entry<Integer, Finance> entry : entrySet) {
					Finance value = entry.getValue();
					BsonDocument finance = new BsonDocument()
							.append("assets", new BsonString(value.getAssets().toPlainString()))
							.append("liabilities", new BsonString(value.getLiabilities().toPlainString()));

					financeList.add(new BasicDBObject);
				}

				docCompany.put("finances", new BsonArray(financeList));
				collection.insertMany(financeList);
				
			}

		} finally {
			mongoClient.close();
		}

	}

}
