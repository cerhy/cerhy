package com.jeecms.cms.entity.assist;

import com.jeecms.cms.entity.assist.base.BaseCmsJoinGroup;

public class CmsJoinGroup extends BaseCmsJoinGroup {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* [CONSTRUCTOR MARKER BEGIN] */
	public CmsJoinGroup () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsJoinGroup (java.lang.Integer id) {
		super(id);
	}
	
	/**
	 * Constructor for required fields
	 */
	public CmsJoinGroup (java.lang.Integer id,java.lang.String joinCode,java.util.Date joinTime) {
		super (
			id,
			joinCode,
			joinTime);
	}


}
