package com.jeecms.cms.action.member;
import static com.jeecms.cms.Constants.TPLDIR_BLOG;
import static com.jeecms.cms.Constants.TPLDIR_MEMBER;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jeecms.cms.action.blog.BlogAct;
import com.jeecms.cms.action.blog.BlogCommon;
import com.jeecms.cms.action.blog.CreateSerialNo;
import com.jeecms.cms.dao.main.impl.BlogDao;
import com.jeecms.cms.entity.assist.CmsComment;
import com.jeecms.cms.entity.assist.CmsJoinGroup;
import com.jeecms.cms.entity.assist.CmsPostilInfo;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.entity.main.ContentStick;
import com.jeecms.cms.entity.main.Focus;
import com.jeecms.cms.manager.assist.CmsFileMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.impl.ColumnsMng;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.Ftp;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.manager.DbFileMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

import freemarker.template.TemplateException;

/**
 * 会员投稿Action
 * 
 */
@Controller
public class ContributeAct extends AbstractContentMemberAct {
	private static final Logger log = LoggerFactory.getLogger(ContributeAct.class);
	
	public static final String CONTRIBUTE_LIST = "tpl.contributeList";
	public static final String CONTRIBUTE_ADD = "tpl.contributeAdd";
	public static final String CONTRIBUTE_EDIT = "tpl.contributeEdit";
	public static final String CONTRIBUTE_UPLOADMIDIA = "tpl.uploadMedia";
	public static final String CONTRIBUTE_UPLOADATTACHMENT = "tpl.uploadAttachment";

