package com.jeecms.cms.entity.assist;

import com.jeecms.cms.entity.assist.base.BaseCmsPersonalChannel;


public class CmsPersonalChannel extends BaseCmsPersonalChannel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* [CONSTRUCTOR MARKER BEGIN] */
	public CmsPersonalChannel () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsPersonalChannel (java.lang.Integer id) {
		super(id);
	}
	
	/**
	 * Constructor for required fields
	 */
	public CmsPersonalChannel (java.lang.Integer id,java.lang.Integer channelId
			,java.lang.Integer channelType,java.lang.String channelName
			,java.lang.Integer userName) {
		super (
			id,
			channelId,
			channelType,
			channelName,
			userName);
	}


}
