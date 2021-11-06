package com.gtict.app.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppUtils {

	
	public static String getProperty(String propertyName) {
		String value = "";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
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
}
