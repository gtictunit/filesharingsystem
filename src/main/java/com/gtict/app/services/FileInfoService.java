package com.gtict.app.services;

import com.google.gson.Gson;
import com.gtict.app.dao.DAO;
import com.gtict.app.models.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileInfoService {
	public static void save(FileInfo entity) {
		// Save in Mongo.
		System.out.println("No or Shared users to persist::::>  "+entity.getSharedWithUsers().size());
		DB db = DAO.getDB("archivedb");
		entity.setId(DAO.getNextId(db, entity.getClass().getName()));
		DBCollection coll = db.getCollection("file_info");
		Gson gson = new Gson();
		BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(entity));
		BasicDBObject obj2 = (BasicDBObject) JSON.parse(gson.toJson(entity));
		
		coll.insert(obj);
		// db.getMongoClient().close();;
	}

	public static void update(FileInfo entity) {
		// Save in Mongo.
		DB db = DAO.getDB("archivedb");
		DBCollection coll = db.getCollection("file_info");
		DBObject query =new BasicDBObject();
		query.put("id", entity.getId());
		DBObject find = coll.findOne(query);
		Gson gson = new Gson();
		BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(entity));
		obj.put("_id",find.get("_id"));
		coll.save(obj);
	}

	public static void delete(FileInfo entity) {
		// Save in Mongo.
		DB db = DAO.getDB("archivedb");
		DBCollection coll = db.getCollection("file_info");
		Gson gson = new Gson();
		BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(entity));
		coll.save(obj);
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

	public static FileInfo findOne(File f) {
		// Find One FileInfo
		DB db = DAO.getDB("archivedb");
		DBCollection coll = db.getCollection("file_info");
		DBObject query = new BasicDBObject();
		query.put("filename", f.getName());

		DBObject cursor = coll.findOne(query);
		// Converting BasicDBObject to a custom Class(Employee)
		FileInfo emp = (new Gson()).fromJson(cursor.toString(), FileInfo.class);
		return emp;
	}
}
