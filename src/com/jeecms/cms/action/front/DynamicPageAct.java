package com.jeecms.cms.action.front;

import static com.jeecms.common.web.Constants.INDEX;
import static com.jeecms.common.web.Constants.INDEX_HTML;
import static com.jeecms.common.web.Constants.INDEX_HTML_MOBILE;
import static  com.jeecms.cms.Constants.TPLDIR_INDEX;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeecms.cms.action.blog.BlogAct;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.manager.assist.CmsKeywordMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.common.page.Paginable;
import com.jeecms.common.page.SimplePage;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsGroup;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.manager.CmsConfigMng;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
import com.jeecms.core.web.util.URLHelper;
import com.jeecms.core.web.util.URLHelper.PageInfo;

@Controller
public class DynamicPageAct {
	private static final Logger log = LoggerFactory
			.getLogger(DynamicPageAct.class);
	/**
	 * 首页模板名称
	 */
	public static final String TPL_INDEX = "tpl.index";
	public static final String GROUP_FORBIDDEN = "login.groupAccessForbidden";
	public static final String CONTENT_STATUS_FORBIDDEN ="content.notChecked";
	

	/**
	 * TOMCAT的默认路径
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
	//	return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_INDEX, TPL_INDEX);
		//带有其他路径则是非法请求(非内网)
		String uri=URLHelper.getURI(request);
		if(StringUtils.isNotBlank(uri)&&(!(uri.equals("/")||uri.equals("/index.jhtml")))){
			CmsConfig config=configMng.get();
			if(!config.getInsideSite()){
				return FrontUtils.pageNotFound(request, response, model);
			}
		}
		//使用静态首页而且静态首页存在
		if(existIndexPage(site)){
			return goToIndexPage(request, response, site);
		}else{
			String tpl = site.getTplIndex();
			if (!StringUtils.isBlank(tpl)) {
				return tpl;
			} else {
				return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_INDEX, TPL_INDEX);
			}
		}
	}
	

	/**
	 * WEBLOGIC的默认路径
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index.jhtml", method = RequestMethod.GET)
	public String indexForWeblogic(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		return index(request, response,model);
	}

	/**
	 * 动态页入口
	 */
	@RequestMapping(value = "/**/*.*", method = RequestMethod.GET)
	public String dynamic(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		// 尽量不要携带太多参数，多使用标签获取数据。
		// 目前已知的需要携带翻页信息。
		// 获得页号和翻页信息吧。
		int pageNo = URLHelper.getPageNo(request);
		String[] params = URLHelper.getParams(request);
		PageInfo info = URLHelper.getPageInfo(request);
		String[] paths = URLHelper.getPaths(request);
		CmsConfig config=configMng.get();
		String d = request.getParameter("d");//自己博客中心，博客内容跳转.  1
		String f = request.getParameter("f");//好友  0
		String wx = request.getParameter("wx");//微信分享
		String sx = request.getParameter("sx");//局部刷新(好友)
		String o = request.getParameter("o");//进入博客文章内容显示
		String columnId = request.getParameter("columnIdZ");//好友  0
		
		if(paths[0].equals("t")){
			//继续教育、社区文章展示
			return blogAct.blogContentEdu(paths, params, info, pageNo,request, response, model,f,columnId);
		}
		
		if(null != d){
			if(null != o){
				return blogAct.blogContentShowOwn(paths, params, info, pageNo,request, response, model,columnId);
			}else{
				return blogAct.blogContentShow(paths, params, info, pageNo,request, response, model,columnId);
			}
		}
		if(null != f){
			if(null != sx){
				//好友页面--局部刷新
				return blogAct.blogContentLocalRefreshFriend(paths, params, info, pageNo,request, response, model,f,columnId);
			}else{
				return blogAct.blogContentShowFriend(paths, params, info, pageNo,request, response, model,f,columnId);
			}
		}
		if(null != wx){
			//判断是PC访问还是移动端访问
			String equipment=(String) request.getAttribute("ua");
			//微信分享展示页面
			if(StringUtils.isNotBlank(equipment)&&equipment.equals("mobile")){
				return blogAct.blogContentShare(paths, params, info, pageNo,request, response, model,f,columnId);
			}else{
				return blogAct.blogContentSharePc(paths, params, info, pageNo,request, response, model,f,columnId);
			}
		}
		if(null != o){
			return blogAct.blogContentShowOwn(paths, params, info, pageNo,request, response, model,columnId);
		}
		if(config.getInsideSite()){
			return network(paths, params, info, pageNo, request, response, model);
		}else{
			return extranet(paths, params, info, pageNo, request, response, model);
		}
	}
	
