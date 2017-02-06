package com.jeecms.cms.entity.assist.base;

import java.io.Serializable;

/**
 * This is an object that contains data related to the jc_blog_visitor table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_blog_visitor"
 */

public abstract class BaseCmsBlogVisitor implements Serializable{
	
	public static String PROP_ID = "id";
	public static String PROP_VISITOR_ID = "visitorId";
	public static String PROP_BY_VISITOR_ID = "byVisitorId";
	public static String PROP_VISITOR_TIME = "visitorTime";

	
	
	// constructors
	public BaseCmsBlogVisitor () {
		initialize();
	}
	
	/**
	 * Constructor for primary key
	 */
	public BaseCmsBlogVisitor (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}
	
	
	/**
	 * Constructor for required fields
	 */
	public BaseCmsBlogVisitor (java.lang.Integer id,java.util.Date visitorTime) {
		this.setId(id);
		this.setVisitorTime(visitorTime);
		initialize();
	}
	
	protected void initialize () {}
	
	
	private int hashCode = Integer.MIN_VALUE;
	
	
	// primary key
	private java.lang.Integer id;
	// fields
	private java.util.Date visitorTime;
	
	
	private com.jeecms.core.entity.CmsUser visitorId;
	private com.jeecms.core.entity.CmsUser byVisitorId;
	
	
	
	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="visitor_id"
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
	 * Return the value associated with the column: visitor_time
	 */
	public java.util.Date getVisitorTime() {
		return visitorTime;
	}
	
	/**
	 * Set the value related to the column: create_time
	 * @param visitorTime the visitor_time value
	 */
	public void setVisitorTime(java.util.Date visitorTime) {
		this.visitorTime = visitorTime;
	}
	
	
	/**
	 * Return the value associated with the column: visitor_id
	 */
	public com.jeecms.core.entity.CmsUser getVisitorId() {
		return visitorId;
	}
	/**
	 * Set the value related to the column: visitor_id
	 * @param visitorId the visitor_id value
	 */
	public void setVisitorId(com.jeecms.core.entity.CmsUser visitorId) {
		this.visitorId = visitorId;
	}
	
	/**
	 * Return the value associated with the column: by_visitor_id
	 */
	public com.jeecms.core.entity.CmsUser getByVisitorId() {
		return byVisitorId;
	}
	/**
	 * Set the value related to the column: by_visitor_id
	 * @param byVisitorId the by_visitor_id value
	 */
	public void setByVisitorId(com.jeecms.core.entity.CmsUser byVisitorId) {
		this.byVisitorId = byVisitorId;
	}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.cms.entity.assist.CmsBlogVisitor)) return false;
		else {
			com.jeecms.cms.entity.assist.CmsBlogVisitor cmsVisitor = (com.jeecms.cms.entity.assist.CmsBlogVisitor) obj;
			if (null == this.getId() || null == cmsVisitor.getId()) return false;
			else return (this.getId().equals(cmsVisitor.getId()));
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