	/**
	 * 会员投稿列表
	 * 
	 * @param title
	 *            文章标题
	 * @param channelId
	 *            栏目ID
	 * @param pageNo
	 *            页码
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_list.jspx")
	public String list(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return super.list(queryTitle, modelId, queryChannelId, CONTRIBUTE_LIST,
				pageNo, request, model);
	}

	/**
	 * 会员投稿添加
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_add.jspx")
	public String add(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return super.add(true, CONTRIBUTE_ADD, request, response, model);
	}

	/**
	 * 会员投稿保存
	 * 
	 * @param id
	 *            文章ID
	 * @param title
	 *            标题
	 * @param author
	 *            作者
	 * @param description
	 *            描述
	 * @param txt
	 *            内容
	 * @param tagStr
	 *            TAG字符串
	 * @param channelId
	 *            栏目ID
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws TemplateException 
	 */
	@RequestMapping(value = "/member/contribute_save.jspx")
	public String save(String title, String author, String description,
			String txt, String tagStr, Integer channelId,Integer columnId,Integer modelId, 
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws TemplateException {
		   String blog = request.getParameter("blog");
		   String channelIds = request.getParameter("channelIds");
		   String password = request.getParameter("password");
		   //String showStyle= request.getParameter("showStyle");
		   int sta=0;
		   if(channelIds!=null&&channelIds!=""){
			   String[] str=channelIds.split("&");
			   if(str[1].equals("chan")){
				   channelId=Integer.valueOf(str[0]);
				   //高中、初中、小学美术
				   if(channelId.toString().equals("316")||channelId.toString().equals("323")){
					   channelId=305;
				   }
				   //高中、初中、小学音乐
				   if(channelId.toString().equals("315")||channelId.toString().equals("322")){
					   channelId=304;
				   }
				   //高中、初中、小学体育
				   if(channelId.toString().equals("314")||channelId.toString().equals("321")){
					   channelId=303;
				   }
				   //高中、初中、小学综合实践
				   if(channelId.toString().equals("417")||channelId.toString().equals("418")){
					   channelId=416;
				   }
				   //高中、初中地理
				   if(channelId.toString().equals("311")){
					   channelId=300;
				   }
				   //高中、初中历史
				   if(channelId.toString().equals("310")){
					   channelId=299;
				   }
				   //初中、小学信息技术
				   if(channelId.toString().equals("319")){
					   channelId=312;
				   }
				   //初中、小学政治
				   if(channelId.toString().equals("317")){
					   channelId=309;
				   }
				   //高中、初中化学
				   if(channelId.toString().equals("307")){
					   channelId=296;
				   }
				   //高中、初中物理
				   if(channelId.toString().equals("306")){
					   channelId=295;
				   }
				   //高中、初中生物
				   if(channelId.toString().equals("308")){
					   channelId=297;
					}
				   //高中、初中语文
				   if(channelId.toString().equals("142")){
					   channelId=140;
				   }
					//高中、初中、小学心理健康
				   if(channelId.toString().equals("421")||channelId.toString().equals("422")){
					   channelId=420;
				   }

			   }else if(str[1].equals("colu")){
				   columnId=Integer.valueOf(str[0]);
				   sta=1;
			   }
		   }
		   if(null != blog ){
			return blogAct.blog_save(title, author, description, txt, tagStr, channelId,columnId,modelId,
						null, captcha,mediaPath,mediaType,attachmentPaths,attachmentNames, attachmentFilenames
						,picPaths,picDescs,charge,chargeAmount,password,
						nextUrl, request, response, model,sta);
					   }else{
			 return super.save(title, author, description, txt, tagStr, channelId,modelId,
						null, captcha,mediaPath,mediaType,attachmentPaths,attachmentNames, attachmentFilenames
						,picPaths,picDescs,charge,chargeAmount,
						nextUrl, request, response, model);
		   }
		
	}

	/**
	 * 会员投稿保存
	 * 
	 * @param id
	 *            文章ID
	 * @param title
	 *            标题
	 * @param author
	 *            作者
	 * @param description
	 *            描述
	 * @param txt
	 *            内容
	 * @param tagStr
	 *            TAG字符串
	 * @param channelId
	 *            栏目ID
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws TemplateException 
	 */
	@RequestMapping(value = "/member/contribute_ajaxsave.jspx")
	public @ResponseBody String contribute_ajaxsave(String title, String author, String description,
			String contentTxt, String tagStr, Integer channelId,Integer columnId,Integer modelId, 
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws TemplateException {
		   String channelIds = request.getParameter("channelIds");
		   String password = request.getParameter("password");
		   //String showStyle= request.getParameter("showStyle");
		   String txt=contentTxt;
		   int sta=0;
		   JSONObject json = new JSONObject();
		   if(channelIds!=null&&channelIds!=""){
			   String[] str=channelIds.split("&");
			   if(str[1].equals("chan")){
				   channelId=Integer.valueOf(str[0]);
				   //高中、初中、小学美术
				   if(channelId.toString().equals("316")||channelId.toString().equals("323")){
					   channelId=305;
				   }
				   //高中、初中、小学音乐
				   if(channelId.toString().equals("315")||channelId.toString().equals("322")){
					   channelId=304;
				   }
				   //高中、初中、小学体育
				   if(channelId.toString().equals("314")||channelId.toString().equals("321")){
					   channelId=303;
				   }
				   //高中、初中、小学综合实践
				   if(channelId.toString().equals("417")||channelId.toString().equals("418")){
					   channelId=416;
				   }
				   //高中、初中地理
				   if(channelId.toString().equals("311")){
					   channelId=300;
				   }
				   //高中、初中历史
				   if(channelId.toString().equals("310")){
					   channelId=299;
				   }
				   //初中、小学信息技术
				   if(channelId.toString().equals("319")){
					   channelId=312;
				   }
				   //初中、小学政治
				   if(channelId.toString().equals("317")){
					   channelId=309;
				   }
				   //高中、初中化学
				   if(channelId.toString().equals("307")){
					   channelId=296;
				   }
				   //高中、初中物理
				   if(channelId.toString().equals("306")){
					   channelId=295;
				   }
				   //高中、初中生物
				   if(channelId.toString().equals("308")){
					   channelId=297;
					}
				   //高中、初中语文
				   if(channelId.toString().equals("142")){
					   channelId=140;
				   }
					//高中、初中、小学心理健康
				   if(channelId.toString().equals("421")||channelId.toString().equals("422")){
					   channelId=420;
				   }

			   }else if(str[1].equals("colu")){
				   columnId=Integer.valueOf(str[0]);
				   sta=1;
			   }
		   }
		  
		   String result= blogAct.blog_ajaxsave(title, author, description, txt, tagStr, channelId,columnId,modelId,
					null, captcha,mediaPath,mediaType,attachmentPaths,attachmentNames, attachmentFilenames
					,picPaths,picDescs,charge,chargeAmount,password,
					nextUrl, request, response, model,sta);
		   try {
			json.put("code", result);
		} catch (JSONException e) {
			log.error(e.getMessage(),e);
		}
			return json.toString();
		
	}

	
	/**
	 * 会员投稿修改
	 * 
	 * @param id
	 *            文章ID
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_edit.jspx")
	public String edit(Integer id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return super.edit(id, CONTRIBUTE_EDIT, request, response, model);
	}

	/**
	 * 会有投稿更新
	 * 
	 * @param id
	 *            文章ID
	 * @param title
	 *            标题
	 * @param author
	 *            作者
	 * @param description
	 *            描述
	 * @param txt
	 *            内容
	 * @param tagStr
	 *            TAG字符串
	 * @param channelId
	 *            栏目ID
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_update.jspx")
	public void update(Integer id, String title, String author,
			String description, String txt, String tagStr, Integer columnId,Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String password) {
		String blog = request.getParameter("blog");
		String pw = request.getParameter("password");
		//String showStyle= request.getParameter("showStyle");
		String channelIds = request.getParameter("channelIds");
		   if(channelIds!=null&&channelIds!=""){
			   String[] str=channelIds.split("&");
			   if(str[1]==null){
				   channelId=Integer.valueOf(str[0]);
				 //高中、初中、小学美术
				   if(channelId.toString().equals("316")||channelId.toString().equals("323")){
					   channelId=305;
				   }
				   //高中、初中、小学音乐
				   if(channelId.toString().equals("315")||channelId.toString().equals("322")){
					   channelId=304;
				   }
				   //高中、初中、小学体育
				   if(channelId.toString().equals("314")||channelId.toString().equals("321")){
					   channelId=303;
				   }
				   //高中、初中、小学综合实践
				   if(channelId.toString().equals("417")||channelId.toString().equals("418")){
					   channelId=416;
				   }
				   //高中、初中地理
				   if(channelId.toString().equals("311")){
					   channelId=300;
				   }
				   //高中、初中历史
				   if(channelId.toString().equals("310")){
					   channelId=299;
				   }
				   //初中、小学信息技术
				   if(channelId.toString().equals("319")){
					   channelId=312;
				   }
				   //初中、小学政治
				   if(channelId.toString().equals("317")){
					   channelId=309;
				   }
				   //高中、初中化学
				   if(channelId.toString().equals("307")){
					   channelId=296;
				   }
				   //高中、初中物理
				   if(channelId.toString().equals("306")){
					   channelId=295;
				   }
				   //高中、初中生物
				   if(channelId.toString().equals("308")){
					   channelId=297;
					}
				   //高中、初中语文
					if(channelId.toString().equals("142")){
						channelId=140;
					}
					//高中、初中、小学心理健康
					if(channelId.toString().equals("421")||channelId.toString().equals("422")){
						channelId=420;
					}
			   }else if(str[1].equals("colu")){
				   columnId=Integer.valueOf(str[0]);
			   }else if(str[1].equals("chan")){
				   channelId=Integer.valueOf(str[0]);
				   //高中、初中、小学美术
				   if(channelId.toString().equals("316")||channelId.toString().equals("323")){
					   channelId=305;
				   }
				   //高中、初中、小学音乐
				   if(channelId.toString().equals("315")||channelId.toString().equals("322")){
					   channelId=304;
				   }
				   //高中、初中、小学体育
				   if(channelId.toString().equals("314")||channelId.toString().equals("321")){
					   channelId=303;
				   }
				   //高中、初中、小学综合实践
				   if(channelId.toString().equals("417")||channelId.toString().equals("418")){
					   channelId=416;
				   }
				   //高中、初中地理
				   if(channelId.toString().equals("311")){
					   channelId=300;
				   }
				   //高中、初中历史
				   if(channelId.toString().equals("310")){
					   channelId=299;
				   }
				   //初中、小学信息技术
				   if(channelId.toString().equals("319")){
					   channelId=312;
				   }
				   //初中、小学政治
				   if(channelId.toString().equals("317")){
					   channelId=309;
				   }
				   //高中、初中化学
				   if(channelId.toString().equals("307")){
					   channelId=296;
				   }
				   //高中、初中物理
				   if(channelId.toString().equals("306")){
					   channelId=295;
				   }
				   //高中、初中生物
				   if(channelId.toString().equals("308")){
					   channelId=297;
					}
				   //高中、初中语文
					if(channelId.toString().equals("142")){
						channelId=140;
					}
					//高中、初中、小学心理健康
					if(channelId.toString().equals("421")||channelId.toString().equals("422")){
						channelId=420;
					}
			   }
		   }
		   if(null != blog ){
			   blogAct.blog_update(id, title, author, description, txt, tagStr,
					   columnId,channelId, mediaPath,mediaType,attachmentPaths,
						attachmentNames, attachmentFilenames
						,picPaths,picDescs,null,charge, chargeAmount,
						nextUrl, request, response, model,pw);
		   }else{
			   super.update(id, title, author, description, txt, tagStr,
						channelId, mediaPath,mediaType,attachmentPaths,
						attachmentNames, attachmentFilenames
						,picPaths,picDescs,null,charge, chargeAmount,
						nextUrl, request, response, model);
		   }
		
	}
	/**
	 * 会有投稿更新
	 * 
	 * @param id
	 *            文章ID
	 * @param title
	 *            标题
	 * @param author
	 *            作者
	 * @param description
	 *            描述
	 * @param txt
	 *            内容
	 * @param tagStr
	 *            TAG字符串
	 * @param channelId
	 *            栏目ID
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_ajax_update.jspx")
	public @ResponseBody String ajaxUpdate(Integer id, String title, String author,
			String description,String contentTxt, String tagStr, Integer columnId,Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String password) {
		JSONObject json = new JSONObject();
		String txt=contentTxt;
		//String showStyle= request.getParameter("showStyle");
		String pw = request.getParameter("password");
		String channelIds = request.getParameter("channelIds");
		   if(channelIds!=null&&channelIds!=""){
			   String[] str=channelIds.split("&");
			   if(str[1]==null){
				   channelId=Integer.valueOf(str[0]);
				 //高中、初中、小学美术
				   if(channelId.toString().equals("316")||channelId.toString().equals("323")){
					   channelId=305;
				   }
				   //高中、初中、小学音乐
				   if(channelId.toString().equals("315")||channelId.toString().equals("322")){
					   channelId=304;
				   }
				   //高中、初中、小学体育
				   if(channelId.toString().equals("314")||channelId.toString().equals("321")){
					   channelId=303;
				   }
				   //高中、初中、小学综合实践
				   if(channelId.toString().equals("417")||channelId.toString().equals("418")){
					   channelId=416;
				   }
				   //高中、初中地理
				   if(channelId.toString().equals("311")){
					   channelId=300;
				   }
				   //高中、初中历史
				   if(channelId.toString().equals("310")){
					   channelId=299;
				   }
				   //初中、小学信息技术
				   if(channelId.toString().equals("319")){
					   channelId=312;
				   }
				   //初中、小学政治
				   if(channelId.toString().equals("317")){
					   channelId=309;
				   }
				   //高中、初中化学
				   if(channelId.toString().equals("307")){
					   channelId=296;
				   }
				   //高中、初中物理
				   if(channelId.toString().equals("306")){
					   channelId=295;
				   }
				   //高中、初中生物
				   if(channelId.toString().equals("308")){
					   channelId=297;
					}
				   //高中、初中语文
					if(channelId.toString().equals("142")){
						channelId=140;
					}
					//高中、初中、小学心理健康
					if(channelId.toString().equals("421")||channelId.toString().equals("422")){
						channelId=420;
					}
			   }else if(str[1].equals("colu")){
				   columnId=Integer.valueOf(str[0]);
			   }else if(str[1].equals("chan")){
				   channelId=Integer.valueOf(str[0]);
				   //高中、初中、小学美术
				   if(channelId.toString().equals("316")||channelId.toString().equals("323")){
					   channelId=305;
				   }
				   //高中、初中、小学音乐
				   if(channelId.toString().equals("315")||channelId.toString().equals("322")){
					   channelId=304;
				   }
				   //高中、初中、小学体育
				   if(channelId.toString().equals("314")||channelId.toString().equals("321")){
					   channelId=303;
				   }
				   //高中、初中、小学综合实践
				   if(channelId.toString().equals("417")||channelId.toString().equals("418")){
					   channelId=416;
				   }
				   //高中、初中地理
				   if(channelId.toString().equals("311")){
					   channelId=300;
				   }
				   //高中、初中历史
				   if(channelId.toString().equals("310")){
					   channelId=299;
				   }
				   //初中、小学信息技术
				   if(channelId.toString().equals("319")){
					   channelId=312;
				   }
				   //初中、小学政治
				   if(channelId.toString().equals("317")){
					   channelId=309;
				   }
				   //高中、初中化学
				   if(channelId.toString().equals("307")){
					   channelId=296;
				   }
				   //高中、初中物理
				   if(channelId.toString().equals("306")){
					   channelId=295;
				   }
				   //高中、初中生物
				   if(channelId.toString().equals("308")){
					   channelId=297;
					}
				   //高中、初中语文
					if(channelId.toString().equals("142")){
						channelId=140;
					}
					//高中、初中、小学心理健康
					if(channelId.toString().equals("421")||channelId.toString().equals("422")){
						channelId=420;
					}
			   }
		   }
		  
			   String result =blogAct.blog_ajax_update(id, title, author, description, txt, tagStr,
					   columnId,channelId, mediaPath,mediaType,attachmentPaths,
						attachmentNames, attachmentFilenames
						,picPaths,picDescs,null,charge, chargeAmount,
						nextUrl, request, response, model,pw);
			   try {
				json.put("code", result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  return json.toString();
				  
		
	}
	
	/**
	 * 会员投稿删除
	 * 
	 * @param ids
	 *            待删除的文章ID数组
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_delete.jspx")
	public void delete(Integer[] ids, HttpServletRequest request,
			String nextUrl, HttpServletResponse response, ModelMap model) {
		super.delete(ids, request, nextUrl, response, model);
	}
	
	@RequestMapping("/member/o_upload_media.jspx")
	public @ResponseBody String uploadMedia(
			@RequestParam(value = "mediaFile", required = false) MultipartFile file,
			String filename, HttpServletRequest request, ModelMap model,HttpServletResponse response ) {
		
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		
		JSONObject object = new JSONObject();
		try {
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file, request);
		if (errors.hasErrors()) {
			log.error(errors.getErrors().get(0));
			//model.addAttribute("error", errors.getErrors().get(0));
			//return FrontUtils.getTplPath(request, site.getSolutionPath(),
				//	TPLDIR_MEMBER, CONTRIBUTE_Uerrors.getErrors().get(0)PLOADMIDIA);
			object.put("code", "fail");
			object.put("msg", errors.getErrors().get(0));
			String newStr = new String(object.toString().getBytes("UTF-8"), "ISO8859_1");
			return newStr;
		}
		// TODO 检查允许上传的后缀
		
			String fileUrl;
			if (site.getConfig().getUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(dbFilePath.length());
					fileUrl = dbFileMng.storeByFilename(filename, file
							.getInputStream());
				} else {
					fileUrl = dbFileMng.storeByExt(site.getUploadPath(), ext,
							file.getInputStream());
					// 加上访问地址
					fileUrl = request.getContextPath() + dbFilePath + fileUrl;
				}
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(ftpUrl.length());
					fileUrl = ftp.storeByFilename(filename, file
							.getInputStream());
				} else {
					fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
							.getInputStream());
					// 加上url前缀
					fileUrl = ftpUrl + fileUrl;
				}
			} else {
				String ctx = request.getContextPath();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(ctx.length());
					fileUrl = fileRepository.storeByFilename(filename, file);
				} else {
					fileUrl = fileRepository.storeByExt(site.getUploadPath(),
							ext, file);
					// 加上部署路径
					fileUrl = ctx + fileUrl;
				}
			}
			cmsUserMng.updateUploadSize(user.getId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			fileMng.saveFileByPath(fileUrl, fileUrl, false);
			object.put("code", "success");
			object.put("mediaPath", fileUrl);
		} catch (IllegalStateException e) {
			//model.addAttribute("error", e.getMessage());
			log.error(e.getMessage(),e);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			//model.addAttribute("error", e.getMessage());
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
		}
	
		return object.toString();
	}
	
	@RequestMapping("/member/o_upload_attachment.jspx")
	public String uploadAttachment(
			@RequestParam(value = "attachmentFile", required = false) MultipartFile file,
			String attachmentNum, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user= CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file,request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_MEMBER, CONTRIBUTE_UPLOADATTACHMENT);
		}
		// TODO 检查允许上传的后缀
		try {
			String fileUrl;
			if (site.getConfig().getUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				fileUrl = dbFileMng.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上访问地址
				fileUrl = request.getContextPath() + dbFilePath + fileUrl;
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上url前缀
				fileUrl = ftpUrl + fileUrl;
			} else {
				String ctx = request.getContextPath();
				fileUrl = fileRepository.storeByExt(site.getUploadPath(), ext,
						file);
				// 加上部署路径
				fileUrl = ctx + fileUrl;
			}
			cmsUserMng.updateUploadSize(user.getId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			fileMng.saveFileByPath(fileUrl, origName, false);
			model.addAttribute("attachmentPath", fileUrl);
			model.addAttribute("attachmentName", origName);
			model.addAttribute("attachmentNum", attachmentNum);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, CONTRIBUTE_UPLOADATTACHMENT);
	}
	
	private WebErrors validateUpload(MultipartFile file,
			HttpServletRequest request) {
		String origName = file.getOriginalFilename();
		CmsUser user= CmsUtils.getUser(request);
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
		int fileSize = (int) (file.getSize() / 1024);
		WebErrors errors = WebErrors.create(request);
		if (errors.ifNull(file, "file")) {
			return errors;
		}
		if(origName!=null&&(origName.contains("/")||origName.contains("\\")||origName.indexOf("\0")!=-1)){
			errors.addErrorCode("upload.error.filename", origName);
		}
		//非允许的后缀
		if(!user.isAllowSuffix(ext)){
			errors.addErrorCode("upload.error.invalidsuffix", ext);
			return errors;
		}
		//超过附件大小限制
		if(!user.isAllowMaxFile((int)(file.getSize()/1024))){
			errors.addErrorCode("upload.error.toolarge",origName,user.getGroup().getAllowMaxFile());
			return errors;
		}
		//超过每日上传限制
		if (!user.isAllowPerDay(fileSize)) {
			long laveSize=user.getGroup().getAllowPerDay()-user.getUploadSize();
			if(laveSize<0){
				laveSize=0;
			}
			errors.addErrorCode("upload.error.dailylimit", laveSize);
		}
		return errors;
	}
	

	//获取最上一级模板id
	@RequestMapping(value = "/member/channelId.jspx")
	public void aliPayOrderQuery(String channelId,HttpServletRequest request,
			HttpServletResponse response, ModelMap model)
					throws UnsupportedEncodingException, JSONException {
		JSONObject json = new JSONObject();
		CmsSite site=CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		//获取上级栏目ID
		Channel ids = channelMng.findById(Integer.valueOf(channelId));
		if(null!=ids.getModel()){
			json.put("status", ids.getModel().getId());
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	public String findUserId(String userName){
		CmsUser id = channelMng.findUserId(userName);
		return id.getId().toString();
	}
	
	@Autowired
	private DbFileMng dbFileMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private CmsFileMng fileMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	protected BlogAct blogAct;
	@Autowired
	protected BlogCommon blogCommon;
	@Autowired
	protected ColumnsMng columnsMng;
	
	
	//博客主页
	@RequestMapping(value = "/blog/index.jspx")
	public String blog_index(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) { 
		 return blogAct.blog_index(queryTitle, modelId, queryChannelId, "tpl.blogCenter",
				pageNo, request, model);
	} 
	
	
	//博客主页
	@RequestMapping(value = "/blog/indexs.jspx")
	public String blog_indexs(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) { 
		return blogAct.blog_indexs(queryTitle, modelId, queryChannelId, "tpl.blogCenter",
				pageNo, request, model);
	} 
	//局部刷新栏目
	@RequestMapping(value = "/blog/refreshColumn.jspx")
	public String refreshColumn(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) { 
		return blogAct.refreshColumn(queryTitle, modelId, queryChannelId,null,
				pageNo, request, model);
	} 
	
	@RequestMapping(value = "/blog/tzsetting.jspx")
	public String tzsetting(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return blogAct.tzsetting(request, response, model);
	}

	/**
	 * 博客更新参数保存
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/blog/updateSetting.jspx")
	public void updateSetting(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		blogAct.updateSetting(request, response, model);
	}
	/**
	 * 博客更新参数保存-局部刷新
	 * @param request
	 * @param response
	 * @param model
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/updateSetting_refresh.jspx")
	public void updateSetting_refresh(HttpServletRequest request, HttpServletResponse response,ModelMap model) throws JSONException {
		blogAct.updateSetting_refresh(request, response, model);
	}
	
	/**
	 * 博客文章
	 */
	@RequestMapping(value = "/blog/contribute_list.jspx")
	public String blog_list(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.blog_list(queryTitle, modelId, queryChannelId, CONTRIBUTE_LIST,
				pageNo, request, model);
	}
	
	/**
	 * 局部刷新博客文章
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/contribute_list_own.jspx")
	public   String blog_list3(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,HttpServletResponse response,
			ModelMap model) throws JSONException {
		return  blogAct.blog_list_own(queryTitle, modelId, queryChannelId, null,
				pageNo, request, model);
		 
	}
	
	
	/**
	 * 局部刷新好友博客文章
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/contribute_list_friends.jspx")
	public String blog_list_friends(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,HttpServletResponse response,
			ModelMap model) throws JSONException {
		return  blogAct.blog_list_friends(queryTitle, modelId, queryChannelId, null,
				pageNo, request, model);
		 
	}
	
	
	
	
	//好友博客栏目
	@RequestMapping(value = "/blog/contribute_list_friend.jspx")
	public String blog_list_friend(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.blog_list_friend(queryTitle, modelId, queryChannelId, "tpl.testPage",
				pageNo, request, model);
	}

	/**
	 * 新建博客文章
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/contribute_add.jspx")
	public String blog_add(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return blogAct.blog_add(true, CONTRIBUTE_ADD, request, response, model);
	}
	
	/**
	 * 新建博客文章
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/contribute_ajax_add.jspx")
	public String blog_ajax_add(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return blogAct.blog_ajax_add(true, CONTRIBUTE_ADD, request, response, model);
	}
	/**
	 * 博客栏目列表
	 * @param queryTitle
	 * @param modelId
	 * @param queryChannelId
	 * @param pageNo
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/columns_list.jspx")
	public String blog_columns_list(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.blog_columns_list(queryTitle, modelId, queryChannelId, "tpl.columnsList",
				pageNo, request, model);
	}
	
	/**
	 * 新建博客栏目--局部刷新
	 * @param request
	 * @param response
	 * @param model
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/columns_add_refresh.jspx")
	public void columns_add_refresh(HttpServletRequest request, HttpServletResponse response,ModelMap model) throws JSONException {
		blogAct.columns_add_refresh(request, response, model);
	}
	
	
	
	/**
	 * 跳转到博客更新参数页面
	 * @param id
	 * @param orderId
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/update_tz.jspx")
	public String update_tz(String id, HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return blogAct.update_tz(id,request, response, model);
	}
	
	/**
	 * 博客更新博客栏目
	 * @param request
	 * @param response
	 * @param model
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/columns_updates_refresh.jspx")
	public void columns_updates_refresh(HttpServletRequest request, HttpServletResponse response,ModelMap model) throws JSONException {
		blogAct.columns_updates_refresh(request, response, model);
	}
	
	
	/**
	 * 新建博客栏目
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/blog/newColumn.jspx")
	public String columns_add(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return blogAct.columns_add(request, response, model);
	}
	

	/**
	 * 删除博客栏目
	 * @param request
	 * @param response
	 * @param model
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/deleteColumnRefresh.jspx")
	public void columns_delete_refresh(HttpServletRequest request, HttpServletResponse response,ModelMap model) throws JSONException {
		blogAct.columns_delete_refresh(request, response, model);
	}
	
	/**
	 * 删除博客栏目
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/blog/deleteColumn.jspx")
	public String columns_delete(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return blogAct.columns_delete(request, response, model);
	}
	
	/**
	 * 查看博客栏目下是否有文章
	 */
	
	@RequestMapping(value = "/blog/queryLanmuColumn.jspx")
	public void queryLanmuColumn(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		  blogAct.columns_query(request, response, model);
	}
	
	
	
	
	/**
	 * 博客更新博客栏目
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/blog/updateColumn.jspx")
	public String columns_update(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return blogAct.columns_update(request, response, model);
	}
	
	/**
	 * 博客文章编辑
	 * @param id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/contribute_edit.jspx")
	public String blog_edit(Integer id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return blogAct.blog_edit(id, CONTRIBUTE_EDIT, request, response, model);
	}

	/**
	 * 博客文章编辑
	 * @param id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/contribute_ajax_edit.jspx")
	public  String blog_ajax_edit(Integer id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return blogAct.blog_ajax_edit(id, CONTRIBUTE_EDIT, request, response, model);
	}
	/**
	 * 博客文章删除
	 */
	@RequestMapping(value = "/blog/contribute_delete.jspx")
	public void blog_delete(Integer ids,Integer columnId,Integer channelId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		blogAct.blog_delete(ids,columnId,channelId, request,  response, model);
	}
	
	/**
	 * ajax文章删除
	 */
	@RequestMapping(value = "/blog/ajax_contribute_delete.jspx")
	public  @ResponseBody   String ajax_contribute_delete(Integer ids,Integer columnId,Integer channelId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		String result = blogAct.ajax_blog_delete(ids, request,  response, model);
		return result;
	}
	
	/**
	 *跳转链接页面方法 
	  */
	@RequestMapping(value = "/blog/link_list.jspx")
	public String linkList(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return "/WEB-INF/t/cms/www/default/blog/login.html";
		}
		String linkUrl=user.getHyperlink();
		if(StringUtils.isNotEmpty(linkUrl)){
			model.addAttribute("linkUrls", linkUrl.replaceAll(" ", "\r\n"));
		}else{
			model.addAttribute("linkUrls", "");
		}
		return "/WEB-INF/t/cms/www/default/blog/link_list_refresh.html";
		//return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.linkList");
	}
	//局部刷新
	@RequestMapping(value = "/blog/add_update_refresh.jspx")
	public void add_update_refresh(String hyperlink,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		if(StringUtils.isEmpty(hyperlink)){
			blogAct.add_update_refresh(null,nextUrl,request, response, model);
		}else{
			hyperlink=hyperlink.replaceAll(" ", "");//强制去空格
			blogAct.add_update_refresh(hyperlink.replaceAll("\r\n", " "),nextUrl,request, response, model);
		}
		
	}
	@RequestMapping(value = "/blog/add_link.jspx")
	public String custom(String hyperlink,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		if(StringUtils.isEmpty(hyperlink)){
			return blogAct.link_save(null,nextUrl,request, response, model);
		}else{
			hyperlink=hyperlink.replaceAll(" ", "");//强制去空格
			return blogAct.link_save(hyperlink.replaceAll("\r\n", " "),nextUrl,request, response, model);
		}
		
	}
	
	/**
	 *跳转好友页面方法 
	 */
	@RequestMapping(value = "/blog/friends.jspx")
	public String friends(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return "/WEB-INF/t/cms/www/default/blog/login.html";
		}
		String friends = user.getFriends();
		if(StringUtils.isNotEmpty(friends)){
			model.addAttribute("friends", friends.replaceAll(" ", "\r\n"));
		}else{
			model.addAttribute("friends", "");
		}
		return "/WEB-INF/t/cms/www/default/blog/friends_refresh.html";
		//return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.friends");
	}
	//局部刷新
	@RequestMapping(value = "/blog/add_friends_refresh.jspx")
	public void add_friends_refresh(String friends,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		if(StringUtils.isEmpty(friends)){
			blogAct.add_friends_refresh(null,nextUrl,request, response, model);
		}else{
			friends=friends.replaceAll(" ", "");//强制去空格
			blogAct.add_friends_refresh(friends.replaceAll("\r\n", " "),nextUrl,request, response, model);
		}
		
	}