	private String network(String[] paths,String[] params,PageInfo info,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model){
		int len = paths.length;
		if (len == 2) {
			//首页
			if(paths[1].equals(INDEX)){
				return index(request,response, model);
			}else{
				// 单页
				return channel(paths[1],true, pageNo, params, info, request, response,
						model);
			}
		} else if (len == 3) {
			if (paths[2].equals(INDEX)) {
				// 栏目页
				return channel(paths[1],false, pageNo, params, info, request,
						response, model);
			} else {
				// 内容页
				try {
					Integer id = Integer.parseInt(paths[2]);
					return content(id, pageNo, params, info, request, response,
							model);
				} catch (NumberFormatException e) {
					log.debug("Content id must String: {}", paths[1]);
					return FrontUtils.pageNotFound(request, response, model);
				}
			}
		} else {
			log.debug("Illegal path length: {}, paths: {}", len, paths);
			return FrontUtils.pageNotFound(request, response, model);
		}
	}
	
	private String extranet(String[] paths,String[] params,PageInfo info,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model){
		int len = paths.length;
		if (len == 1) {
			// 单页
			return channel(paths[0],true, pageNo, params, info, request, response,
					model);
		} else if (len == 2) {
			if (paths[1].equals(INDEX)) {
				// 栏目页
				return channel(paths[0],false, pageNo, params, info, request,
						response, model);
			} else {
				// 内容页
				try {
					Integer id = Integer.parseInt(paths[1]);
					return content(id, pageNo, params, info, request, response,
							model);
				} catch (NumberFormatException e) {
					log.debug("Content id must String: {}", paths[1]);
					return FrontUtils.pageNotFound(request, response, model);
				}
			}
		} else {
			log.debug("Illegal path length: {}, paths: {}", len, paths);
			return FrontUtils.pageNotFound(request, response, model);
		}
	}

	public String channel(String path,boolean checkAlone, int pageNo, String[] params,
			PageInfo info, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		Channel channel = channelMng.findByPathForTag(path, site.getId());
		if (channel == null) {
			log.debug("Channel path not found: {}", path);
			return FrontUtils.pageNotFound(request, response, model);
		}
		//检查是否单页
		if(checkAlone){
			if(channel.getHasContent()){
				return FrontUtils.pageNotFound(request, response, model);
			}
		}
		model.addAttribute("channel", channel);
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		String equipment=(String) request.getAttribute("ua");
		/*if(StringUtils.isNotBlank(equipment)&&equipment.equals("mobile")){
			return channel.getMobileTplChannelOrDef();
		}*/
		return channel.getTplChannelOrDef();
	}

	
	
