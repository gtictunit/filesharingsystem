package com.gtict.app.test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Date date = new Date(Instant.now().toEpochMilli());
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hhmmss");
		System.out.println("Formatted Date::::  "+sdf.format(date));
	}

}
