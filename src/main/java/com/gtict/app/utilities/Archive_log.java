package com.gtict.app.utilities;

import java.util.Date;

import lombok.Data;

@Data
public class Archive_log {

	String id;
	long userId;
	Date log_date;
	String action_info;
}
