package com.gtict.app.utilities;

import java.util.List;
import lombok.*;

@Data
public class FileInfo {

	String id;

	String filename;
	
	long userId;
	boolean isPrivate;
	List<Long>sharedWithUsers;
	

}
