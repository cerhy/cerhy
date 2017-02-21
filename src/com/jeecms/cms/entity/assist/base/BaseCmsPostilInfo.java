package com.jeecms.cms.entity.assist.base;

import java.io.Serializable;

/**
 * This is an object that contains data related to the jc_postil_info table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_postil_info"
 */

public abstract class BaseCmsPostilInfo implements Serializable{
	
	public static String PROP_ID = "id";
	public static String PROP_ADD_HTML = "addHtml";
	public static String PROP_POSTIL_USER_ID = "postilUserId";
	public static String PROP_CONTENT_ID = "contentId";
	public static String PROP_INPUT_CONTENT = "inputContent";
	public static String PROP_CREATE_TIME = "createTime";
	public static String PROP_DIV_ID = "divId";
	
	
	// constructors
	public BaseCmsPostilInfo () {
		initialize();
	}
	
	/**
	 * Constructor for primary key
	 */
	public BaseCmsPostilInfo (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}
	
	
	/**
	 * Constructor for required fields
	 */
	public BaseCmsPostilInfo (java.lang.Integer id,
			java.lang.String addHtml,
			java.lang.Integer contentId,
			java.lang.String inputContent,
			java.util.Date createTime,
			java.lang.String divId) {
		this.setId(id);
		this.setAddHtml(addHtml);
		this.setContentId(contentId);
		this.setInputContent(inputContent);
		this.setCreateTime(createTime);
		this.setDivId(divId);
		initialize();
	}
	
	protected void initialize () {}
	
	private int hashCode = Integer.MIN_VALUE;
	
	
	// primary key
	private java.lang.Integer id;
	// fields
	private java.lang.String addHtml;
	private java.lang.Integer contentId;
	private java.lang.String inputContent;
	private java.util.Date createTime;
	private java.lang.String divId;
	
	private com.jeecms.core.entity.CmsUser postilUserId;

	
	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="postil_id"
     */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}

	/**
	 * Return the value associated with the column: add_html
	 */
	public java.lang.String getAddHtml() {
		return addHtml;
	}
	/**
	 * Set the value related to the column: add_html
	 * @param visitorTime the add_html value
	 */
	public void setAddHtml(java.lang.String addHtml) {
		this.addHtml = addHtml;
	}

	/**
	 * Return the value associated with the column: content_id
	 */
	public java.lang.Integer getContentId() {
		return contentId;
	}
	/**
	 * Set the value related to the column: content_id
	 * @param visitorTime the content_id value
	 */
	public void setContentId(java.lang.Integer contentId) {
		this.contentId = contentId;
	}

	/**
	 * Return the value associated with the column: input_content
	 */
	public java.lang.String getInputContent() {
		return inputContent;
	}
	/**
	 * Set the value related to the column: input_content
	 * @param visitorTime the input_content value
	 */
	public void setInputContent(java.lang.String inputContent) {
		this.inputContent = inputContent;
	}

	/**
	 * Return the value associated with the column: create_time
	 */
	public java.util.Date getCreateTime() {
		return createTime;
	}

	/**
	 * Set the value related to the column: create_time
	 * @param visitorTime the create_time value
	 */
	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * Return the value associated with the column: postil_user_id
	 */
	public com.jeecms.core.entity.CmsUser getPostilUserId() {
		return postilUserId;
	}
	/**
	 * Set the value related to the column: postil_user_id
	 * @param visitorTime the postil_user_id value
	 */
	public void setPostilUserId(com.jeecms.core.entity.CmsUser postilUserId) {
		this.postilUserId = postilUserId;
	}
	
	
	/**
	 * Return the value associated with the column: div_id
	 */
	public java.lang.String getDivId() {
		return divId;
	}
	/**
	 * Set the value related to the column: div_id
	 * @param visitorTime the div_id value
	 */
	public void setDivId(java.lang.String divId) {
		this.divId = divId;
	}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.cms.entity.assist.CmsBlogVisitor)) return false;
		else {
			com.jeecms.cms.entity.assist.CmsPostilInfo cmsPostil = (com.jeecms.cms.entity.assist.CmsPostilInfo) obj;
			if (null == this.getId() || null == cmsPostil.getId()) return false;
			else return (this.getId().equals(cmsPostil.getId()));
		}
	}
	
	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	public String toString () {
		return super.toString();
	}
	
	
	
	
}
