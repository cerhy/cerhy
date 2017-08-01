package com.jeecms.cms.action.blog;

import static com.jeecms.common.page.SimplePage.cpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.jeecms.cms.dao.main.impl.BlogDao;
import com.jeecms.cms.entity.assist.CmsBlogVisitor;
import com.jeecms.cms.entity.assist.CmsJoinGroup;
import com.jeecms.cms.entity.assist.CmsPersonalChannel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.entity.main.Focus;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.impl.ColumnsMng;
import com.jeecms.cms.manager.main.impl.FocusMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsUserMng;

public class BlogCommon {
	Logger log = (Logger) Logger.getInstance(BlogCommon.class) ;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ModelMap getColumn(HttpServletRequest request,ModelMap model,CmsUser user){
		if(user!=null){
			List<Columns> columnsList = columnsMng.getOneColumnsByUserId(user.getId());
			List listc=null;
			if(null!=columnsList&&columnsList.size()>0){
				listc=new ArrayList();
				for(int i=0;i<columnsList.size();i++){
					Map<String,Object> map=new HashMap<String,Object>();
					List<Columns> twoList=columnsMng.findTwoByParentId(columnsList.get(i).getColumnId());
					List lists=new ArrayList();
					String keyName="";
					if(null!=twoList&&twoList.size()>0){
						for(int j=0;j<twoList.size();j++){
							if(null!=twoList.get(j).getUniqueCode()){
								lists.add(twoList.get(j).getColumnId()+"="+twoList.get(j).getColumnName()+"=(验证码:"+twoList.get(j).getUniqueCode()+")");
							}else{
								lists.add(twoList.get(j).getColumnId()+"="+twoList.get(j).getColumnName());
							}
						}
						keyName=columnsList.get(i).getColumnId()+"="+columnsList.get(i).getColumnName();
					}else{
						if(null!=columnsList.get(i).getUniqueCode()){
							lists.add(columnsList.get(i).getColumnId()+"="+columnsList.get(i).getColumnName()+"=(验证码:"+columnsList.get(i).getUniqueCode()+")");
						}else{
							lists.add(columnsList.get(i).getColumnId()+"="+columnsList.get(i).getColumnName());
						}
					}
					map.put(keyName,lists);
					listc.add(map);
				}
			}
			model.addAttribute("columnsList", listc);
		}else{
			return model.addAttribute("columnsList", null);
		}
		return model;
	}
	
