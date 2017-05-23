package com.jeecms.cms.entity.assist;

import com.jeecms.cms.entity.assist.base.BaseCmsReportDoc;

public class CmsReportDoc extends BaseCmsReportDoc {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* [CONSTRUCTOR MARKER BEGIN] */
	public CmsReportDoc () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsReportDoc (java.lang.Integer id) {
		super(id);
	}
	
	/**
	 * Constructor for required fields
	 */
	public CmsReportDoc (java.lang.Integer id,java.lang.String reportname,java.lang.String reporturl,
			java.util.Date reporttime) {
		super (
			id,
			reportname,
			reporturl,
			reporttime);
	}


}
