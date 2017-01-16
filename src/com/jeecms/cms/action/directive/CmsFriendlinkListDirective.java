package com.jeecms.cms.action.directive;

import static com.jeecms.common.web.freemarker.DirectiveUtils.OUT_LIST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeecms.cms.entity.assist.CmsFriendlink;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.manager.assist.CmsFriendlinkMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.common.web.freemarker.DefaultObjectWrapperBuilderFactory;
import com.jeecms.common.web.freemarker.DirectiveUtils;
import com.jeecms.core.web.util.FrontUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 友情链接类别列表标签
 */
public class CmsFriendlinkListDirective implements TemplateDirectiveModel {
	/**
	 * 输入参数，站点ID。
	 */
	public static final String PARAM_SITE_ID = "siteId";
	/**
	 * 输入参数，类别ID。
	 */
	public static final String PARAM_CTG_ID = "ctgId";
	/**
	 * 输入参数，是否显示。
	 */
	public static final String PARAM_ENABLED = "enabled";
	/**
	 * 输入参数，栏目id
	 */
	public static final String PARAM_CHANNEL_ID = "channelId";
	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Integer siteId = getSiteId(params);
		if (siteId == null) {
			siteId = FrontUtils.getSite(env).getId();
		}
		Integer ctgId = getCtgId(params);
		Boolean enabled = getEnabled(params);
		if (enabled == null) {
			enabled = true;
		}
		Integer channelId = getChannelId(params);
		//首页一级栏目判断首次点击显示
		if(channelId==98||channelId==99||channelId==100||channelId==101
				||channelId==102||channelId==103||channelId==104||channelId==168){
			Channel cc = channelMng.findById(channelId);
			List<Channel> topList = new ArrayList<Channel>();
			topList.add(cc);
			List<Channel> channelList = Channel.getListForSelect(topList, null, true);
			channelId=channelList.get(2).getId();
		}
		//二级栏目获取第一个子栏目处理方式
		if(channelId==282){
			Channel cc = channelMng.findById(channelId);
			List<Channel> topList = new ArrayList<Channel>();
			topList.add(cc);
			List<Channel> channelList = Channel.getListForSelect(topList, null, true);
			channelId=channelList.get(1).getId();
		}
		
		List<CmsFriendlink> list = cmsFriendlinkMng.getList1(siteId, ctgId,channelId,
				enabled);

		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_LIST, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(list));
		Map<String, TemplateModel> origMap = DirectiveUtils
				.addParamsToVariable(env, paramWrap);
		body.render(env.getOut());
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
	}

	private Integer getSiteId(Map<String, TemplateModel> params)
			throws TemplateException {
		return DirectiveUtils.getInt(PARAM_SITE_ID, params);
	}

	private Integer getCtgId(Map<String, TemplateModel> params)
			throws TemplateException {
		return DirectiveUtils.getInt(PARAM_CTG_ID, params);
	}

	private Integer getChannelId(Map<String, TemplateModel> params)
			throws TemplateException {
		return DirectiveUtils.getInt(PARAM_CHANNEL_ID, params);
	}
	
	private Boolean getEnabled(Map<String, TemplateModel> params)
			throws TemplateException {
		return DirectiveUtils.getBool(PARAM_ENABLED, params);
	}

	@Autowired
	private CmsFriendlinkMng cmsFriendlinkMng;
	
	@Autowired
	private ChannelMng channelMng;
}
