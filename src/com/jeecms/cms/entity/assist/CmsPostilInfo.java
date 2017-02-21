package com.jeecms.cms.entity.assist;

import com.jeecms.cms.entity.assist.base.BaseCmsPostilInfo;

public class CmsPostilInfo extends BaseCmsPostilInfo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* [CONSTRUCTOR MARKER BEGIN] */
	public CmsPostilInfo () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsPostilInfo (java.lang.Integer id) {
		super(id);
	}
	
	/**
	 * Constructor for required fields
	 */
	public CmsPostilInfo (java.lang.Integer id,
			java.lang.String addHtml,
			java.lang.Integer contentId,
			java.lang.String inputContent,
			java.util.Date createTime,
			java.lang.String divId) {
		super (
			id,
			addHtml,
			contentId,
			inputContent,
			createTime,
			divId);
	}
}
