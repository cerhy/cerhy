package com.jeecms.cms.entity.main;

public class Columns {

	private int column_id;
	private int user_id;
	private String column_name;

	public Columns(){}
	public Columns(int column_id, int user_id, String column_name){
		this.column_id = column_id;
		this.user_id = user_id;
		this.column_name = column_name;
	}
	public int getColumn_id() {
		return column_id;
	}

	public void setColumn_id(int column_id) {
		this.column_id = column_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
}
