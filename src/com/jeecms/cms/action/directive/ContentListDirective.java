package com.jeecms.cms.action.directive;

import static com.jeecms.cms.Constants.TPL_STYLE_LIST;
import static com.jeecms.cms.Constants.TPL_SUFFIX;
import static com.jeecms.common.web.Constants.UTF8;
import static com.jeecms.common.web.freemarker.DirectiveUtils.OUT_LIST;
import static com.jeecms.core.web.util.FrontUtils.PARAM_STYLE_LIST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.jeecms.cms.action.directive.abs.AbstractContentDirective;
import com.jeecms.cms.entity.main.Content;
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
 * 内容列表标签
 */
public class ContentListDirective extends AbstractContentDirective {
	private static final Logger logger = Logger.getLogger(ContentListDirective.class);
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "content_list";

	/**
	 * 输入参数，文章ID。允许多个文章ID，用","分开。排斥其他所有筛选参数。
	 */
	public static final String PARAM_IDS = "ids";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		String channelIds=params.get("channelId").toString();
		List<Content> list=new ArrayList<Content>();
		String key = channelIds;  
		try {
			list=RedisUtil.getList(key);
		} catch (Exception e) {
			list = getList(params, env);
			logger.error("redis读取异常,切回数据库读取", e);
		}
//		System.out.println("开始时间1:"+new Date());
		if(list==null||list.size()<=0){
			list = getList(params, env);
			RedisUtil.setList(key, list);
		}
//		System.out.println("结束时间2:"+new Date());
		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_LIST, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(list));
		Map<String, TemplateModel> origMap = DirectiveUtils
				.addParamsToVariable(env, paramWrap);
//		System.out.println("结束时间3:"+new Date());
		InvokeType type = DirectiveUtils.getInvokeType(params);
		String listStyle = DirectiveUtils.getString(PARAM_STYLE_LIST, params);
		try {
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
//				System.out.println("结束时间4:"+new Date());
				body.render(env.getOut());
//				System.out.println("结束时间5:"+new Date());
			} else {
				throw new RuntimeException("invoke type not handled: " + type);
			}
		} catch (Exception e) {
//			System.out.println("---------"+params.get("channelId").toString());
			e.printStackTrace();
		}
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
//		System.out.println("结束时间6:"+new Date());
//		System.out.println("结束");
	}

	@SuppressWarnings("unchecked")
	protected List<Content> getList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		Integer[] ids = DirectiveUtils.getIntArray(PARAM_IDS, params);
		if (ids != null) {
			return contentMng.getListByIdsForTag(ids, getOrderBy(params));
		} else {
//			System.out.println("时间st:"+new Date());
			List<Content> list=(List<Content>) super.getData(params, env);
//			System.out.println("时间en:"+new Date());
			return list;
		}
	}

	@Override
	protected boolean isPage() {
		return false;
	}
}
