package com.gtict.app.services;

import com.google.gson.Gson;
import com.gtict.app.dao.DAO;
import com.gtict.app.utilities.Archive_log;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.util.ArrayList;
import java.util.List;

public class ArchiveLogService {
	public static void save(Archive_log entity) {
		// Save in Mongo.
		DB db = DAO.getDB("archivedb");
		entity.setId(DAO.getNextId(db, entity.getClass().getName()));
		DBCollection coll = db.getCollection("file_info");
		Gson gson = new Gson();
		BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(entity));
		coll.insert(obj);
		// db.getMongoClient().close();;
	}

	public static List<Archive_log> findAll() {
		// Find all ChangeLogs
		List<Archive_log> ChangeLogs = new ArrayList<Archive_log>();
		DB db = DAO.getDB("archivedb");
		DBCollection coll = db.getCollection("file_info");
		DBCursor cursor = coll.find();
		try {
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				// Converting BasicDBObject to a custom Class(Employee)
				Archive_log emp = (new Gson()).fromJson(dbobj.toString(), Archive_log.class);
				ChangeLogs.add(emp);
			}
		} finally {
			cursor.close();
			// db.getMongoClient().close();;
		}
		return ChangeLogs;
//	        return repository.findAll();
	}
}
