package com.jeecms.cms.entity.main;

public class Columns {
	private static final long serialVersionUID = 1L;
	private Integer columnId;
	private Integer userId;
	private String columnName;
	private Integer orderId;
	private String uniqueCode;

	public Columns() {
	}

	public Columns(Integer userId, String columnName, Integer orderId) {
		this.userId = userId;
		this.columnName = columnName;
		this.orderId = orderId;
	}

	public Columns(Integer columnId, Integer userId, String columnName, Integer orderId) {
		this.columnId = columnId;
		this.userId = userId;
		this.columnName = columnName;
		this.orderId = orderId;
	}
	public Columns(Integer userId, String columnName, Integer orderId,String uniqueCode) {
		this.userId = userId;
		this.columnName = columnName;
		this.orderId = orderId;
		this.uniqueCode=uniqueCode;
	}

	public Integer getColumnId() {
		return columnId;
	}

	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	
	
	
}
