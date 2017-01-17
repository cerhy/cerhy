package com.jeecms.cms.action.member;

import static com.jeecms.cms.Constants.TPLDIR_MEMBER;
import static com.jeecms.cms.Constants.TPLDIR_BLOG;
import static com.jeecms.common.page.SimplePage.cpn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeecms.cms.action.blog.BlogCommon;
import com.jeecms.cms.dao.main.impl.BlogDao;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.web.CookieUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.MemberConfig;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

/**
 * 收藏信息Action
 * 
 * @author
 * 
 */
@Controller
public class CollectionMemberAct {

	public static final String COLLECTION_LIST = "tpl.collectionList";

	/**
	 * 我的收藏信息
	 * 
	 * 如果没有登录则跳转到登陆页
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/collection_list.jspx")
	public String collection_list(String queryTitle, Integer queryChannelId,
			Integer pageNo, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		Pagination p = contentMng.getPageForCollection(site.getId(), user
				.getId(), cpn(pageNo), CookieUtils.getPageSize(request));
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(queryTitle)) {
			model.addAttribute("queryTitle", queryTitle);
		}
		if (queryChannelId != null) {
			model.addAttribute("queryChannelId", queryChannelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, COLLECTION_LIST);
	}
	
	@RequestMapping(value = "/blog/checkout.jspx")
	public void checkout( 
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		CmsUser user = CmsUtils.getUser(request);
		if(null != user){
			String s = request.getParameter("CUId");
			if(Integer.parseInt(s) != user.getId()){
				try {
					response.getWriter().println("1");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@RequestMapping(value = "/member/collect.jspx")
	public void collect(Integer cId, Integer operate,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject object = new JSONObject();
		if (user == null) {
			object.put("result", false);
		} else {
			object.put("result", true);
			
			userMng.updateUserConllection(user,cId,1);
		}
		ResponseUtils.renderJson(response, object.toString());
	}
	
	@RequestMapping(value = "/member/collect_cancel.jspx")
	public String  collect_cancel(Integer[] cIds,Integer pageNo,HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		for(Integer  id:cIds){
			userMng.updateUserConllection(user,id,0);
		}
		return collection_list(null, null, pageNo, request, response, model);
	}

	@RequestMapping(value = "/member/collect_exist.jspx")
	public void collect_exist(Integer cId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws JSONException {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject object = new JSONObject();
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			object.put("result", false);
		} else {
			if (cId!=null&&user.getCollectContents().contains(contentMng.findById(cId))) {
				object.put("result", true);
			} else {
				object.put("result", false);
			}
		}
		ResponseUtils.renderJson(response, object.toString());
	}

	@Autowired
	private ContentMng contentMng;
	@Autowired
	private CmsUserMng userMng;
	@Autowired
	protected BlogCommon blogCommon;

	@RequestMapping(value = "/blog/collection_list.jspx")
	public String collection_list_blog(String queryTitle, Integer queryChannelId,
			Integer pageNo, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.blog_focus_find(null,request,model);
	    model = blogCommon.getLinks(model,user);
		model = blogCommon.getFriends(model,user);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForCollection(site.getId(), user
				.getId(), cpn(pageNo), CookieUtils.getPageSize(request));
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(queryTitle)) {
			model.addAttribute("queryTitle", queryTitle);
		}
		if (queryChannelId != null) {
			model.addAttribute("queryChannelId", queryChannelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_BLOG, COLLECTION_LIST);
	}
	
	@RequestMapping(value = "/blog/collect_cancel.jspx")
	public String  collect_cancel_blog(Integer[] cIds,Integer pageNo,HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		 
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		for(Integer  id:cIds){
			userMng.updateUserConllection(user,id,0);
		}
		return collection_list_blog(null, null, pageNo, request, response, model);
	}
	
	@Autowired
	protected ChannelMng channelMng;
	
}
