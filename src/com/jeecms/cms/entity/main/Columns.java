package com.jeecms.cms.entity.main;

public class Columns {
	private static final long serialVersionUID = 1L;
	private Integer columnId;
	private Integer userId;
	private String columnName;
	private Integer orderId;
	private String uniqueCode;
	private Integer type ;
	
	private Integer columsLevel ;
	private com.jeecms.cms.entity.main.Columns parentId; 

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

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
	
	public Columns(Integer columnId,Integer userId, String columnName, Integer orderId,String uniqueCode) {
		this.columnId = columnId;
		this.userId = userId;
		this.columnName = columnName;
		this.orderId = orderId;
		this.uniqueCode=uniqueCode;
	}
	
	public Columns(Integer columnId,Integer userId, String columnName, Integer orderId,String uniqueCode,Integer type) {
		this.columnId = columnId;
		this.userId = userId;
		this.columnName = columnName;
		this.orderId = orderId;
		this.uniqueCode=uniqueCode;
		this.type = type;
	}
	public Columns(Integer userId, String columnName, Integer orderId,String uniqueCode,Integer type,Integer columsLevel,com.jeecms.cms.entity.main.Columns parentId) {
		this.userId = userId;
		this.columnName = columnName;
		this.orderId = orderId;
		this.uniqueCode=uniqueCode;
		this.type = type;
		this.columsLevel=columsLevel;
		this.parentId=parentId;
	}
	public Columns(Integer columnId,Integer userId, String columnName, Integer orderId,String uniqueCode,Integer type,Integer columsLevel,com.jeecms.cms.entity.main.Columns parentId) {
		this.columnId=columnId;
		this.userId = userId;
		this.columnName = columnName;
		this.orderId = orderId;
		this.uniqueCode=uniqueCode;
		this.type = type;
		this.columsLevel=columsLevel;
		this.parentId=parentId;
	}
	public Columns(Integer columnId,Integer userId, String columnName, Integer orderId,String uniqueCode,Integer type,Integer columsLevel) {
		this.columnId=columnId;
		this.userId = userId;
		this.columnName = columnName;
		this.orderId = orderId;
		this.uniqueCode=uniqueCode;
		this.type = type;
		this.columsLevel=columsLevel;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getColumsLevel() {
		return columsLevel;
	}

	public void setColumsLevel(Integer columsLevel) {
		this.columsLevel = columsLevel;
	}

	public com.jeecms.cms.entity.main.Columns getParentId() {
		return parentId;
	}

	public void setParentId(com.jeecms.cms.entity.main.Columns parentId) {
		this.parentId = parentId;
	}
	
	
}
