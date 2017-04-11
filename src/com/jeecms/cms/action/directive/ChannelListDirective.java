package com.jeecms.cms.action.directive;

import static com.jeecms.cms.Constants.TPL_STYLE_LIST;
import static com.jeecms.cms.Constants.TPL_SUFFIX;
import static com.jeecms.common.web.Constants.UTF8;
import static com.jeecms.common.web.freemarker.DirectiveUtils.OUT_LIST;
import static com.jeecms.core.web.util.FrontUtils.PARAM_STYLE_LIST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jeecms.cms.action.directive.abs.AbstractChannelDirective;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.common.util.RedisUtil;
import com.jeecms.common.web.freemarker.DefaultObjectWrapperBuilderFactory;
import com.jeecms.common.web.freemarker.DirectiveUtils;
import com.jeecms.common.web.freemarker.DirectiveUtils.InvokeType;
import com.jeecms.common.web.freemarker.ParamsRequiredException;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.web.util.FrontUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 栏目列表标签
 */
public class ChannelListDirective extends AbstractChannelDirective {
	private static final Logger logger = Logger.getLogger(ChannelListDirective.class);
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "channel_list";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		Integer parentId = DirectiveUtils.getInt(PARAM_PARENT_ID, params);
		Integer siteId = DirectiveUtils.getInt(PARAM_SITE_ID, params);
		boolean hasContentOnly = getHasContentOnly(params);
		List<Channel> list=new ArrayList<Channel>();
		String key = null ;
		if(parentId != null){
			key = parentId.toString()+"s";
			try {
				list=RedisUtil.getListC(key);
			} catch (Exception e) {
				list = channelMng.getChildListForTag(parentId, hasContentOnly);
				logger.error("redis读取异常,切回数据库读取", e);
			}
			if(list==null||list.size()==0){
				list = channelMng.getChildListForTag(parentId, hasContentOnly);
				RedisUtil.setListC(key, list);
			}
		}else{
			if (siteId == null) {
				siteId = site.getId();
				key = siteId+"s";
			}
			try {
				list=RedisUtil.getListC(key);
			} catch (Exception e) {
				list = channelMng.getChildListForTag(parentId, hasContentOnly);
				logger.error("redis读取异常,切回数据库读取", e);
			}
			if(list==null||list.size()==0){
				list = channelMng.getTopListForTag(siteId, hasContentOnly);
				RedisUtil.setListC(key, list);
			}
			//RedisUtil.setListC(siteId.toString(), list);
		}


		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_LIST, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(list));
		Map<String, TemplateModel> origMap = DirectiveUtils
				.addParamsToVariable(env, paramWrap);
		InvokeType type = DirectiveUtils.getInvokeType(params);
		String listStyle = DirectiveUtils.getString(PARAM_STYLE_LIST, params);
		if (InvokeType.sysDefined == type) {
			if (StringUtils.isBlank(listStyle)) {
				throw new ParamsRequiredException(PARAM_STYLE_LIST);
			}
			env.include(TPL_STYLE_LIST + listStyle + TPL_SUFFIX, UTF8, true);
		} else if (InvokeType.userDefined == type) {
			if (StringUtils.isBlank(listStyle)) {
				throw new ParamsRequiredException(PARAM_STYLE_LIST);
			}
			FrontUtils.includeTpl(TPL_STYLE_LIST, site, env);
		} else if (InvokeType.custom == type) {
			FrontUtils.includeTpl(TPL_NAME, site, params, env);
		} else if (InvokeType.body == type) {
			body.render(env.getOut());
		} else {
			throw new RuntimeException("invoke type not handled: " + type);
		}
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
	}
}
