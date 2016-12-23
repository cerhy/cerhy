package com.jeecms.cms.action.member;


import static com.jeecms.cms.Constants.TPLDIR_BLOG;
import static com.jeecms.cms.Constants.TPLDIR_MEMBER;
import static com.jeecms.common.page.SimplePage.cpn;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.jeecms.cms.dao.main.impl.BlogDao;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.CmsModel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.entity.main.ContentExt;
import com.jeecms.cms.entity.main.ContentTxt;
import com.jeecms.cms.entity.main.ContentType;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.CmsModelMng;
import com.jeecms.cms.manager.main.ContentDocMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.ContentTypeMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.upload.FileRepository;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.MemberConfig;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * @author Tom
 */
public class AbstractContentMemberAct {
	protected String list(String q, Integer modelId,Integer queryChannelId,
			String nextUrl,Integer pageNo,
			HttpServletRequest request, ModelMap model) {
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
		Pagination p = contentMng.getPageForMember(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, nextUrl);
	}
	
	public String add(boolean hasPermission,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if(hasPermission){
			FrontUtils.frontData(request, model, site);
			MemberConfig mcfg = site.getConfig().getMemberConfig();
			// 没有开启会员功能
			if (!mcfg.isMemberOn()) {
				return FrontUtils.showMessage(request, model, "member.memberClose");
			}
			if (user == null) {
				return FrontUtils.showLogin(request, model, site);
			}
			// 获得本站栏目列表
			Set<Channel> rights = user.getGroup().getContriChannels();
			List<Channel> topList=channelMng.getTopList(site.getId(), true);
			List<Channel> channelList = Channel.getListForSelect(topList, rights,true);
			model.addAttribute("site", site);
			model.addAttribute("channelList", channelList);
			model.addAttribute("sessionId",request.getSession().getId());
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_MEMBER, nextUrl);
		}else{
			WebErrors errors = WebErrors.create(request);
			errors.addErrorCode("error.uploadMoreNumber", user.getGroup().getAllowFileTotal());
			return FrontUtils.showError(request, response, model, errors);
		}
	}
	
	public String save(String title, String author, String description,
			String txt, String tagStr, Integer channelId, Integer modelId,ContentDoc doc,
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
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
		WebErrors errors = validateSave(title, author, description, txt,doc,
				tagStr, channelId, site, user, captcha, request, response);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}

		Content c = new Content();
		c.setSite(site);
		CmsModel defaultModel=cmsModelMng.getDefModel();
		if(modelId!=null){
			CmsModel m=cmsModelMng.findById(modelId);
			if(m!=null){
				c.setModel(m);
			}else{
				c.setModel(defaultModel);
			}
		}else{
			c.setModel(defaultModel);
		}
		ContentExt ext = new ContentExt();
		ext.setTitle(title);
		ext.setAuthor(author);
		ext.setDescription(description);
		ext.setMediaPath(mediaPath);
		ext.setMediaType(mediaType);
		ContentTxt t = new ContentTxt();
		t.setTxt(txt);
		ContentType type = contentTypeMng.getDef();
		if (type == null) {
			throw new RuntimeException("Default ContentType not found.");
		}
		Integer typeId = type.getId();
		String[] tagArr = StrUtils.splitAndTrim(tagStr, ",", null);
		if(c.getRecommendLevel()==null){
			c.setRecommendLevel((byte) 0);
		}
		c = contentMng.save(c, ext, t,null, null, null, null, tagArr,
				attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs,channelId, typeId, null,true,
				charge,chargeAmount, user, true);
		if(doc!=null){
			contentDocMng.save(doc, c);
		}
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
	public String edit(Integer id, String nextUrl,HttpServletRequest request,
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
		WebErrors errors = validateEdit(id, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		Content content = contentMng.findById(id);
		// 获得本站栏目列表
		Set<Channel> rights = user.getGroup().getContriChannels();
		List<Channel> topList = channelMng.getTopList(site.getId(), true);
		List<Channel> channelList = Channel.getListForSelect(topList, rights,
				true);
		model.addAttribute("content", content);
		model.addAttribute("site", site);
		model.addAttribute("channelList", channelList);
		model.addAttribute("sessionId",request.getSession().getId());
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, nextUrl);
	}
	
	public String delete(Integer[] ids, HttpServletRequest request,
			String nextUrl, HttpServletResponse response, ModelMap model) {
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
		WebErrors errors = validateDelete(ids, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		contentMng.deleteByIds(ids);
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
	public String update(Integer id, String title, String author,
			String description, String txt, String tagStr, Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			ContentDoc doc,Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
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
		WebErrors errors = validateUpdate(id, channelId, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		Content c = new Content();
		c.setId(id);
		c.setSite(site);
		ContentExt ext = new ContentExt();
		ext.setId(id);
		ext.setTitle(title);
		ext.setAuthor(author);
		ext.setDescription(description);
		ext.setMediaPath(mediaPath);
		ext.setMediaType(mediaType);
		ContentTxt t = new ContentTxt();
		t.setId(id);
		t.setTxt(txt);
		String[] tagArr = StrUtils.splitAndTrim(tagStr, ",", null);
		contentMng.update(c, ext, t,null, tagArr, null, null, null, 
				attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs, null, channelId, null, null, 
				charge,chargeAmount,user, true);
		if(doc!=null){
			contentDocMng.update(doc, c);
		}
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
	private WebErrors validateSave(String title, String author,
			String description, String txt,ContentDoc doc, String tagStr, Integer channelId,
			CmsSite site, CmsUser user, String captcha,
			HttpServletRequest request, HttpServletResponse response) {
		WebErrors errors = WebErrors.create(request);
		try {
			if (!imageCaptchaService.validateResponseForID(session
					.getSessionId(request, response), captcha)) {
				errors.addErrorCode("error.invalidCaptcha");
				return errors;
			}
		} catch (CaptchaServiceException e) {
			errors.addErrorCode("error.exceptionCaptcha");
			return errors;
		}
		if (errors.ifBlank(title, "title", 150)) {
			return errors;
		}
		if (errors.ifMaxLength(author, "author", 100)) {
			return errors;
		}
		if (errors.ifMaxLength(description, "description", 255)) {
			return errors;
		}
		if(doc==null){
			// 内容不能大于1M
			if (errors.ifBlank(txt, "txt", 1048575)) {
				return errors;
			}
		}else{
			if(StringUtils.isBlank(doc.getDocPath())){
				errors.addErrorCode("error.hasNotUploadDoc");
				return errors;
			}
		}
		
		if (errors.ifMaxLength(tagStr, "tagStr", 255)) {
			return errors;
		}
		if (errors.ifNull(channelId, "channelId")) {
			return errors;
		}
		if (vldChannel(errors, site, user, channelId)) {
			return errors;
		}
		return errors;
	}

	

	private WebErrors validateEdit(Integer id, CmsSite site, CmsUser user,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldOpt(errors, site, user, new Integer[] { id })) {
			return errors;
		}
		return errors;
	}
	
	private WebErrors validateUpdate(Integer id, Integer channelId,
			CmsSite site, CmsUser user, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldOpt(errors, site, user, new Integer[] { id })) {
			return errors;
		}
		if (vldChannel(errors, site, user, channelId)) {
			return errors;
		}
		return errors;
	}
	
	private WebErrors validateDelete(Integer[] ids, CmsSite site, CmsUser user,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldOpt(errors, site, user, ids)) {
			return errors;
		}
		return errors;
	}

	private boolean vldOpt(WebErrors errors, CmsSite site, CmsUser user,
			Integer[] ids) {
		for (Integer id : ids) {
			if (errors.ifNull(id, "id")) {
				return true;
			}
			Content c = contentMng.findById(id);
			// 数据不存在
			if (errors.ifNotExist(c, Content.class, id)) {
				return true;
			}
			// 非本用户数据
			if (!c.getUser().getId().equals(user.getId())) {
				errors.noPermission(Content.class, id);
				return true;
			}
			// 非本站点数据
			if (!c.getSite().getId().equals(site.getId())) {
				errors.notInSite(Content.class, id);
				return true;
			}
			// 文章级别大于0，不允许修改
			if (c.getCheckStep() > 0) {
				errors.addErrorCode("member.contentChecked");
				return true;
			}
		}
		return false;
	}
	
	private boolean vldChannel(WebErrors errors, CmsSite site, CmsUser user,
			Integer channelId) {
		Channel channel = channelMng.findById(channelId);
		if (errors.ifNotExist(channel, Channel.class, channelId)) {
			return true;
		}
		if (!channel.getSite().getId().equals(site.getId())) {
			errors.notInSite(Channel.class, channelId);
			return true;
		}
		if (!channel.getContriGroups().contains(user.getGroup())) {
			errors.noPermission(Channel.class, channelId);
			return true;
		}
		return false;
	}
	
	

	@Autowired
	protected ContentMng contentMng;
	@Autowired
	protected ContentDocMng contentDocMng;
	@Autowired
	protected ContentTypeMng contentTypeMng;
	@Autowired
	protected ChannelMng channelMng;
	@Autowired
	protected CmsModelMng cmsModelMng;
	@Autowired
	protected SessionProvider session;
	@Autowired
	protected FileRepository fileRepository;
	@Autowired
	protected ImageCaptchaService imageCaptchaService;
	
	public String tzsetting(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String name =null ;
		  try {
			request.setCharacterEncoding("UTF-8");
		    name = new String(request.getParameter("id").getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int user_id = user.getId();
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
		model.addAttribute("columnsList", columnsList);
		String id=request.getParameter("id");
		String orderId = request.getParameter("order");
		Columns column = new Columns(Integer.parseInt(id),user.getId(),name,Integer.parseInt(orderId));
		model.addAttribute("column", column);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.columnsUpdate");
	}
	
	protected String center(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		int user_id = user.getId();
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
		model.addAttribute("columnsList", columnsList);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20,null);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}
	
	protected String blog_list(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String name =null ;
		try {
			request.setCharacterEncoding("UTF-8");
			if(null != request.getParameter("id")){
			    name = new String(request.getParameter("id").getBytes("ISO-8859-1"), "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int user_id = user.getId();
	    String path = request.getSession().getServletContext().getRealPath("/");
		List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
		model.addAttribute("columnsList", columnsList);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20,name);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}
	
	public String blog_add(boolean hasPermission,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		int user_id = user.getId();
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
		model.addAttribute("columnsList", columnsList);
		if(hasPermission){
			FrontUtils.frontData(request, model, site);
			// 获得本站栏目列表
			Set<Channel> rights = user.getGroup().getContriChannels();
			List<Channel> topList=channelMng.getTopList(site.getId(), true);
			List<Channel> channelList = Channel.getListForSelect(topList, rights,true);
			model.addAttribute("site", site);
			model.addAttribute("channelList", channelList);
			model.addAttribute("sessionId",request.getSession().getId());
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, nextUrl);
		}else{
			WebErrors errors = WebErrors.create(request);
			errors.addErrorCode("error.uploadMoreNumber", user.getGroup().getAllowFileTotal());
			return FrontUtils.showError(request, response, model, errors);
		}
	}
	
		protected String blog_columns_list(String q, Integer modelId,Integer queryChannelId,
				String nextUrl,Integer pageNo,
				HttpServletRequest request, ModelMap model) {
			CmsSite site = CmsUtils.getSite(request);
			CmsUser user = CmsUtils.getUser(request);
			int user_id = user.getId();
			String path = request.getSession().getServletContext().getRealPath("/");
			List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
			model.addAttribute("columnsList", columnsList);
			FrontUtils.frontData(request, model, site);
			Pagination p = contentMng.getPageForMember(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20);
			model.addAttribute("pagination", p);
			if (!StringUtils.isBlank(q)) {
				model.addAttribute("q", q);
			}
			if (modelId != null) {
				model.addAttribute("modelId", modelId);
			}
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, nextUrl);
		}
		
		public void columns_add(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
			String name = request.getParameter("columnInput");
			String order = request.getParameter("columnOrder");
			CmsUser user = CmsUtils.getUser(request);
			int user_id = user.getId();
			String path = request.getSession().getServletContext().getRealPath("/");
			if(null != name && null != user){
				(new BlogDao()).addColumn(user_id,name,order, path);
			}
			try {
				request.getRequestDispatcher("/blog/columns_list.jspx").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void columns_detele(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
			String id = request.getParameter("id");
			String path = request.getSession().getServletContext().getRealPath("/");
			if(null != id){
				(new BlogDao()).deleteColumn(Integer.parseInt(id), path);
			}
			try {
				request.getRequestDispatcher("/blog/columns_list.jspx").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		public void columns_update(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
			String id = request.getParameter("id");
			String name = request.getParameter("updateName");
			String orderId = request.getParameter("updateOrderId");
			String path = request.getSession().getServletContext().getRealPath("/");
			if (null != id) {
				if (null != name && ""!=name) {
					if (null != orderId && ""!=orderId) {
						(new BlogDao()).updateColumn(Integer.parseInt(id), name, Integer.parseInt(orderId), path);
					} else {
						(new BlogDao()).updateColumn(Integer.parseInt(id), name, path);
					}
				} else {
					if (null != orderId) {
						(new BlogDao()).updateColumn(Integer.parseInt(id), Integer.parseInt(orderId), path);
					}
				}
			}
			try {
				request.getRequestDispatcher("/blog/columns_list.jspx").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public String update_tz(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
			CmsSite site = CmsUtils.getSite(request);
			CmsUser user = CmsUtils.getUser(request);
			String name =null ;
			  try {
				request.setCharacterEncoding("UTF-8");
			    name = new String(request.getParameter("id").getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			int user_id = user.getId();
			String path = request.getSession().getServletContext().getRealPath("/");
			List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
			model.addAttribute("columnsList", columnsList);
			String id=request.getParameter("id");
			String orderId = request.getParameter("order");
			Columns column = new Columns(Integer.parseInt(id),user.getId(),name,Integer.parseInt(orderId));
			model.addAttribute("column", column);
			FrontUtils.frontData(request, model, site);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.columnsUpdate");
		}
		
	public String blog_save(String title, String author, String description,
			String txt, String tagStr, String columnName, Integer modelId,ContentDoc doc,
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
				CmsSite site = CmsUtils.getSite(request);
				CmsUser user = CmsUtils.getUser(request);
				FrontUtils.frontData(request, model, site);
			
			if (user == null) {
				return FrontUtils.showLogin(request, model, site);
			}

		Content c = new Content();
		c.setSite(site);
		CmsModel defaultModel=cmsModelMng.getDefModel();
		if(modelId!=null){
			CmsModel m=cmsModelMng.findById(modelId);
			if(m!=null){
				c.setModel(m);
			}else{
				c.setModel(defaultModel);
			}
		}else{
			c.setModel(defaultModel);
		}
		ContentExt ext = new ContentExt();
		ext.setTitle(title);
		ext.setAuthor(author);
		ext.setDescription(description);
		ext.setMediaPath(mediaPath);
		ext.setMediaType(mediaType);
		ContentTxt t = new ContentTxt();
		t.setTxt(txt);
		ContentType type = contentTypeMng.getDef();
		if (type == null) {
			throw new RuntimeException("Default ContentType not found.");
		}
		Integer typeId = type.getId();
		String[] tagArr = StrUtils.splitAndTrim(tagStr, ",", null);
		if(c.getRecommendLevel()==null){
			c.setRecommendLevel((byte) 0);
		}
		c = contentMng.blog_save(c, ext, t,null, null, null, null, tagArr,
				attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs,columnName, typeId, null,true,
				charge,chargeAmount, user, true);
		if(doc!=null){
			contentDocMng.save(doc, c);
		}
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
	public String blog_edit(Integer id, String nextUrl,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		int user_id = user.getId();
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
		model.addAttribute("columnsList", columnsList);
	/*	WebErrors errors = validateEdit(id, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}*/
		Content content = contentMng.findById(id);
		// 获得本站栏目列表
		Set<Channel> rights = user.getGroup().getContriChannels();
		List<Channel> topList = channelMng.getTopList(site.getId(), true);
		List<Channel> channelList = Channel.getListForSelect(topList, rights,
				true);
		model.addAttribute("content", content);
		model.addAttribute("site", site);
		model.addAttribute("channelList", channelList);
		model.addAttribute("sessionId",request.getSession().getId());
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_BLOG, nextUrl);
	}
	
	public String blog_delete(Integer[] ids, HttpServletRequest request,
			String nextUrl, HttpServletResponse response, ModelMap model) {
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
		WebErrors errors = validateDelete(ids, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		contentMng.deleteByIds(ids);
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
	public String blog_update(Integer id, String title, String author,
			String description, String txt, String tagStr, Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			ContentDoc doc,Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
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
		WebErrors errors = validateUpdate(id, channelId, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		Content c = new Content();
		c.setId(id);
		c.setSite(site);
		ContentExt ext = new ContentExt();
		ext.setId(id);
		ext.setTitle(title);
		ext.setAuthor(author);
		ext.setDescription(description);
		ext.setMediaPath(mediaPath);
		ext.setMediaType(mediaType);
		ContentTxt t = new ContentTxt();
		t.setId(id);
		t.setTxt(txt);
		String[] tagArr = StrUtils.splitAndTrim(tagStr, ",", null);
		contentMng.update(c, ext, t,null, tagArr, null, null, null, 
				attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs, null, channelId, null, null, 
				charge,chargeAmount,user, true);
		if(doc!=null){
			contentDocMng.update(doc, c);
		}
		return FrontUtils.showSuccess(request, model, nextUrl);
	}

	public String link_save(String linkUrl,String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		channelMng.updateLinkUrl(linkUrl,user);
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
}
