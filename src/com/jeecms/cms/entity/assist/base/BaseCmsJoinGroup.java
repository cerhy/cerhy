package com.jeecms.cms.entity.assist.base;

import java.io.Serializable;

/**
 * This is an object that contains data related to the jc_join_group table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_join_group"
 */
public abstract class BaseCmsJoinGroup implements Serializable{
	public static String PROP_ID = "id";
	public static String PROP_CREATE_USER_ID="createUserId";
	public static String PROP_JOIN_USER_ID="joinUserId";
	public static String PROP_COLUMNS_ID="columnsId";
	public static String PROP_CODE="joinCode";
	public static String PROP_JOIN_TIME="joinTime";
	
	// constructors
		public BaseCmsJoinGroup () {
			initialize();
		}
		
		/**
		 * Constructor for primary key
		 */
		public BaseCmsJoinGroup (java.lang.Integer id) {
			this.setId(id);
			initialize();
		}
		
		
		/**
		 * Constructor for required fields
		 */
		public BaseCmsJoinGroup (java.lang.Integer id,java.lang.String joinCode,java.util.Date joinTime) {
			this.setId(id);
			this.setJoinCode(joinCode);
			this.setJoinTime(joinTime);
			initialize();
		}
		
		protected void initialize () {}
		
		
		private int hashCode = Integer.MIN_VALUE; 
	
		// primary key
		private java.lang.Integer id;
		// fields
		private java.lang.String joinCode;
		private java.util.Date joinTime;
		
		
		private com.jeecms.core.entity.CmsUser createUserId;
		private com.jeecms.core.entity.CmsUser joinUserId;
		private com.jeecms.cms.entity.main.Columns columnsId;


		
		/**
		 * Return the unique identifier of this class
	     * @hibernate.id
	     *  generator-class="identity"
	     *  column="join_id"
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
		 * Return the value associated with the column: create_user_id
		 */
		public com.jeecms.core.entity.CmsUser getCreateUserId() {
			return createUserId;
		}

		/**
		 * Set the value related to the column: create_user_id
		 * @param visitorTime the create_user_id value
		 */
		public void setCreateUserId(com.jeecms.core.entity.CmsUser createUserId) {
			this.createUserId = createUserId;
		}

		/**
		 * Return the value associated with the column: join_user_id
		 */
		public com.jeecms.core.entity.CmsUser getJoinUserId() {
			return joinUserId;
		}

		/**
		 * Set the value related to the column: join_user_id
		 * @param visitorTime the join_user_id value
		 */
		public void setJoinUserId(com.jeecms.core.entity.CmsUser joinUserId) {
			this.joinUserId = joinUserId;
		}
		
		/**
		 * Return the value associated with the column: code
		 */
		public java.lang.String getJoinCode() {
			return joinCode;
		}
		
		/**
		 * Set the value related to the column: code
		 * @param visitorTime the code value
		 */
		public void setJoinCode(java.lang.String joinCode) {
			this.joinCode = joinCode;
		}
		
		/**
		 * Return the value associated with the column: join_time
		 */
		public java.util.Date getJoinTime() {
			return joinTime;
		}
		
		/**
		 * Set the value related to the column: join_time
		 * @param visitorTime the join_time value
		 */
		public void setJoinTime(java.util.Date joinTime) {
			this.joinTime = joinTime;
		}
		
		/**
		 * Return the value associated with the column: columns_id
		 */
		public com.jeecms.cms.entity.main.Columns getColumnsId() {
			return columnsId;
		}

		/**
		 * Set the value related to the column: columns_id
		 * @param visitorTime the columns_id value
		 */
		public void setColumnsId(com.jeecms.cms.entity.main.Columns columnsId) {
			this.columnsId = columnsId;
		}
		
		
		public boolean equals (Object obj) {
			if (null == obj) return false;
			if (!(obj instanceof com.jeecms.cms.entity.assist.CmsJoinGroup)) return false;
			else {
				com.jeecms.cms.entity.assist.CmsJoinGroup cmsJoinGroup = (com.jeecms.cms.entity.assist.CmsJoinGroup) obj;
				if (null == this.getId() || null == cmsJoinGroup.getId()) return false;
				else return (this.getId().equals(cmsJoinGroup.getId()));
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
