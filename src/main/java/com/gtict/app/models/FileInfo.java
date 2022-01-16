package com.gtict.app.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.*;

@Data
public class FileInfo implements Serializable{

	
	String id;

	String filename;
	String filepath;
	Date createdDate;
	Date modifiedDate = new Date();
	String owner;
	long userId;
	boolean isPrivate;
	List<User>sharedWithUsers;
	boolean isDeleted = false;
	

}