	@RequestMapping(value = "/blog/add_friends.jspx")
	public void friends(String friends,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		if(StringUtils.isEmpty(friends)){
			blogAct.friends_save(null,nextUrl,request, response, model);
		}else{
			friends=friends.replaceAll(" ", "");//强制去空格
			blogAct.friends_save(friends.replaceAll("\r\n", " "),nextUrl,request, response, model);
		}
	}
	
	/**
	 *跳转好友博客页面方法 
	  */
	@RequestMapping(value = "/blog/find_all_url_friend.jspx")
	public String findAllInfo(String userIds,String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.friendCenter(userIds,queryTitle, modelId, queryChannelId, "tpl.testPage",
				pageNo, request, model);
	}
	
	
	/**
	 *跳转好友博客页面方法  局部刷新
	  */
	@RequestMapping(value = "/blog/find_all_url_friends.jspx")
	public String find_all_url_friends(String userIds,String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.find_all_url_friends(userIds,queryTitle, modelId, queryChannelId, "tpl.testPage",
				pageNo, request, model);
	}
	
	
	/**
	 *博客关注 
	 * @throws IOException 
	  */
	@RequestMapping(value = "/blog/focus.jspx")
	public void blog_focus(String focusUserId,String focusUserName, HttpServletRequest request,HttpServletResponse response) throws IOException {
		blogAct.blog_focus(focusUserId,focusUserName, request, response);
	}
	
