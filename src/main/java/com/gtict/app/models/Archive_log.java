package com.gtict.app.models;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Archive_log implements Serializable{

	String id;
	long userId;
	Date log_date;
	String action_info;
	String filename;
	String filepath;
}
