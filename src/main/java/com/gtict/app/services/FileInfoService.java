package com.gtict.app.services;

import com.google.gson.Gson;
import com.gtict.app.dao.DAO;
import com.gtict.app.utilities.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.util.ArrayList;
import java.util.List;

public class FileInfoService {
	public static void save(FileInfo entity) {
		// Save in Mongo.
		DB db = DAO.getDB("archivedb");
		entity.setId(DAO.getNextId(db, entity.getClass().getName()));
		DBCollection coll = db.getCollection("file_info");
		Gson gson = new Gson();
		BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(entity));
		coll.insert(obj);
		// db.getMongoClient().close();;
	}

	public static List<FileInfo> findAll() {
		// Find all ChangeLogs
		List<FileInfo> ChangeLogs = new ArrayList<FileInfo>();
		DB db = DAO.getDB("archivedb");
		DBCollection coll = db.getCollection("file_info");
		DBCursor cursor = coll.find();
		try {
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				// Converting BasicDBObject to a custom Class(Employee)
				FileInfo emp = (new Gson()).fromJson(dbobj.toString(), FileInfo.class);
				ChangeLogs.add(emp);
			}
		} finally {
			cursor.close();
			// db.getMongoClient().close();;
		}
		return ChangeLogs;
//        return repository.findAll();
	}
}
