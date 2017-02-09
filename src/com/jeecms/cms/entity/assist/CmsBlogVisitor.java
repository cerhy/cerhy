package com.jeecms.cms.entity.assist;

import com.jeecms.cms.entity.assist.base.BaseCmsBlogVisitor;

public class CmsBlogVisitor extends BaseCmsBlogVisitor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* [CONSTRUCTOR MARKER BEGIN] */
	public CmsBlogVisitor () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsBlogVisitor (java.lang.Integer id) {
		super(id);
	}
	
	/**
	 * Constructor for required fields
	 */
	public CmsBlogVisitor (java.lang.Integer id,java.util.Date visitorTime) {
		super (
			id,
			visitorTime);
	}

}
