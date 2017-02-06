package com.jeecms.cms.action.blog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.jeecms.cms.dao.main.impl.BlogDao;
import com.jeecms.cms.entity.assist.CmsBlogVisitor;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.entity.main.Focus;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.impl.ColumnsMng;
import com.jeecms.cms.manager.main.impl.FocusMng;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.web.util.CmsUtils;

public class BlogCommon {
	
	public ModelMap getColumn(HttpServletRequest request,ModelMap model,CmsUser user){
		int groupId = user.getGroup().getId();
		if (4!=groupId){
			if(5 != groupId){
				List<Columns> columnsList = columnsMng.getColumnsByUserId(user.getId());
				model.addAttribute("columnsList", columnsList);
			}
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
			int groupId = user.getGroup().getId();
			if (4 == groupId) {
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
			} else if (5 == groupId) {
				for (Channel c : channelList) {
					if (c.getId() == 168) {
						channelList3.add(c);
					} else if (null != c.getParent()) {
						if (c.getParent().getId() == 168) {
							channelList3.add(c);
						} else if (null != c.getParent().getParent()) {
							if (c.getParent().getParent().getId() == 168)
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
	
	//获取链接列表
	public ModelMap getLinks(ModelMap model, CmsUser user) {
		String linkUrl = user.getLinkUrl();
		List<Object> listU = new ArrayList<Object>();
		if (linkUrl != null) {
			String[] strs = linkUrl.split(" ");
			String newUrl = "";
			for (int i = 0; i < strs.length; i++) {
				if (i != strs.length - 1) {
					if (!strs[i].contains("http") && strs[i + 1].contains("http")) {
						newUrl += "~" + strs[i] + " ";
					} else {
						newUrl += strs[i] + " ";
					}
				} else {
					if (!strs[i].contains("http")) {
						newUrl += "~" + strs[i] + " ";
					} else {
						newUrl += strs[i] + " ";
					}
				}
			}
			String[] str = newUrl.split("~");
			for (int j = 0; j < str.length; j++) {
				Map<String, Object> map = new HashMap<String, Object>();
				String[] st = str[j].toString().split(" ");
				List<Object> lists = new ArrayList<Object>();
				String newName = "";
				for (int k = 0; k < st.length; k++) {
					if (st[0].contains("http")) {
						newName = "";
					} else {
						newName = st[0];
					}
					lists.add(st[k]);
				}
				map.put(newName, lists);
				listU.add(map);
			}
			model.addAttribute("urlList", listU);
			model.addAttribute("linkUrls", linkUrl.replaceAll(" ", "\r\n"));
		} else {
			model.addAttribute("urlList", listU);
			model.addAttribute("linkUrls", "");
		}
		return model;
	}
	
	//获取好友列表
	public ModelMap getFriends(ModelMap model, CmsUser user) {
		String friends = user.getFriends();
		List<Object> listF = new ArrayList<Object>();
		if (friends != null) {

			String[] strs = friends.split(" ");
			for (int i = 0; i < strs.length; i++) {
				String[] str = strs[i].split("=");
				Map<String, Object> map = new HashMap<String, Object>();
				CmsUser u = channelMng.findUserImage(str[1].toString());
				if (null == u) {
					String newName = "";
					map.put(newName, null);
				} else {
					String newName = str[0] + "~" + u.getId() + "~" + u.getUserExt().getUserImg();
					map.put(newName, u.getUserExt().getUserImg());
				}
				listF.add(map);
			}
			model.addAttribute("friendsList", listF);
			model.addAttribute("friends", friends.replaceAll(" ", "\r\n"));
		} else {
			model.addAttribute("friendsList", listF);
			model.addAttribute("friends", "");
		}
		return model;
	}
	
	public ModelMap blog_focus_find(Integer userid,HttpServletRequest request, ModelMap model) {
		if(null == userid){
			CmsUser user = CmsUtils.getUser(request);
			userid = user.getId();
		}
		List<List<Focus>> list = focusMng.findByUserId(userid);
		if (null != list) {
			if (null != list.get(0)) {
				model.addAttribute("focus", list.get(0));
			}
			if (null != list.get(1)) {
				model.addAttribute("fans", list.get(1));
			}
		}
		return model;
	}
	
	public ModelMap getMaxFocus(HttpServletRequest request,  ModelMap model){
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Focus> list = (new BlogDao()).findMaxFocusCount( path);
	    List<Focus> l = null;
	    List<CmsUser> u = new ArrayList<CmsUser>();
	    if(null != list){
	    	if(list.size()>5){
	    		l = new ArrayList<Focus>();
	    		for(int i =0;i<5;i++){
	    			l.add(list.get(i));
	    		}
	    	}
	    	if(null != l){
	    		for(Focus f : l){
	    			u.add(cmsUserMng.findById(f.getFocusUserId()));
	    		}
	    	}else{
	    		for(Focus f : list){
	    			u.add(cmsUserMng.findById(f.getFocusUserId()));
	    		}
	    	}
	    	model.addAttribute("focusMax", u);
	    }
	    return model;
	}
	
	public boolean isNumeric(String str){ 
		String regex = "[0-9]+";
		   Pattern pattern = Pattern.compile(regex); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	
	/**
	 * 获取文章总数
	 **/
	public ModelMap getTotalArticleNum(ModelMap model, CmsUser user){
		int articleCount=contentMng.getTotalArticleNum(user);
		model.addAttribute("articleCount", articleCount);
		return model;
	}
	
	/**
	 * 获取自己文章下评论总数
	 **/
	public ModelMap getTotalCommentNum(ModelMap model, CmsUser user){
		int commentCount=contentMng.getTotalCommentNum(user);
		model.addAttribute("commentCount", commentCount);
		return model;
	}
	/**
	 * 获取我评论总数
	 **/
	public ModelMap getTotalCoverCommentNum(ModelMap model, CmsUser user){
		int coverCommentCount=contentMng.getTotalCoverCommentNum(user);
		model.addAttribute("coverCommentCount", coverCommentCount);
		return model;
	}
	/**
	 * 获取我的博客文章总数
	 **/
	public ModelMap getTotalReadNum(ModelMap model, CmsUser user){
		int readCount=contentMng.getTotalReadNum(user);
		model.addAttribute("readCount", readCount);
		return model;
	}
	
	/**
	 * 获取博客访问者/日期
	 **/
	public ModelMap getAllVistor(HttpServletRequest request, ModelMap model,CmsUser user) {
		List<CmsBlogVisitor> visitorList=contentMng.getgetAllVistor(user);
		model.addAttribute("visitorList", visitorList);
		return model;
	}
	
	
	@Autowired
	protected CmsUserMng cmsUserMng;
	@Autowired
	protected ColumnsMng columnsMng;
	@Autowired
	protected FocusMng focusMng;
	@Autowired
	protected ChannelMng channelMng;
	
	@Autowired
	private ContentMng contentMng;

	

}
