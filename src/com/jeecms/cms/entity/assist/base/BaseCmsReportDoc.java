package com.jeecms.cms.entity.assist.base;

import java.io.Serializable;

/**
 * This is an object that contains data related to the jc_report_doc table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_report_doc"
 */
public abstract class BaseCmsReportDoc implements Serializable{
	public static String PROP_ID = "id";
	public static String PROP_REPORT_NAME="reportname";
	public static String PROP_REPORT_URL="reporturl";
	public static String PROP_REPORT_TIME="reporttime";
	
	// constructors
		public BaseCmsReportDoc () {
			initialize();
		}
		
		/**
		 * Constructor for primary key
		 */
		public BaseCmsReportDoc (java.lang.Integer id) {
			this.setId(id);
			initialize();
		}
		
		
		/**
		 * Constructor for required fields
		 */
		public BaseCmsReportDoc (java.lang.Integer id,java.lang.String reportname,java.lang.String reporturl,
				java.util.Date reporttime) {
			this.setId(id);
			this.setReportname(reportname);
			this.setReporturl(reporturl);
			this.setReporttime(reporttime);
			initialize();
		}
		
		protected void initialize () {}
		
		
		private int hashCode = Integer.MIN_VALUE; 
	
		// primary key
		private java.lang.Integer id;
		// fields
		private java.lang.String reportname;
		private java.lang.String reporturl;
		private java.util.Date reporttime;


		
	
		/**
		 * Return the unique identifier of this class
	     * @hibernate.id
	     *  generator-class="identity"
	     *  column="id"
	     */
		public java.lang.Integer getId() {
			return id;
		}
		/**
		 * Set the unique identifier of this class
		 * @param id the new ID
		 */
		public void setId(java.lang.Integer id) {
			this.hashCode = Integer.MIN_VALUE;
			this.id = id;
		}

		/**
		 * Return the value associated with the column: reportname
		 */
		public java.lang.String getReportname() {
			return reportname;
		}
		/**
		 * Set the value related to the column: reportname
		 * @param visitorTime the reportname value
		 */
		public void setReportname(java.lang.String reportname) {
			this.reportname = reportname;
		}

		/**
		 * Return the value associated with the column: reporturl
		 */
		public java.lang.String getReporturl() {
			return reporturl;
		}

		/**
		 * Set the value related to the column: reporturl
		 * @param visitorTime the reporturl value
		 */
		public void setReporturl(java.lang.String reporturl) {
			this.reporturl = reporturl;
		}

		/**
		 * Return the value associated with the column: reporttime
		 */
		public java.util.Date getReporttime() {
			return reporttime;
		}
		/**
		 * Set the value related to the column: reporttime
		 * @param visitorTime the reporttime value
		 */
		public void setReporttime(java.util.Date reporttime) {
			this.reporttime = reporttime;
		}

		public boolean equals (Object obj) {
			if (null == obj) return false;
			if (!(obj instanceof com.jeecms.cms.entity.assist.CmsReportDoc)) return false;
			else {
				com.jeecms.cms.entity.assist.CmsReportDoc cmsReportDoc = (com.jeecms.cms.entity.assist.CmsReportDoc) obj;
				if (null == this.getId() || null == cmsReportDoc.getId()) return false;
				else return (this.getId().equals(cmsReportDoc.getId()));
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