	public String content(Integer id, int pageNo, String[] params,
			PageInfo info, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		Content content = contentMng.findById(id);
		if (content == null) {
			log.debug("Content id not found: {}", id);
			return FrontUtils.pageNotFound(request, response, model);
		}
		Integer pageCount=content.getPageCount();
		if(pageNo>pageCount||pageNo<0){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//非终审文章
		CmsConfig config=CmsUtils.getSite(request).getConfig();
		config.getConfigAttr().getPreview();
		CmsUtils.getUser(request);
		CmsSite site = content.getSite();
		Set<CmsGroup> groups = content.getViewGroupsExt();
		groups.size();
		/*// 需要浏览权限
		if (len != 0) {
			// 没有登录
			if (user == null) {
				session.setAttribute(request, response, "loginSource", "needPerm");
				return FrontUtils.showLogin(request, model, site);
			}
			// 已经登录但没有权限
			Integer gid = user.getGroup().getId();
			boolean right = false;
			for (CmsGroup group : groups) {
				if (group.getId().equals(gid)) {
					right = true;
					break;
				}
			}
			//无权限且不支持预览
			if (!right&&!preview) {
				String gname = user.getGroup().getName();
				return FrontUtils.showMessage(request, model, GROUP_FORBIDDEN,
						gname);
			}
			//无权限支持预览
			if(!right&&preview){
				model.addAttribute("preview", preview);
				model.addAttribute("groups", groups);
			}
		}*/
		//收费模式
		/*if(content.getCharge()){
			if(user==null){
				session.setAttribute(request, response, "loginSource", "charge");
				return FrontUtils.showLogin(request, model, site);
			}else{
				//非作者且未购买
				if(!content.getUser().equals(user)){
					//用户已登录判断是否已经购买
					boolean hasBuy=contentBuyMng.hasBuyContent(user.getId(), content.getId());
					if(!hasBuy){
						try {
							String rediretUrl="/content/buy.jspx?contentId="+content.getId();
							if(StringUtils.isNotBlank(site.getContextPath())){
								rediretUrl=site.getContextPath()+rediretUrl;
							}
							response.sendRedirect(rediretUrl);
						} catch (IOException e) {
							//e.printStackTrace();
						}
					}
				}
			}
		}*/
		String txt = content.getTxtByNo(pageNo);
		// 内容加上关键字
		txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
		Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
		model.addAttribute("pagination", pagination);
		FrontUtils.frontPageData(request, model);
		model.addAttribute("content", content);
		model.addAttribute("channel", content.getChannel());
		model.addAttribute("title", content.getTitleByNo(pageNo));
		model.addAttribute("txt", txt);
		model.addAttribute("pic", content.getPictureByNo(pageNo));
		
		String topicId = request.getParameter("topicId");
		model.addAttribute("topicId", topicId);
		String typeIds = request.getParameter("typeIds");
		model.addAttribute("typeIds", typeIds);
		FrontUtils.frontData(request, model, site);
		String equipment=(String) request.getAttribute("ua");
		/*if(StringUtils.isNotBlank(equipment)&&equipment.equals("mobile")){
			return content.getMobileTplContentOrDef(content.getModel());
		}*/
		return content.getTplContentOrDef(content.getModel());
	}
	

	private boolean existIndexPage(CmsSite site){
		boolean exist=false;
		if(site.getStaticIndex()){
			File indexPage;
			if(site.getIndexToRoot()){
				indexPage=new File(realPathResolver.get(INDEX_HTML));
			}else{
				indexPage=new File(realPathResolver.get(site.getStaticDir()+INDEX_HTML));
			}
			if(indexPage.exists()){
				exist=true; 
			}
		}
		return exist;
	}
	
	private String goToIndexPage(HttpServletRequest request,HttpServletResponse response,CmsSite site){
		String equipment=(String) request.getAttribute("ua");
		try {
			String ctx="";
			if(StringUtils.isNotBlank(site.getContextPath())){
				ctx=site.getContextPath();
			}
			if(site.getIndexToRoot()){
				
				/*if(StringUtils.isNotBlank(equipment)&&equipment.equals("mobile")){
					response.sendRedirect(ctx+INDEX_HTML_MOBILE);
				}else{*/
					response.sendRedirect(ctx+INDEX_HTML);
				/*}*/
			}else{
				response.sendRedirect(ctx+site.getStaticDir()+INDEX_HTML);
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_INDEX, TPL_INDEX); 
	}
	
	@Autowired
	private BlogAct blogAct;
	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private CmsKeywordMng cmsKeywordMng;
	@Autowired
	private CmsConfigMng configMng;
	@Autowired
	private RealPathResolver realPathResolver;
}
