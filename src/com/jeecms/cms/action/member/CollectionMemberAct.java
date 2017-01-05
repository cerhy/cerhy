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
		String s = request.getParameter("CUId");
		if(Integer.parseInt(s) == user.getId()){
			try {
				response.getWriter().println("1");
			} catch (IOException e) {
				e.printStackTrace();
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

	@RequestMapping(value = "/blog/collection_list.jspx")
	public String collection_list_blog(String queryTitle, Integer queryChannelId,
			Integer pageNo, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		model = getColumn(request,model,user);
	    model = getChannel(request,model,user,site);
		
		//获取链接列表
	    String linkUrl=user.getLinkUrl();
	    List listU=new ArrayList();
	    if(linkUrl!=null){
	    	String[] strs=linkUrl.split(" ");
	    	String newUrl="";
	    	for(int i=0;i<strs.length;i++){
	    		if(i!=strs.length-1){
	    			if(!strs[i].contains("http")&&strs[i+1].contains("http")){
	    				newUrl+="~"+strs[i]+" ";
	    			}else{
	    				newUrl+=strs[i]+" ";
	    			}
	    		}else{
	    			if(!strs[i].contains("http")){
	    				newUrl+="~"+strs[i]+" ";
	    			}else{
	    				newUrl+=strs[i]+" ";
	    			}
	    		}
	    	}
	    	String[] str=newUrl.split("~");
	    	for(int j=0;j<str.length;j++){
	    		Map<String,Object> map=new HashMap<String,Object>();
	    		String[] st=str[j].toString().split(" ");
	    		List lists=new ArrayList();
	    		String newName="";
	    		for(int k=0;k<st.length;k++){
	    			if(st[0].contains("http")){
	    				newName="";
	    			}else{
	    				newName=st[0];
	    			}
	    			lists.add(st[k]);
	    		}
	    		map.put(newName, lists);
	    		listU.add(map);
	    	}
	    	model.addAttribute("urlList", listU);
	    	model.addAttribute("linkUrls", linkUrl.replaceAll(" ", "\r\n"));
	    }else{
	    	model.addAttribute("urlList",listU);
	    	model.addAttribute("linkUrls","");
	    	
	    }
	    //获取好友列表
	    String friends=user.getFriends();
	    List listF = new ArrayList<>();
	    if(friends!=null){
	    	
	    	String[] strs=friends.split(" ");
	    	for(int i=0;i<strs.length;i++){
	    		String[] str=strs[i].split("=");
	    		Map<String,Object> map=new HashMap<String,Object>();
	    		CmsUser u=channelMng.findUserImage(str[1].toString());
	    		String newName=str[0]+"~"+u.getId()+"~"+u.getUserExt().getUserImg();
	    		map.put(newName, u.getUserExt().getUserImg());
	    		listF.add(map);
	    	}
	    	model.addAttribute("friendsList", listF);
	    	model.addAttribute("friends", friends.replaceAll(" ", "\r\n"));
	    }else{
	    	model.addAttribute("friendsList", listF);
	    	model.addAttribute("friends","");
	    }
		
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
	
	public ModelMap getColumn(HttpServletRequest request,ModelMap model,CmsUser user){
		if ((null != user.getGroup().getId())&&(4 !=user.getGroup().getId()) || 5 != user.getGroup().getId()){
			String path = request.getSession().getServletContext().getRealPath("/");
			List<Columns> columnsList = (new BlogDao()).findByUserId(user.getId(), path);
			model.addAttribute("columnsList", columnsList);
		}
		return model;
	}
	
	public ModelMap getChannel(HttpServletRequest request,ModelMap model,CmsUser user,CmsSite site) {
		// 学科教研，市县教研专用
		List<Channel> channelList2 = null;
		List<Channel> channelList3 = null;
		if (null != user.getGroup()) {
			// 获得本站栏目列表
			Set<Channel> rights = user.getGroup().getContriChannels();
			List<Channel> topList = channelMng.getTopList(site.getId(), true);
			List<Channel> channelList = Channel.getListForSelect(topList, rights, true);

			channelList2 = new ArrayList<Channel>();// 学科教研
			channelList3 = new ArrayList<Channel>();// 市县教研

			if (4 == user.getGroup().getId()) {
				for (Channel c : channelList) {
					if (c.getId() == 98) {
						channelList2.add(c);
					} else if (null != c.getParent()) {
						if (c.getParent().getId() == 98) {
							channelList2.add(c);
						} else if (null != c.getParent().getParent()) {
							if (c.getParent().getParent().getId() == 98)
								channelList2.add(c);
						}
					}
				}
			} else if (5 == user.getGroup().getId()) {
				for (Channel c : channelList) {
					if (c.getId() == 98) {
						channelList3.add(c);
					} else if (null != c.getParent()) {
						if (c.getParent().getId() == 98) {
							channelList3.add(c);
						} else if (null != c.getParent().getParent()) {
							if (c.getParent().getParent().getId() == 98)
								channelList3.add(c);
						}
					}
				}

			}

			if (null != channelList2 && channelList2.size() > 0) {
				model.addAttribute("channelList", channelList2);
			} else if (null != channelList3 && channelList3.size() > 0) {
				model.addAttribute("channelList", channelList3);
			}
		}
		return model;
	}
	
	@Autowired
	protected ChannelMng channelMng;
	
}