	/**
	 * 博客检验是否关注
	 */
	@RequestMapping(value = "blog/focuscheck.jspx")
	public void blog_focus_check(String focusUserId, HttpServletRequest request,HttpServletResponse response) throws IOException {
		blogAct.blog_focus_check(focusUserId,request, response);
	}
	
	/**
	 * 博客取消关注
	 *  
	 */
	@RequestMapping(value = "blog/cancelfocus.jspx")
	public void blog_cancel_focus(String focusUserId, HttpServletRequest request,HttpServletResponse response) throws IOException {
		blogAct.blog_cancel_focus(focusUserId,request, response);
	}
	
	/**
	 * 博客背景
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/changeTheme.jspx")
	public void updateTheme(String theme, String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException{
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if (user == null) {
			FrontUtils.showLogin(request, model, site);
		}
		if(StringUtils.isNotBlank(theme)){
			user.setTheme(theme);
			cmsUserMng.updateUser(user);
			log.info("update CmsUser success. id={}", user.getId());
			json.put("status", 1);
		}	
		ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 *读取评论消息数目
	 */
	@RequestMapping(value = "/member/findCommentNu.jspx")
	public void findCommentNu(HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site=CmsUtils.getSite(request);
		if(user!=null){
			JSONObject json = new JSONObject();
			FrontUtils.frontData(request, model, site);
			Date lastDate=user.getLastLoginTime();
			int userId=user.getId();
			List<CmsComment> listNum=contentMng.findCommentByConid(userId,lastDate);
			json.put("status", listNum.size());
			ResponseUtils.renderJson(response, json.toString());
		}else{
			FrontUtils.showLogin(request, model, site);
		}
	}
	
	
	/**
	 *校验添加的好友是否存在!
	 */
	@RequestMapping(value = "/member/checkAddfinds.jspx")
	public void checkAddfinds(String friends,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		if(user==null){
			FrontUtils.showLogin(request, model, site);
		}else{
			String[] strs=friends.split("~");
			int addnum=1;
			String no="";
			for(int i=0;i<strs.length;i++){
				CmsUser u=channelMng.findUserImage(strs[i].split("=")[1].toString());
				if(null==u){
					no+=strs[i].split("=")[1].toString()+",";
				}else{
					if(user.getId().equals(u.getId())){
						no="repeatName";
						break;
					}
				}
			}
			if(no!=""){
				if(no.equals("repeatName")){
					addnum=2;
				}else{
					addnum=0;
				}
			}else{
				addnum=1;
			}
			JSONObject json = new JSONObject();
			FrontUtils.frontData(request, model, site);
			if(addnum==0){
				json.put("status",no.substring(0,no.length() - 1));
			}else if(addnum==2){
				json.put("status",2);
			}else{
				json.put("status","");	
			}
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	
	/**
	 *跳转登陆人数据统计页面方法 
	 */
	@RequestMapping(value = "/blog/dataStatistics.jspx")
	public String dataStatistics(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				return FrontUtils.showLogin(request, model, site);
			}
		}
		model =blogCommon.getHyperlink(request,model,user);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    int totalCount = blogCommon.getTotalArticleNum(model,user);
	    model.addAttribute("articleCount", totalCount);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getTotalCoverCommentNum(model, user);
 		model = blogCommon.getTotalReadNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
 		model = blogCommon.getFriendLeft(user.getId(),model,1);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.dataStatistics");
	}
	
