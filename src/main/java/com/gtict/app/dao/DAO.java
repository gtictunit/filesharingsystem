package com.gtict.app.dao;

import com.gtict.app.utilities.AppUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

public class DAO {
	
	public static MongoClient m;

	static String mongoHost =  "localhost";//AppUtils.getProperty("spring.data.mongodb.host");

	static int mongoPort = 27017;//AppUtils.getProperty("spring.data.mongodb.port");

//	static String mongoUser = AppUtils.getProperty("spring.data.mongodb.username");
//
//	static String mongoPwd = AppUtils.getProperty("spring.data.mongodb.password");

//	static String mongoAuthDb = AppUtils.getProperty("spring.data.mongodb.authentication-database");

	static {
		try {

			// for local loggin
//			int mongoP = Integer.parseInt(mongoPort);
			System.out.println("MongoHost:>>> " + mongoHost);
			m = new MongoClient(mongoHost, mongoPort);
			m.setWriteConcern(WriteConcern.SAFE);
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public static String getNextId(DB db, String seq_name) {
		String sequence_collection = "Access";
		String sequence_field = "Access";
		DBCollection seq = db.getCollection(sequence_collection);
		BasicDBObject basicDBObject1 = new BasicDBObject();
		basicDBObject1.put("_id", seq_name);
		BasicDBObject basicDBObject2 = new BasicDBObject(sequence_field, Integer.valueOf(1));
		BasicDBObject basicDBObject3 = new BasicDBObject("$inc", basicDBObject2);
		DBObject res = seq.findAndModify((DBObject) basicDBObject1, (DBObject) new BasicDBObject(),
				(DBObject) new BasicDBObject(), false, (DBObject) basicDBObject3, true, true);
		String nextId = res.get(sequence_field).toString();
		System.out.println("Next Transaction Id:>>> " + nextId);
		return nextId;
	}

	public static DB getDB(String dbname) {
		
//		if (AppUtils.getProperty("gt.app.db.mode").equalsIgnoreCase("local")) {
			System.out.println("Inside Get DB method - local");
			DB db = m.getDB(dbname);
			return db;
//		} else {System.out.println("Inside Get DB method - remote");
//			String client_url = "mongodb://" + mongoUser + ":" + mongoPwd + "@" + mongoHost + ":" + mongoPort + "/"
//					+ mongoAuthDb;
//			MongoClientURI uri = new MongoClientURI(client_url);
//			System.out.println("Inside Get DB method 2");
//			MongoClient mongo_client = new MongoClient(uri);
//			System.out.println("Inside Get DB method 3");
//			DB db = mongo_client.getDB(dbname);
//			System.out.println("Inside Get DB method 4");
//			return db;
//		}
	}
}
