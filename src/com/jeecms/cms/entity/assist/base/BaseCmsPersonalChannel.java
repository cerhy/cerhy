package com.jeecms.cms.entity.assist.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the jc_blog_visitor table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_personal_channel"
 */
public abstract  class BaseCmsPersonalChannel implements Serializable {
	
	public static String PROP_ID = "id";
	public static String PROP_USER_NAME = "userName";
	public static String PROP_CHANNEL_ID = "channelId";
	public static String PROP_CHANNEL_TYPE = "channelType";
	public static String PROP_CHANNEL_NAME = "channelName";
	
	
	// constructors
		public BaseCmsPersonalChannel () {
			initialize();
		}
	
	
		/**
		 * Constructor for primary key
		 */
		public BaseCmsPersonalChannel (java.lang.Integer id) {
			this.setId(id);
			initialize();
		}
		
		
		/**
		 * Constructor for required fields
		 */
		public BaseCmsPersonalChannel (java.lang.Integer id,java.lang.Integer channelId
				,java.lang.Integer channelType,java.lang.String channelName,
				java.lang.Integer userName) {
			initialize();
		}
		
		protected void initialize () {}
		
		private int hashCode = Integer.MIN_VALUE;
		
		

		// primary key
		private java.lang.Integer id;
		// fields
		private java.lang.Integer channelId;
		private java.lang.Integer channelType;
		private java.lang.String channelName;
		
		private java.lang.Integer userName;
		
		
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
			this.id = id;
			this.hashCode = Integer.MIN_VALUE;
		}

		/**
		 * Return the value associated with the column: channel_id
		 */
		public java.lang.Integer getChannelId() {
			return channelId;
		}

		/**
		 * Set the value related to the column: channel_id
		 * @param visitorTime the channel_id value
		 */
		public void setChannelId(java.lang.Integer channelId) {
			this.channelId = channelId;
		}

		/**
		 * Return the value associated with the column: channel_type
		 */
		public java.lang.Integer getChannelType() {
			return channelType;
		}

		/**
		 * Set the value related to the column: channel_type
		 * @param visitorTime the channel_type value
		 */
		public void setChannelType(java.lang.Integer channelType) {
			this.channelType = channelType;
		}


		/**
		 * Return the value associated with the column: channel_name
		 */
		public java.lang.String getChannelName() {
			return channelName;
		}

		/**
		 * Set the value related to the column: channel_name
		 * @param visitorTime the channel_name value
		 */
		public void setChannelName(java.lang.String channelName) {
			this.channelName = channelName;
		}

	
		
		/**
		 * Return the value associated with the column: user_name
		 */
		public java.lang.Integer getUserName() {
			return userName;
		}

		/**
		 * Set the value related to the column: user_name
		 * @param visitorTime the user_name value
		 */
		public void setUserName(java.lang.Integer userName) {
			this.userName = userName;
		}


		public boolean equals (Object obj) {
			if (null == obj) return false;
			if (!(obj instanceof com.jeecms.cms.entity.assist.CmsPersonalChannel)) return false;
			else {
				com.jeecms.cms.entity.assist.CmsPersonalChannel cmsPc = (com.jeecms.cms.entity.assist.CmsPersonalChannel) obj;
				if (null == this.getId() || null == cmsPc.getId()) return false;
				else return (this.getId().equals(cmsPc.getId()));
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
