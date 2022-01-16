package com.gtict.app.utilities;

import com.gtict.app.dao.DAO;
import com.gtict.app.models.Archive_log;
import com.gtict.app.models.FileInfo;
import com.gtict.app.models.User;
import com.gtict.app.services.ArchiveLogService;
import com.gtict.app.services.FileInfoService;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class AppUtils {

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

	public static boolean isFileShared(String fileName, long userid) {
		List<FileInfo> fInfo = FileInfoService.findAll();
		FileInfo f = fInfo.stream().filter(fileinfo -> fileName.equals(fileinfo.getFilename())).findFirst()
				.orElse(new FileInfo());
		System.out.println("File Retrieved::  " + f.getFilename());
		if (!Objects.isNull(f.getFilename())) {
			for (User u : f.getSharedWithUsers()) {
				if (u.getUserId() == userid) {
					System.out.println("File is not Null");
					return true;
				}
			}
		}
		return false;

	}

	public static void createFolder(String CTX_URI, String folderName, com.liferay.portal.kernel.model.User user) {
		File f = new File(CTX_URI + "\\" + folderName);
		f.mkdir();
		Archive_log archive_log = new Archive_log();
		archive_log.setUserId(user.getUserId());
		archive_log.setLog_date(new Date());
		archive_log.setAction_info("CREATED_NEW_FOLDER");
		archive_log.setFilename(folderName);
		archive_log.setFilepath(CTX_URI);
//		FileInfo fileinfo = new FileInfo();
//		fileinfo.setFilename(userPath+"\\" +fileName);
//		fileinfo.setUserId(userId);
//		fileinfo.setSharedWithUsers(userids);
//		fileinfo.setPrivate(isPrivate);
//		FileInfoService.save(fileinfo);
		ArchiveLogService.save(archive_log);
	}

	public static User maptoKeyUser(com.liferay.portal.kernel.model.User user) {
		User usr = new User();
		usr.setFullname(user.getFullName());
		usr.setUserId(user.getUserId());
		return usr;
	}

	public static DBObject mapFileInfoToDBObject(DBObject obj, FileInfo entity) {
		// TODO Auto-generated method stub
		return null;
	}
}
