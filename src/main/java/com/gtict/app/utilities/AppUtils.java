package com.gtict.app.utilities;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("deprecation")
public class AppUtils {	
	private static Mongo m;
	private static DB db;
	
	static {
		m = new Mongo("localhost", 27017);
	}

	
	public static String getProperty(String propertyName) {
		String value = "";
//		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File initialFile = new File("C:\\ICT\\Settings\\FileServerApp\\application.properties");
		InputStream stream = null;
		try {
			stream = new FileInputStream(initialFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		if (stream != null) {
			Properties p = new Properties();
			try {
				p.load(stream);
				value = p.getProperty(propertyName).trim();
			} catch (IOException e) {
				return null;
			}
		}
		return value;
	}
	
	public static void saveFileInfo(FileInfo fileinfo) {
		db = m.getDB("archivedb");
		DBCollection dbCol = db.getCollection("file_info");
		DBObject obj = new BasicDBObject();		
		obj.put("userid", fileinfo.getUserId());
		obj.put("fileInfo", fileinfo);
		dbCol.save(obj);
//		return true;
	}
	
	public static void saveArchiveLog(Archive_log fileinfo) {
		db = m.getDB("archivedb");
		DBCollection dbCol = db.getCollection("archive_log");
		DBObject obj = new BasicDBObject();		
		obj.put("userid", fileinfo.getUserId());
		obj.put("archive_log", fileinfo);
		dbCol.save(obj);
//		return true;
	}
	
	
}