	/**
	 *跳转登陆人数据统计页面方法--弹框
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/dataStatisticsT.jspx")
	public void dataStatisticsT(HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user==null){
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				try {
					request.getRequestDispatcher("/login.jspx").forward(request, response);
					return;
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		int articleCount = blogCommon.getTotalArticleNum(model,user);
		model = blogCommon.getTotalCommentNum(model, user);
		model = blogCommon.getTotalCoverCommentNum(model, user);
		model = blogCommon.getTotalReadNum(model, user);
		FrontUtils.frontData(request, model, site);
		if(null!=user){
			json.put("blogVisitNum", user.getBlogVisitNum());
			json.put("articleCount", articleCount);
			json.put("coverCommentCount", model.get("coverCommentCount"));
			json.put("readCount", model.get("readCount"));
			json.put("commentCount", model.get("commentCount"));
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	/**
	 *跳转好友数据统计页面方法 
	 */
	@RequestMapping(value = "/blog/friendDataStatistics.jspx")
	public String friendDataStatistics(String userIds,String q,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		CmsUser userT=cmsUserMng.findById(Integer.valueOf(userIds.toString()));
		model =blogCommon.getHyperlink(request,model,userT);
		model = blogCommon.getColumn(request,model,userT);
	    model = blogCommon.getChannel(request,model,userT,site);
	    int totalCount = blogCommon.getTotalArticleNum(model,userT);
	    model.addAttribute("articleCount", totalCount);
 		model = blogCommon.getTotalCommentNum(model, userT);
 		model = blogCommon.getTotalCoverCommentNum(model, userT);
 		model = blogCommon.getTotalReadNum(model, userT);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,userT);
 		model = blogCommon.getAddFriends(request, model,userT,user);
 		model = blogCommon.getFouces(request, model,userT,user);
 		model = blogCommon.getFriendLeft(user.getId(),model,1);
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Focus> list = (new BlogDao()).findMaxFocusCount( path);
	    List<Focus> l = null;
	    if(null != list){
	    	if(list.size()>3){
	    		l = new ArrayList<Focus>();
	    		for(int i =0;i<3;i++){
	    			l.add(list.get(i));
	    		}
	    	}
	    	if(null != l){
	    		model.addAttribute("focusMax", l);
	    	}else{
	    		model.addAttribute("focusMax", list);
	    	}
	    }
	    model.addAttribute("usert", userT);
	    model.addAttribute("userIds", userIds);
	    FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.friendDataStatistics");
	}
	
	/**
	 * 显示好友，关注，粉丝，明星博主数据-----自己数据
	 */
	@RequestMapping(value = "/blog/gotoDataShow.jspx")
	public String gotoDataShow(int dataFlag,HttpServletRequest request, HttpServletResponse response,ModelMap model,Integer pageNo) {
		return blogAct.gotoDataShow(dataFlag, request, response, model,pageNo);
	}
	
	/**
	 * 显示好友，关注，粉丝，明星博主数据-----好友博客数据
	 */
	@RequestMapping(value = "/blog/gotoDataShowFriend.jspx")
	public String gotoDataShowFriend(int dataFlag,HttpServletRequest request, HttpServletResponse response,ModelMap model,Integer pageNo,String userId) {
		return blogAct.gotoDataShowFriend(dataFlag, request, response, model,pageNo,userId);
	}
	
	/**
	 *查询访客 
	 */
	@RequestMapping(value = "/blog/visitor.jspx")
	public String visitor(String queryTitle, Integer modelId,Integer queryChannelId, Integer pageNo,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				return "/WEB-INF/t/cms/www/default/blog/login.html";
			}
		}
 		model = blogAct.getAllVisitors(queryTitle, modelId, queryChannelId,pageNo, request, model,user);
		FrontUtils.frontData(request, model, site);
		return "/WEB-INF/t/cms/www/default/blog/visitor_refresh.html";
		//return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.visitor");
	}
	
	
	/**
	 *好友访客页面 
	 */
	@RequestMapping(value = "/blog/friends_visitor.jspx")
	public String friends_visitor(String userIds,String q,Integer modelId,Integer queryChannelId, Integer pageNo,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			return FrontUtils.showLogin(request, model, site);
		}
		CmsUser userT=cmsUserMng.findById(Integer.valueOf(userIds.toString()));
		model =blogCommon.getHyperlink(request,model,userT);
		model = blogCommon.getColumn(request,model,userT);
	    model = blogCommon.getChannel(request,model,userT,site);
