package com.jeecms.cms.action.directive;

import static com.jeecms.common.web.freemarker.DirectiveUtils.OUT_BEAN;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.common.web.freemarker.DefaultObjectWrapperBuilderFactory;
import com.jeecms.common.web.freemarker.DirectiveUtils;
import com.jeecms.common.web.freemarker.ParamsRequiredException;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.web.util.FrontUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 内容对象标签
 */
public class ContentDirective implements TemplateDirectiveModel {
	/**
	 * 输入参数，topicId
	 */
	public static final String TOPIC_ID = "topicId";
	/**
	 * 输入参数，typeIds
	 */
	public static final String TYPE_IDS = "typeIds";
	/**
	 * 输入参数，用户ID
	 */
	public static final String PARAM_USER_ID = "userId";
	/**
	 * 输入参数，columnId
	 */
	public static final String PARAM_COLUMN_ID = "columnId";
	/**
	 * 输入参数，栏目ID。
	 */
	public static final String PARAM_ID = "id";
	/**
	 * 输入参数，下一篇。
	 */
	public static final String PRAMA_NEXT = "next";
	/**
	 * 输入参数，栏目ID。
	 */
	public static final String PARAM_CHANNEL_ID = "channelId";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Integer id = getId(params);
		Boolean next = DirectiveUtils.getBool(PRAMA_NEXT, params);
		Integer userId = getUserId(params);
		Integer columnId = getColumnId(params);
		Integer topicId = getTopicId(params);
		Integer typeIds = getTypeIds(params);
		Content content;
		if (next == null) {
			content = contentMng.findById(id);
		} else {
			CmsSite site = FrontUtils.getSite(env);
			Integer channelId = DirectiveUtils.getInt(PARAM_CHANNEL_ID, params);
			content = contentMng.getSide(id, site.getId(), channelId, next,userId,columnId,topicId,typeIds);
		}

		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_BEAN, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(content));
		Map<String, TemplateModel> origMap = DirectiveUtils
				.addParamsToVariable(env, paramWrap);
		body.render(env.getOut());
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
	}
	
	private Integer getTopicId(Map<String, TemplateModel> params)
			throws TemplateException {
		Integer id = DirectiveUtils.getInt(TOPIC_ID, params);
		return id;
	}

	private Integer getId(Map<String, TemplateModel> params)
			throws TemplateException {
		Integer id = DirectiveUtils.getInt(PARAM_ID, params);
		if (id != null) {
			return id;
		} else {
			throw new ParamsRequiredException(PARAM_ID);
			
		}
	}

	private Integer getUserId(Map<String, TemplateModel> params)throws TemplateException{
		Integer userId = DirectiveUtils.getInt(PARAM_USER_ID, params);
		if (userId != null) {
			return userId;
		} else {
			return null;
		}
	}
	private Integer getColumnId(Map<String, TemplateModel> params)throws TemplateException{
		Integer columnId = DirectiveUtils.getInt(PARAM_COLUMN_ID, params);
		if (columnId != null) {
			return columnId;
		} else {
			return null;
		}
	}
	
	
	
	private Integer getTypeIds(Map<String, TemplateModel> params)throws TemplateException{
		Integer typeIds = DirectiveUtils.getInt(TYPE_IDS, params);
		if (typeIds != null) {
			return typeIds;
		} else {
			return null;
		}
	}



	@Autowired
	private ContentMng contentMng;
}
