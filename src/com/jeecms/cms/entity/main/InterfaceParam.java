package com.jeecms.cms.entity.main;

import java.io.Serializable;

import javax.persistence.Entity;
@Entity
public class InterfaceParam implements Serializable{

	private static final long serialVersionUID = -6586309228607361962L;
	private String areacode;//手机国家地区代码
	private String mobile;//用户手机号
	private String password ;//用户密码
	private String oldpassword ;//用户旧密码
	private String newpassword  ;//用户新密码
	private String nickname;//用户昵称
	private String type;//用户类型:0:教师,1:学生,2:教研工作人员,3:家长,4:其他
    private String username;//用户名
    private String id;//用户id
    private String email;//电子邮箱
    private String gender;//性别:-1:未设置,0:女,1:男,2:其他
    private String brithdate;//出生日期
    private String category;//栏目id
    private String contentid;//文章id
    private String contenttype;//1:获取最新文章,2:获取id之后的文章,3:获取id之前的文章
    private String count;//数量
    private String userid;//指定用户下的文章
    private String name;//姓名
    
    private String reportname;//报告名称
    private String reporturl;//报告链接地址
    private String reporttime;//报告时间
    
    private String keyword;//关键字
    private String page;//请求的页数，从1开始
    
    private String columnID;//栏目id(用户自建的那种)
    
    
	public String getColumnID() {
		return columnID;
	}
	public void setColumnID(String columnID) {
		this.columnID = columnID;
	}
	public String getAreacode() {
		return areacode;
	}
	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBrithdate() {
		return brithdate;
	}
	public void setBrithdate(String brithdate) {
		this.brithdate = brithdate;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getContentid() {
		return contentid;
	}
	public void setContentid(String contentid) {
		this.contentid = contentid;
	}
	public String getContenttype() {
		return contenttype;
	}
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getOldpassword() {
		return oldpassword;
	}
	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}
	public String getNewpassword() {
		return newpassword;
	}
	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReportname() {
		return reportname;
	}
	public void setReportname(String reportname) {
		this.reportname = reportname;
	}
	public String getReporturl() {
		return reporturl;
	}
	public void setReporturl(String reporturl) {
		this.reporturl = reporturl;
	}
	public String getReporttime() {
		return reporttime;
	}
	public void setReporttime(String reporttime) {
		this.reporttime = reporttime;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	
	
	
	
}