	/**
	 * 查询收录人的栏目id
	 * @param request
	 * @param model
	 * @param user
	 * @return ModelMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ModelMap getEmbodyColumn(ModelMap model,CmsUser user){
		if(user!=null){
			List<Columns> columnsList = columnsMng.getOneColumnsByUserId(user.getId());
			List listc=null;
			if(null!=columnsList&&columnsList.size()>0){
				listc=new ArrayList();
				for(int i=0;i<columnsList.size();i++){
					Map<String,Object> map=new HashMap<String,Object>();
					List<Columns> twoList=columnsMng.findTwoByParentId(columnsList.get(i).getColumnId());
					List lists=new ArrayList();
					String keyName="";
					if(null!=twoList&&twoList.size()>0){
						for(int j=0;j<twoList.size();j++){
							if(null!=twoList.get(j).getUniqueCode()){
								lists.add(twoList.get(j).getColumnId()+"="+twoList.get(j).getColumnName()+"=(验证码:"+twoList.get(j).getUniqueCode()+")");
							}else{
								lists.add(twoList.get(j).getColumnId()+"="+twoList.get(j).getColumnName());
							}
						}
						keyName=columnsList.get(i).getColumnId()+"="+columnsList.get(i).getColumnName();
					}else{
						if(null!=columnsList.get(i).getUniqueCode()){
							lists.add(columnsList.get(i).getColumnId()+"="+columnsList.get(i).getColumnName()+"=(验证码:"+columnsList.get(i).getUniqueCode()+")");
						}else{
							lists.add(columnsList.get(i).getColumnId()+"="+columnsList.get(i).getColumnName());
						}
					}
					map.put(keyName,lists);
					listc.add(map);
				}
			}
			model.addAttribute("emBodyColumnsList", listc);
		}else{
			return model.addAttribute("emBodyColumnsList", null);
		}
		return model;
	}
	
	
	public ModelMap getChannel(HttpServletRequest request,ModelMap model,CmsUser user,CmsSite site) {
		// 学科教研，市县教研专用
		List<CmsPersonalChannel> channelList=null;
		try {
			if (null != user && null != user.getGroup()) {
				int groupId = user.getGroup().getId();
				if (4 == groupId||5 == groupId) {
					channelList = columnsMng.getPersonChannel(user);
				} 
				model.addAttribute("channelList", channelList);
			}
		} catch (Exception e) {
			log.error("博客获取栏目出错", e);
		}
		return model;
	}
	
//	public ModelMap getChannel(HttpServletRequest request,ModelMap model,CmsUser user,CmsSite site) {
//		// 学科教研，市县教研专用
//		List<Channel> channelList2 = null;
//		List<Channel> channelList3 = null;
//		if (null != user.getGroup()) {
//			// 获得本站栏目列表
//			Set<Channel> rights = user.getGroup().getContriChannels();
//			List<Channel> topList = channelMng.getTopList(site.getId(), true);
//			
//			channelList2 = new ArrayList<Channel>();// 学科教研
//			channelList3 = new ArrayList<Channel>();// 市县教研
//			int groupId = user.getGroup().getId();
//			if (4 == groupId) {
//				List<Channel> channelList = Channel.getListForSelect(topList, rights, true);
//				for (Channel c : channelList) {
//					if (c.getId() == 98) {
//						channelList2.add(c);
//					} else if (null != c.getParent()) {
//						if (c.getParent().getId() == 98) {
//							channelList2.add(c);
//						} else if (null != c.getParent().getParent()) {
//							if (c.getParent().getParent().getId() == 98)
//								channelList2.add(c);
//						}
//					}
//				}
//			} else if (5 == groupId) {
//				List<Channel> channelList = Channel.getListForSelect(topList, rights, true);
//				for (Channel c : channelList) {
//					if (c.getId() == 168) {
//						channelList3.add(c);
//					} else if (null != c.getParent()) {
//						if (c.getParent().getId() == 168) {
//							channelList3.add(c);
//						} else if (null != c.getParent().getParent()) {
//							if (c.getParent().getParent().getId() == 168)
//								channelList3.add(c);
//						}
//					}
//				}
//				
//			}
//			
//			if (null != channelList2 && channelList2.size() > 0) {
//				model.addAttribute("channelList", channelList2);
//			} else if (null != channelList3 && channelList3.size() > 0) {
//				model.addAttribute("channelList", channelList3);
//			}
//		}
//		return model;
//	}
//	
	/*//获取链接列表
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
	}*/
	
	//获取好友列表
	public ModelMap getFriends(int id, ModelMap model,int pageNo) {
		String friends = cmsUserMng.findById(id).getFriends();
		Pagination p = null;
		if (friends != null) {
			List<CmsUser> list = new ArrayList<CmsUser>();
			String[] strs = friends.split(" ");
			for (int i = 0; i < strs.length; i++) {
				String[] str = strs[i].split("=");
				if(str[1].toString()!=null){
					CmsUser nc=channelMng.findUserImage(str[1].toString());
					if(null!=nc){
						nc.setNicknames(str[0].toString());
						list.add(nc);
					}
				}
			}
			model.addAttribute("friends", friends.replaceAll(" ", "\r\n"));
			//分页显示好友数据
			List<CmsUser> listPage = new ArrayList<CmsUser>();
			if(null != list && list.size()>0){
				if(pageNo*24 <= list.size()){
					for(int i=(pageNo-1)*24;i<pageNo*24;i++){
						listPage.add(list.get(i));
					}
				}else{
					for(int i=(pageNo-1)*24;i<list.size();i++){
						listPage.add(list.get(i));
					}
				}
				p = new Pagination(pageNo, 24, list.size());	
				p.setList(listPage);
			}
		}else{
			model.addAttribute("friends", "");
		}
		model.addAttribute("paginations", p);
		return model;
	}
	//获取好友列表左侧
	public ModelMap getFriendLeft(int id, ModelMap model,int pageNo) {
		String friends = cmsUserMng.findById(id).getFriends();
		Pagination p = null;
		if (friends != null) {
			List<CmsUser> list = new ArrayList<CmsUser>();
			String[] strs = friends.split(" ");
			for (int i = 0; i < strs.length; i++) {
				if(i<10){
					String[] str = strs[i].split("=");
					if(str[1].toString()!=null){
						CmsUser nc=channelMng.findUserImage(str[1].toString());
						if(null!=nc){
							nc.setNicknames(str[0].toString());
							list.add(nc);
						}
					}
				}else{
					break;
				}
			}
			model.addAttribute("friends", friends.replaceAll(" ", "\r\n"));
			p = new Pagination();
			p.setList(list);
		}else{
			model.addAttribute("friends", "");
		}
		model.addAttribute("paginationLeft", p);
		return model;
	}
	
	public ModelMap getBlogFocus(int id, ModelMap model,Integer pageNo) {
		Pagination p = focusMng.pagingQueryFocus(id,cpn(pageNo),26);
		model.addAttribute("pagination", p);
		return model;
	}
	
	public ModelMap getBlogFans(int id, ModelMap model,Integer pageNo) {
		Pagination p = focusMng.pagingQueryFans(id,cpn(pageNo),26);
		model.addAttribute("pagination", p);
		return model;
	}
	
	public ModelMap getStarBlogger(HttpServletRequest request,  ModelMap model){
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Focus> list = (new BlogDao()).findMaxFocusCount( path);
	    List<Focus> l = null;
	    List<CmsUser> u = new ArrayList<CmsUser>();
	    if(null != list){
	    	if(list.size()>3){
	    		l = new ArrayList<Focus>();
	    		for(int i =0;i<3;i++){
	    			l.add(list.get(i));
	    		}
	    	}
	    	if(null != l){
	    		for(Focus f : l){
	    			if(cmsUserMng.findById(f.getFocusUserId())!=null){
	    				u.add(cmsUserMng.findById(f.getFocusUserId()));
	    			}
	    		}
	    	}else{
	    		for(Focus f : list){
	    			if(cmsUserMng.findById(f.getFocusUserId())!=null){
	    				u.add(cmsUserMng.findById(f.getFocusUserId()));
	    			}
	    		}
	    	}
	    	model.addAttribute("starBlogger", u);
	    	if(list.size()>3){
	    		model.addAttribute("moreStarBloggerExist",1);
	    	}
	    	
	    }
	    return model;
	}
	
	public ModelMap getMoreStarBlogger(HttpServletRequest request, ModelMap model,int pageNo) {
		List<CmsUser> u = null;
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Focus> list = (new BlogDao()).findMaxFocusCount( path);
		Pagination p = null; 
		if(pageNo>3){
			pageNo =3;
		}

		if(null != list && list.size()>0){
			u = new ArrayList<CmsUser>();
			if(pageNo*24 <= list.size()){
				for(int i=(pageNo-1)*24;i<pageNo*24;i++){
					if(cmsUserMng.findById(list.get(i).getFocusUserId())!=null){
						
						u.add(cmsUserMng.findById(list.get(i).getFocusUserId()));
					}
				}
			}else{
				for(int i=(pageNo-1)*24;i<list.size();i++){
					if(cmsUserMng.findById(list.get(i).getFocusUserId())!=null){
						
						u.add(cmsUserMng.findById(list.get(i).getFocusUserId()));
					}
				}
			}
			p = new Pagination(pageNo, 24, list.size());
			p.setList(u);
		}
		
		model.addAttribute("pagination", p);
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
	public int getTotalArticleNum(ModelMap model, CmsUser user){
		int articleCount=0;
		if(user!=null){
			articleCount=contentMng.getTotalArticleNum(user);
		}
		//model.addAttribute("articleCount", articleCount);
		return articleCount;
	}
	
	/**
	 * 获取自己文章下评论总数
	 **/
	public ModelMap getTotalCommentNum(ModelMap model, CmsUser user){
		int commentCount=0;
		if(user!=null){
			commentCount=contentMng.getTotalCommentNum(user);
		}
		model.addAttribute("commentCount", commentCount);
		return model;
	}
	/**
	 * 获取我评论总数
	 **/
	public ModelMap getTotalCoverCommentNum(ModelMap model, CmsUser user){
		int coverCommentCount=0;
		if(user!=null){
			coverCommentCount=contentMng.getTotalCoverCommentNum(user);
		}
		model.addAttribute("coverCommentCount", coverCommentCount);
		return model;
	}
	/**
	 * 获取我的博客文章总数
	 **/
	public ModelMap getTotalReadNum(ModelMap model, CmsUser user){
		int readCount=0;
		if(user!=null){
			readCount=contentMng.getTotalReadNum(user);
		}
		model.addAttribute("readCount", readCount);
		return model;
	}
	
	/**
	 * 获取博客访问者/日期
	 **/
	public ModelMap getAllVistor(HttpServletRequest request, ModelMap model,CmsUser user) {
		if(user!=null){
			List<CmsBlogVisitor> visitorList=contentMng.getgetAllVistor(user);
			model.addAttribute("visitorList", visitorList);
		}else{
			model.addAttribute("visitorList", "");
		}
		return model;
	}
	
	/**
	 * 查询我已经加入的群组
	 **/
	public ModelMap getAlreadyJoinGroup(HttpServletRequest request,ModelMap model, CmsUser user) {
		if(user!=null){
			List<CmsJoinGroup> joinGroupList=contentMng.getAlreadyJoinGroup(user);
			model.addAttribute("joinGroupList", joinGroupList);
		}else{
			model.addAttribute("joinGroupList", "");
		}
		return model;
	}
	
	/**
	 * 查询是否已经添加好友
	 **/
	public ModelMap getAddFriends(HttpServletRequest request, ModelMap model,CmsUser userT, CmsUser user) {
		if(user!=null){
			if(StringUtils.isNotEmpty(user.getFriends())){
				if(user.getFriends().contains(userT.getUsername())){
					model.addAttribute("friendCheck", 1);//已添加好友
				}else{
					model.addAttribute("friendCheck", 0);//未添加好友
				}
			}else{
				model.addAttribute("friendCheck", 0);//未添加好友
			}
		}else{
			model.addAttribute("friendCheck", null);
		}
		return model;
	}
	/**
	 *查询文章是否是发送的还是收录的
	 **/
	public ModelMap getContentSendType(ModelMap model,Integer contentId, CmsUser user) {
		if(user!=null){
			Integer contentSentType = cmsUserMng.getContentSendType(contentId, user.getId());
			model.addAttribute("contentSentType", contentSentType);
		}else{
			model.addAttribute("contentSentType", null);
		}
		return model;
	}
	
	/**
	 * 获取用户添加的链接
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ModelMap getHyperlink(HttpServletRequest request, ModelMap model,CmsUser user) {
		String linkUrl=user.getHyperlink();
		List list=null;
		if(StringUtils.isNotEmpty(linkUrl)){
			String[] strs=linkUrl.split(" ");
			list=new ArrayList();
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
				list.add(map);
			}
			model.addAttribute("linkUrls", linkUrl.replaceAll(" ", "\r\n"));
		}else{
			model.addAttribute("linkUrls", "");
		}
		model.addAttribute("urlList", list);
		return model;
	}
	
	public ModelMap getFouces(HttpServletRequest request, ModelMap model,
			CmsUser userT, CmsUser user) {
		if (user != null) {
			List<Focus> list= focusMng.find(user.getId(), userT.getId());
			if(null != list && list.size()>0){
				model.addAttribute("fouceCheck",1);//已关注
			}else{
				model.addAttribute("fouceCheck",0);//未关注
			}
		}else{
			model.addAttribute("fouceCheck",null);
		}
		return model;
	}
	
	/**
	 *判断该文章是否置顶
	 **/
	public ModelMap getStickId(ModelMap model,Integer contentId, CmsUser user) {
		if(user!=null){
			Integer isStick = cmsUserMng.getStickId(contentId, user.getId());
			model.addAttribute("isStick", isStick);
		}else{
			model.addAttribute("isStick", null);
		}
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