//	    model = blogCommon.blog_focus_find(Integer.parseInt(userIds),request,model);
//	    model = blogCommon.getLinks(model,userT);
//		model = blogCommon.getFriends(model,userT);
	    int totalCount = blogCommon.getTotalArticleNum(model,userT);
	    model.addAttribute("articleCount", totalCount);
 		model = blogCommon.getTotalCommentNum(model, userT);
 		model = blogCommon.getTotalCoverCommentNum(model, userT);
 		model = blogCommon.getTotalReadNum(model, userT);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,userT);
 		model = blogAct.getAllVisitors(q, modelId, queryChannelId,pageNo, request, model,userT);
 		model = blogCommon.getAddFriends(request, model,userT,user);
 		model = blogCommon.getFouces(request, model,userT,user);
 		model = blogCommon.getFriendLeft(user.getId(),model,1);
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Focus> list = (new BlogDao()).findMaxFocusCount( path);
	    List<Focus> l = null;
	    if(null != list){
	    	if(list.size()>3){
	    		l = new ArrayList<Focus>();
	    		for(int i =0;i<3;i++){
	    			l.add(list.get(i));
	    		}
	    	}
	    	if(null != l){
	    		model.addAttribute("focusMax", l);
	    	}else{
	    		model.addAttribute("focusMax", list);
	    	}
	    }
	    model.addAttribute("usert", userT);
	    model.addAttribute("userIds", userIds);
	    FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.friends_visitor");
	}
	
	
	/**
	 * 产生不重复的六位数
	 */
	@RequestMapping(value = "/blog/uniqueNum.jspx")
	public void uniqueNum(HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
			JSONObject json = new JSONObject();
			String no=CreateSerialNo.generateNumber();
			int nu=columnsMng.checkCode(no);
			while(nu==1){ 
				no=CreateSerialNo.generateNumber();
				nu=columnsMng.checkCode(no);
			}
			json.put("status",no);
			ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 * 加入群组
	 */
	@RequestMapping(value = "/blog/joinGroup.jspx")
	public void joinGroup(String code,String createUserId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			CmsUser userT=cmsUserMng.findById(Integer.valueOf(createUserId.toString()));
			List<Columns> cc=columnsMng.findInfoByCodeAndUserid(code,Integer.valueOf(createUserId.toString()));
			//Columns cu=columnsMng.findInfoByCode(code);
			JSONObject json = new JSONObject();
			CmsJoinGroup cjg=new CmsJoinGroup();
			cjg.setCreateUserId(userT);
			cjg.setJoinUserId(user);
			cjg.setJoinCode(code);
			cjg.setJoinTime(new Date());
			cjg.setColumnsId(cc.get(0));
			int joinStatus=columnsMng.saveJoinGroup(cjg);
			json.put("status",joinStatus);
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	/**
	 * 检查是否加入群组-页面现在用
	 */
	@RequestMapping(value = "/blog/checkJoinState.jspx")
	public void checkJoinState(String joinState,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			return;
		}
		JSONObject json = new JSONObject();
		try {
			int joinStatus = columnsMng.checkJoinState(joinState,user);
			json.put("status",joinStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	/**
	 * 保存批注
	 */
	@RequestMapping(value = "/blog/addTpHtml.jspx")
	public void saveAddTpHtml(String X,String Y,String contentId,String userId,String inputInfo,String idss,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			return;
		}
		JSONObject json = new JSONObject();
		try {
			String tbHtml= "<div class='"+idss+"' id='"+idss+"'"
					     + " style='display: block; top: "+Y+"px; left: "+X+"px; position: absolute; margin: 0px;"
					     + "border: 0px solid rgb(255, 102, 0); width: 25px; height: 25px; z-index: 99'>"
					     + "<h2 class='t"+idss+"' style='cursor: move; width: 25px; height: 25px; padding-left: 25px;'>"
					     + "</h2></div>";
			CmsPostilInfo cpi=new CmsPostilInfo();
			cpi.setAddHtml(tbHtml);
			cpi.setContentId(Integer.valueOf(contentId));
			cpi.setInputContent(inputInfo);
			cpi.setPostilUserId(user);
			cpi.setCreateTime(new Date());
			cpi.setDivId(idss);
			int joinStatus=columnsMng.saveAddTpHtml(cpi);
			json.put("status",joinStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 * 读取批注
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/blog/searchPostil.jspx")
	public void searchPostil(String contentId,HttpServletRequest request, String reportType,HttpServletResponse response) throws JSONException {
		JSONObject o;
		JSONArray arr = new JSONArray();
		List<CmsPostilInfo> postilList = columnsMng.findList(Integer.valueOf(contentId));
		for (CmsPostilInfo t : postilList) {
			o = new JSONObject();
			o.put("postilDiv", t.getAddHtml());
			o.put("postilInput", t.getInputContent());
			o.put("postilDivId", t.getDivId());
			o.put("postilName", t.getPostilUserId().getUsername());
			arr.put(o);
		}
		ResponseUtils.renderJson(response, arr.toString());

	}
	
	/**
	 * 更新批注坐标
	 */
	@RequestMapping(value = "/blog/updateDragCoordinate.jspx")
	public void updateDragCoordinate(String leftX,String topY,String postilId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user==null){
			return;
		}else{
			String tbHtml= "<div class='"+postilId+"' id='"+postilId+"'"
					+ " style='display: block; top: "+topY+"px; left: "+leftX+"px; position: absolute; margin: 0px;"
					+ "border: 0px solid rgb(255, 102, 0); width: 25px; height: 25px;'>"
					+ "<h2 class='t"+postilId+"' style='cursor: move; width: 25px; height: 25px; padding-left: 25px;'>"
					+ "</h2></div>";
			int joinStatus=columnsMng.updateDragCoordinate(leftX,topY,postilId,tbHtml);
			json.put("status",joinStatus);
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	/**
	 * 删除批注
	 */
	@RequestMapping(value = "/blog/delAddHtml.jspx")
	public void delAddHtml(String postilId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			FrontUtils.showLogin(request, model, site);
		}
		int joinStatus=columnsMng.delAddHtml(postilId);
		JSONObject json = new JSONObject();
		json.put("status",joinStatus);
		ResponseUtils.renderJson(response, json.toString());
	}
	/**
	 * 退出群组
	 */
	@RequestMapping(value = "/blog/signOutGroup.jspx")
	public void signOutGroup(String groupId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user==null){
			json.put("status","3");
			return;
		}
		int joinStatus=columnsMng.signOutGroup(groupId);
		json.put("status",joinStatus);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 * 删除群组
	 */
	@RequestMapping(value = "/blog/delGroup.jspx")
	public void delGroup(String groupId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			return;
		}
		JSONObject json = new JSONObject();
		try {
			int joinStatus = columnsMng.delGroup(groupId,user);
			json.put("status",joinStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	

	/**
	 *使用手册
	 */
	@RequestMapping(value = "/blog/useManual.jspx")
	public String useManual(String userIds,String q,Integer modelId,Integer queryChannelId, Integer pageNo,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		/*if(user==null){
			return FrontUtils.showLogin(request, model, site);
		}*/
	    FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.use_manual");
	}
	
	/**
	 * 添加/解除好友
	 */
	@RequestMapping(value = "/blog/addOrCancelFriends.jspx")
	public void addOrCancelFriends(String state,String uId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		CmsUser userT=cmsUserMng.findById(Integer.valueOf(uId.toString()));
		if(user!=null){
			if(Integer.valueOf(state)==0){
				//添加好友
				String newName=userT.getUsername()+"="+userT.getUsername();
				String ownFriend=user.getFriends();
				if(ownFriend!=null&&ownFriend!=""){
					String newFriend=ownFriend+" "+newName;
					user.setFriends(newFriend);
				}else{
					String newFriend=newName;
					user.setFriends(newFriend);
				}
				try {
					columnsMng.updateUFO(user);
					json.put("status",1);
				} catch (Exception e) {
					json.put("status",0);
					e.printStackTrace();
				}
			}else if(Integer.valueOf(state)==1){
				//解除好友
				String uname=userT.getUsername();
				String oldName=user.getFriends();
				String[] str=oldName.split(" ");
				String newFriend="";
				if(str.length!=1){
					for(int i=0;i<str.length;i++){
						if(!str[i].split("=")[1].equals(uname)){
							newFriend =str[i]+" "+newFriend;
						}
					}
					user.setFriends(newFriend.trim());
					try {
						columnsMng.updateUFO(user);
						json.put("status",1);
					} catch (Exception e) {
						json.put("status",0);
						e.printStackTrace();
					}
				}else{
					user.setFriends(null);
					try {
						columnsMng.updateUFO(user);
						json.put("status",1);
					} catch (Exception e) {
						json.put("status",0);
						e.printStackTrace();
					}
				}
			}
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	
	/**
	 * check密码是否为初始密码123456
	 */
	@RequestMapping(value = "/member/checkPwdss.jspx")
	public void checkPwd(HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user!=null){
			boolean pass = cmsUserMng.isPasswordValid(user.getId(), "123456");
			if(pass){
				json.put("status","1");
			}else{
				json.put("status","0");
			}
		}else{
			json.put("status","0");
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	/**
	 * 不在提示功能
	 */
	@RequestMapping(value = "/member/updateHint.jspx")
	public void updateHint(HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user!=null){
			channelMng.updateLinkUrl("1",user);
		}else{
			json.put("status","0");
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	/**
	 * 检查新建栏目的级别 1为一级栏目可以建二级栏目,2为二级栏目不再允许建栏目
	 */
	@RequestMapping(value = "/blog/checkLevel.jspx")
	public void checkLevel(String ccId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user!=null){
			Columns level = columnsMng.findById(Integer.valueOf(ccId));
			if(level.getColumsLevel()==1&&null==level.getUniqueCode()){
				json.put("status","1");
			}else if(level.getColumsLevel()==2){
				json.put("status","2");
			}else if(level.getColumsLevel()==1&&null!=level.getUniqueCode()){
				json.put("status","3");
			}
		}else{
			json.put("status","4");
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 * 检查该栏目下是否有子栏目
	 */
	@RequestMapping(value = "/blog/checkTwoColumsNum.jspx")
	public void checkTwoColumsNum(String ccId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user!=null){
			List<Columns> twoList=columnsMng.findTwoByParentId(Integer.valueOf(ccId));
			if(null!=twoList&&twoList.size()>0){
				//不允许删除,有子栏目
				json.put("status","1");
			}else{
				json.put("status","2");
			}
		}else{
			json.put("status","3");
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	/**
	 * 发送文章 
	 */
	@RequestMapping(value = "/blog/sendArticle.jspx", method = RequestMethod.POST)
	public @ResponseBody String sendArticle(Integer contentId,String sendee, Integer validateCode,String contTitle,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model){
		
		JSONObject object = new JSONObject();
		try {
			
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			object.put("code", "fail");
			object.put("msg", "5");//请先登录
			return object.toString();
		}
		int result =cmsUserMng.sendArticle(contentId,user.getId(),sendee,validateCode,contTitle);
		if(result==1){
			object.put("code", "fail");
			object.put("msg", "1");//发件人错误
		}else if(result==2){
			object.put("code", "fail");
			object.put("msg", "2");//验证码错误
		}else if(result==3){
			object.put("code", "fail");
			object.put("msg", "3");//栏目不允许发送错误
		}else if(result==4){
			object.put("code", "fail");
			object.put("msg", "4");//已经存在此文章
		}else if(result==6){
			object.put("code", "fail");
			object.put("msg", "6");//自己不能发送自己
		}else{
			object.put("code", "success");
			object.put("msg", "发送成功！");
		}
		}catch (Exception e){
			try {
				object.put("code", "fail");
				log.error(e.getMessage(),e);
			} catch (JSONException e1) {
				log.error(e.getMessage(),e);
			}
			log.error(e.getMessage(),e);
		}
		return object.toString();
	}

	/**
	 * 撤销文章 
	 */
	@RequestMapping(value = "/blog/cancelArticle.jspx", method = RequestMethod.POST)
	public @ResponseBody String cancelArticle(Integer contentId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model){
		
		JSONObject object = new JSONObject();
		try {
			
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			object.put("code", "fail");
			object.put("msg", "2");//请先登录
			return object.toString();
		}
		Integer result =cmsUserMng.cancelArticle(contentId, user.getId());
		
		object.put("code", "success");
		}catch (Exception e){
			try {
				object.put("code", "fail");
			} catch (JSONException e1) {
				log.error(e.getMessage(),e);
			}
			log.error(e.getMessage(),e);
		}
		return object.toString();
	}
	
	
	/**
	 * 检测该用户下验证码唯一性
	 */
	@RequestMapping(value = "/blog/checkGroupCode.jspx")
	public void checkGroupCode(String code,String columnId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user!=null){
			List<Columns> cc=null;
			if(StringUtils.isNotEmpty(columnId)){
				//修改判断
				cc=columnsMng.findInfoByCodeAndUserids(code,user.getId(),Integer.valueOf(columnId));
			}else{
				//新建判断
				cc=columnsMng.findInfoByCodeAndUserid(code,user.getId());
			}
			if(null!=cc&&cc.size()>0){
				//该用户下验证码重复
				json.put("status","1");
			}else{
				json.put("status","0");
			}
		}else{
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 * 收录文章 
	 */
	@RequestMapping(value = "/blog/embodyArticle.jspx", method = RequestMethod.POST)
	public @ResponseBody String embodyArticle(Integer contentId,Integer friendId, Integer columnId,String contTitle,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model){
		
		JSONObject object = new JSONObject();
		try {
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			object.put("code", "fail");
			object.put("msg", "2");//请先登录
			return object.toString();
		}
		Integer result  = cmsUserMng.embodyArticle(contentId, user.getId(), friendId, columnId,contTitle);
		if(result==1){
			object.put("code", "fail");
			object.put("msg", "1");//已经收录
		}else if(result==3){
			object.put("code", "fail");
			object.put("msg", "3");//改文章不允许收录
		}else{
			object.put("code", "success");
			object.put("msg", "发送成功！");
		}
		
		}catch (Exception e){
			try {
				object.put("code", "fail");
				log.error(e.getMessage(),e);
			} catch (JSONException e1) {
				log.error(e.getMessage(),e);
			}
			log.error(e.getMessage(),e);
		}
		return object.toString();
	}

	/**
	 * 移除用户收录的文章 
	 */
	@RequestMapping(value = "/blog/removeArticle.jspx", method = RequestMethod.POST)
	public @ResponseBody String removeArticle(Integer contentId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model){
		
		JSONObject object = new JSONObject();
		try {
			
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			object.put("code", "fail");
			object.put("msg", "2");//请先登录
			return object.toString();
		}
		Integer result =cmsUserMng.removeArticle(contentId, user.getId());
		
		object.put("code", "success");
		}catch (Exception e){
			try {
				object.put("code", "fail");
			} catch (JSONException e1) {
				log.error(e.getMessage(),e);
			}
			log.error(e.getMessage(),e);
		}
		return object.toString();
	}
	/**
	 * 检测用户以及验证码是否存在
	 */
	@RequestMapping(value = "/blog/checkExist.jspx")
	public void checkExistjspx(String groupName,String groupCode,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user!=null){
			CmsUser users=cmsUserMng.findByUsername(groupName);
			if(null!=users){
				List<Columns> cc=columnsMng.findInfoByCodeAndUserid(groupCode,users.getId());
				if(null!=cc&&cc.size()>0){
					json.put("status",users.getId());
				}else{
					//验证码不存在
					json.put("status","2");
				}
			}else{
				//群组账号不存在
				json.put("status","1");
			}	
		}else{
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	/**
	 * 加入群组
	 */
	@RequestMapping(value = "/blog/joinGroupCode.jspx")
	public void joinGroupCode(String code,String createUserId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user==null){
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			List<CmsJoinGroup> joinGroup=columnsMng.getJoinGroupByUserIdAndCode(user.getId(),code);
			if(null!=joinGroup&&joinGroup.size()>0){
				//重复加入群组
				json.put("status","2");
			}else{
				CmsUser userT=cmsUserMng.findById(Integer.valueOf(createUserId.toString()));
				List<Columns> cc=columnsMng.findInfoByCodeAndUserid(code,Integer.valueOf(createUserId.toString()));
				CmsJoinGroup cjg=new CmsJoinGroup();
				cjg.setCreateUserId(userT);
				cjg.setJoinUserId(user);
				cjg.setJoinCode(code);
				cjg.setJoinTime(new Date());
				cjg.setColumnsId(cc.get(0));
				try {
					columnsMng.saveJoinGroup(cjg);
					json.put("status","1");
					json.put("sname",cc.get(0).getColumnName());
					json.put("sid",cc.get(0).getColumnId());
				} catch (Exception e) {
					json.put("status","0");
					e.printStackTrace();
				}
			}
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	
	/**
	 * 新退出群组
	 */
	@RequestMapping(value = "/blog/signOutGroups.jspx")
	public void signOutGroups(String groupId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if(user==null){
			json.put("status","3");
		}else{
			Columns cc=columnsMng.findById(Integer.valueOf(groupId));
			int joinStatus=columnsMng.signOutGroups(cc,user);
			json.put("status",joinStatus);
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 * 文章置顶
	 */
	@RequestMapping(value = "/blog/contentStick.jspx", method = RequestMethod.POST)
	public @ResponseBody String contentStick(Integer contentId,String contentTitle,String releaseDate,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model){
		
		JSONObject object = new JSONObject();
		try {
			
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			object.put("code", "fail");
			object.put("msg", "2");//请先登录
			return object.toString();
		}
		SimpleDateFormat  dfm = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		ContentStick contentStick = new ContentStick();
		contentStick.setContentId(contentId);
		contentStick.setContentTime(dfm.parse(releaseDate));
		contentStick.setStickTime(new Date());
		contentStick.setStickUserId(user.getId());
		contentStick.setContentTitle(contentTitle);
		
		//置顶
		int result = cmsUserMng.contentStick(contentStick);
		if(result==1){
			object.put("code", "fail");
			object.put("msg", "3");//最多置顶10篇文章
			return object.toString();
		}
		
		object.put("code", "success");
		}catch (Exception e){
			try {
				object.put("code", "fail");
			} catch (JSONException e1) {
				log.error(e.getMessage(),e);
			}
			log.error(e.getMessage(),e);
		}
		return object.toString();
	}
	/**
	 * 取消文章置顶
	 */
	@RequestMapping(value = "/blog/cancelStick.jspx", method = RequestMethod.POST)
	public @ResponseBody String cancelStick(Integer contentId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model){
		
		JSONObject object = new JSONObject();
		try {
			
		CmsUser user = CmsUtils.getUser(request);
		if(user==null){
			object.put("code", "fail");
			object.put("msg", "2");//请先登录
			return object.toString();
		}
		
		//取消置顶
		cmsUserMng.cancelContentStick(contentId, user.getId());
		
		object.put("code", "success");
		}catch (Exception e){
			try {
				object.put("code", "fail");
			} catch (JSONException e1) {
				log.error(e.getMessage(),e);
			}
			log.error(e.getMessage(),e);
		}
		return object.toString();
	}
	
}
