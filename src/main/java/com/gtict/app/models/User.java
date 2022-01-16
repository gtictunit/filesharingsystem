package com.gtict.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class User implements Serializable {

private long userId;
private String fullname;

}
