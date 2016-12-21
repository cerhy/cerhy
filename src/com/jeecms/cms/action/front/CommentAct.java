package com.jeecms.cms.action.front;

import static com.jeecms.cms.Constants.TPLDIR_CSI;
import static com.jeecms.cms.Constants.TPLDIR_SPECIAL;
import static com.jeecms.cms.Constants.TPLDIR_CHANNEL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeecms.cms.entity.assist.CmsComment;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.ChannelExt;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.manager.assist.CmsCommentMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentDocMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;

@Controller
public class CommentAct {
	private static final Logger log = LoggerFactory.getLogger(CommentAct.class);

	public static final String COMMENT_PAGE = "tpl.commentPage";
	public static final String COMMENT_LIST = "tpl.commentList";
	public static final String COMMENT_INPUT = "tpl.commentInput";
	public static final String COMMENT_CHANNEL_INPUT = "tpl.commentChannelInput";

	@RequestMapping(value = "/comment*.jspx", method = RequestMethod.GET)
	public String page(Integer contentId, Integer pageNo,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		if(contentId==null){
			return FrontUtils.showMessage(request, model,
			"comment.contentNotFound");
		}
		Content content = contentMng.findById(contentId);
		if (content == null) {
			return FrontUtils.showMessage(request, model,
					"comment.contentNotFound");
		}
		if (content.getChannel().getCommentControl() == ChannelExt.COMMENT_OFF) {
			return FrontUtils.showMessage(request, model, "comment.closed");
		}
		// 将request中所有参数保存至model中。
		model.putAll(RequestUtils.getQueryParams(request));
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		model.addAttribute("content", content);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_SPECIAL, COMMENT_PAGE);
	}
	
	@RequestMapping(value = "/comment_input_csi.jspx")
	public String custom(String tpl,Integer contentId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		log.debug("visit csi custom template: {}", tpl);
		CmsSite site = CmsUtils.getSite(request);
		if(contentId==null){
			return FrontUtils.showMessage(request, model,
			"comment.contentNotFound");
		}
		Content content = contentMng.findById(contentId);
		if (content == null) {
			return FrontUtils.showMessage(request, model,
					"comment.contentNotFound");
		}
		if (content.getChannel().getCommentControl() == ChannelExt.COMMENT_OFF) {
			return FrontUtils.showMessage(request, model, "comment.closed");
		}
		// 将request中所有参数保存至model中。
		model.putAll(RequestUtils.getQueryParams(request));
		model.addAttribute("content", content);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_SPECIAL, COMMENT_INPUT);
	}

	@RequestMapping(value = "/comment_input_channel.jspx")
	public String channelCustom(String tpl,Integer channelId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		log.debug("visit csi custom template: {}", tpl);
		CmsSite site = CmsUtils.getSite(request);
		if(channelId==null){
			return FrontUtils.showMessage(request, model,
			"comment.channelNotFound");
		}
		Channel channel = channelMng.findById(channelId);
		if (channel == null) {
			return FrontUtils.showMessage(request, model,
					"comment.channelNotFound");
		}
		if (channel.getCommentControl() == ChannelExt.COMMENT_OFF) {
			return FrontUtils.showMessage(request, model, "comment.closed");
		}
		// 将request中所有参数保存至model中。
		model.putAll(RequestUtils.getQueryParams(request));
		model.addAttribute("channel", channel);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_SPECIAL, COMMENT_CHANNEL_INPUT);
	}
	
	@RequestMapping(value = "/comment_list.jspx")
	public String list(Integer siteId,Integer contentId, Integer parentId,
			Integer greatTo,Integer recommend, Integer checked, 
			Integer orderBy, Integer count,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		if (count == null || count <= 0 || count > 200) {
			count = 200;
		}
		boolean desc;
		if (orderBy == null || orderBy == 0) {
			desc = true;
		} else {
			desc = false;
		}
		Boolean rec;
		if (recommend != null) {
			rec = recommend != 0;
		} else {
			rec = null;
		}
		Boolean chk;
		if (checked != null) {
			chk = checked != 0;
		} else {
			chk = null;
		}
		List<CmsComment> list = cmsCommentMng.getListForTag(siteId,contentId,
				parentId,greatTo, chk, rec, desc, count);
		// 将request中所有参数
		model.putAll(RequestUtils.getQueryParams(request));
		model.addAttribute("list", list);
		model.addAttribute("contentId", contentId);
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_CSI, COMMENT_LIST);
	}

	/**
	 * 获取栏目评论列表
	 * @param siteId
	 * @param channelId
	 * @param contentId
	 * @param parentId
	 * @param greatTo
	 * @param recommend
	 * @param checked
	 * @param orderBy
	 * @param count
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/comment_channel_list.jspx")
	public String listChannel(Integer siteId, Integer channelId,Integer parentId,
			Integer greatTo,Integer recommend, Integer checked, 
			Integer orderBy, Integer count,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		if (count == null || count <= 0 || count > 200) {
			count = 200;
		}
		boolean desc;
		if (orderBy == null || orderBy == 0) {
			desc = true;
		} else {
			desc = false;
		}
		Boolean rec;
		if (recommend != null) {
			rec = recommend != 0;
		} else {
			rec = null;
		}
		Boolean chk;
		if (checked != null) {
			chk = checked != 0;
		} else {
			chk = null;
		}
		List<CmsComment> list = cmsCommentMng.getListForTag(siteId, channelId,null,
				parentId,greatTo, chk, rec, desc, count);
		// 将request中所有参数
		model.putAll(RequestUtils.getQueryParams(request));
		model.addAttribute("list", list);
		model.addAttribute("channelId", channelId);
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_CHANNEL, COMMENT_LIST);
	}
	
	@RequestMapping(value = "/comment.jspx", method = RequestMethod.POST)
	public void submit(Integer contentId, Integer parentId,Integer score,
			String text, String captcha,String sessionId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException, IOException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if (contentId == null) {
			json.put("success", false);
			json.put("status", 100);
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		if (StringUtils.isBlank(text)) {
			json.put("success", false);
			json.put("status", 101);
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		if (user == null || user.getGroup().getNeedCaptcha()) {
			// 验证码错误
			try {
				if (!imageCaptchaService.validateResponseForID(session
						.getSessionId(request, response), captcha)) {
					json.put("success", false);
					json.put("status", 1);
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
			} catch (CaptchaServiceException e) {
				json.put("success", false);
				json.put("status", 1);
				log.warn("", e);
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
		}
		Content content = contentMng.findById(contentId);
		if (content == null) {
			// 内容不存在
			json.put("success", false);
			json.put("status", 2);
		} else if (content.getChannel().getCommentControl() == ChannelExt.COMMENT_OFF) {
			// 评论关闭
			json.put("success", false);
			json.put("status", 3);
		} else if ((content.getChannel().getCommentControl() == ChannelExt.COMMENT_LOGIN|content.getChannel().getCommentControl() == ChannelExt.COMMENT_LOGIN_MANY)
				&& user == null) {
			// 需要登录才能评论
			json.put("success", false);
			json.put("status", 4);
		}else if(content.getChannel().getCommentControl() == ChannelExt.COMMENT_LOGIN&&user!=null){
			if (hasCommented(user, content)) {
				// 已经评论过，不能重复评论
				json.put("success", false);
				json.put("status", 5);
			}
		}else {
			boolean checked = false;
			Integer userId = null;
			if (user != null) {
				checked = !user.getGroup().getNeedCheck();
				userId = user.getId();
			}
			ContentDoc doc=content.getContentDoc();
			if(doc!=null){
				doc.setAvgScore(getNewAvgScore(content, score));
				contentDocMng.update(doc,content);
			}
			cmsCommentMng.comment(score,text, RequestUtils.getIpAddr(request),
					contentId, site.getId(), userId, checked, false,parentId);
			json.put("success", true);
			json.put("status", 0);
		}
		ResponseUtils.renderJson(response, json.toString());
	}

	/**
	 * 栏目评论
	 * @param contentId
	 * @param parentId
	 * @param score
	 * @param text
	 * @param captcha
	 * @param sessionId
	 * @param request
	 * @param response
	 * @param model
	 * @throws JSONException
	 * @throws IOException
	 */
	@RequestMapping(value = "/comment_channel.jspx", method = RequestMethod.POST)
	public void submitChannel(Integer channelId, Integer parentId,Integer score,
			String text, String captcha,String sessionId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException, IOException {
		if(channelId==98||channelId==99||channelId==100||channelId==101
				||channelId==102||channelId==103||channelId==104){
			Channel cc = channelMng.findById(channelId);
			List<Channel> topList = new ArrayList<Channel>();
			topList.add(cc);
			List<Channel> channelList = Channel.getListForSelect(topList, null, true);
			channelId=channelList.get(2).getId();
		}
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if (channelId == null) {
			json.put("success", false);
			json.put("status", 100);
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		if (StringUtils.isBlank(text)) {
			json.put("success", false);
			json.put("status", 101);
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		if (user == null || user.getGroup().getNeedCaptcha()) {
			// 验证码错误
			try {
				if (!imageCaptchaService.validateResponseForID(session
						.getSessionId(request, response), captcha)) {
					json.put("success", false);
					json.put("status", 1);
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
			} catch (CaptchaServiceException e) {
				json.put("success", false);
				json.put("status", 1);
				log.warn("", e);
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
		}
		Channel channel = channelMng.findById(channelId);
		if (channel == null) {
			// 栏目不存在
			json.put("success", false);
			json.put("status", 2);
		} else if (channel.getCommentControl() == ChannelExt.COMMENT_OFF) {
			// 评论关闭
			json.put("success", false);
			json.put("status", 3);
		} else if ((channel.getCommentControl() == ChannelExt.COMMENT_LOGIN|channel.getCommentControl() == ChannelExt.COMMENT_LOGIN_MANY)
				&& user == null) {
			// 需要登录才能评论
			json.put("success", false);
			json.put("status", 4);
		}else if(channel.getCommentControl() == ChannelExt.COMMENT_LOGIN&&user!=null){
			if (hasCommented(user, channel)) {
				// 已经评论过，不能重复评论
				json.put("success", false);
				json.put("status", 5);
			}
		}else {
			boolean checked = false;
			Integer userId = null;
			if (user != null) {
				checked = !user.getGroup().getNeedCheck();
				userId = user.getId();
			}
//			ContentDoc doc=content.getContentDoc();
//			if(doc!=null){
//				doc.setAvgScore(getNewAvgScore(content, score));
//				contentDocMng.update(doc,content);
//			}
			cmsCommentMng.commentChannel(score,text, RequestUtils.getIpAddr(request),
					channelId, site.getId(), userId, true, true, parentId);
			json.put("success", true);
			json.put("status", 0);
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequestMapping(value = "/comment_up.jspx")
	public void up(Integer commentId, HttpServletRequest request,
			HttpServletResponse response) {
		if (exist(commentId)) {
			cmsCommentMng.ups(commentId);
			ResponseUtils.renderJson(response, "true");
		} else {
			ResponseUtils.renderJson(response, "false");
		}
	}

	@RequestMapping(value = "/comment_down.jspx")
	public void down(Integer commentId, HttpServletRequest request,
			HttpServletResponse response) {
		if (exist(commentId)) {
			cmsCommentMng.downs(commentId);
			ResponseUtils.renderJson(response, "true");
		} else {
			ResponseUtils.renderJson(response, "false");
		}
	}
	
	private Float getNewAvgScore(Content content, Integer score) {
		Integer commentCount = content.getCommentsCount();
		Float sumScore = content.getAvgScore() * commentCount;
		sumScore += score;
		return sumScore / (commentCount + 1);
	}

	private boolean hasCommented(CmsUser user, Content content) {
		if (content.hasCommentUser(user)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 检查栏目是否已经评论过
	 * @param user
	 * @param content
	 * @return
	 */
	private boolean hasCommented(CmsUser user, Channel channel) {
		if (channel.hasCommentUser(user)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean exist(Integer id) {
		if (id == null) {
			return false;
		}
		CmsComment comment = cmsCommentMng.findById(id);
		return comment != null;
	}

	@Autowired
	private CmsCommentMng cmsCommentMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private ContentDocMng contentDocMng;
	@Autowired
	private SessionProvider session;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private ChannelMng channelMng;
}
