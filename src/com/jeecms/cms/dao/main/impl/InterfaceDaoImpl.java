package com.jeecms.cms.dao.main.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jeecms.cms.Constants;
import com.jeecms.cms.action.blog.CreateSerialNo;
import com.jeecms.cms.dao.main.InterfaceDao;
import com.jeecms.cms.entity.assist.CmsReportDoc;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentAttachment;
import com.jeecms.cms.entity.main.InterfaceParam;
import com.jeecms.cms.lucene.LuceneContentSvc;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.impl.ColumnsMng;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserExt;
import com.jeecms.core.entity.UnifiedUser;
import com.jeecms.core.manager.CmsUserExtMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.manager.UnifiedUserMng;
import com.jeecms.core.web.util.DateUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
@Repository
public class InterfaceDaoImpl  extends HibernateBaseDao<Content, Integer> implements InterfaceDao {
	
	@Override
	public Object register(InterfaceParam param,HttpServletRequest request) {
		String areacode=param.getAreacode();
		String mobile=param.getMobile();
		String password=param.getPassword();
		String nickname=param.getNickname();
		String type=param.getType();
		JSONObject jStatus = new JSONObject();
		JSONObject jData = new JSONObject();
		String ip = RequestUtils.getIpAddr(request);
		try {
			if(StringUtils.isEmpty(areacode)||StringUtils.isEmpty(mobile)
			   ||StringUtils.isEmpty(password)||StringUtils.isEmpty(nickname)
			   ||StringUtils.isEmpty(type)){
				jStatus.put("code", 9999);
				jStatus.put("msg", "参数错误,请检查是否有为空字段!");
				return this.getResponseData("register",jStatus, null);
			}else{
				String no=CreateSerialNo.generateNumber();
				while(unifiedUserMng.usernameExist(no)){ 
					no=CreateSerialNo.generateNumber();
					unifiedUserMng.usernameExist(no);
				}
				CmsUserExt userExt=new CmsUserExt();
				CmsUser user=cmsUserMng.registerInterface(no,areacode,mobile,type,nickname, null, password, ip,null,false,userExt,null, false, null, null);
				jStatus.put("code", 0);
				jStatus.put("msg", "注册成功!");
				JSONObject jUser = new JSONObject();
				jUser.put("id", user.getId());
				jUser.put("username", no);
				jData.put("user", jUser);
				return this.getResponseData("register",jStatus, jData);
			}
		} catch (Exception e) {
			jStatus.put("code", 10000);
			jStatus.put("msg", "服务器内部错误!");
			return this.getResponseData("register",jStatus, null);
		}
	}

	@Override
	public Object login(InterfaceParam param) {
		String username=param.getUsername();
		String password=param.getPassword();
		JSONObject jStatus = new JSONObject();
		JSONObject jData = new JSONObject();
		try {
			if(StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
				jStatus.put("code", 9999);
				jStatus.put("msg", "用户名或密码不能为空!");
				return this.getResponseData("login", jStatus, null);
			}
			if(!unifiedUserMng.usernameExist(username)){
				jStatus.put("code", 1);
				jStatus.put("msg", "该用户不存在!");
				return this.getResponseData("login", jStatus, null);
			}
			//验证密码
			UnifiedUser ufu=unifiedUserMng.getByUsername(username);
			CmsUserExt cue=cmsUserExtMng.findById(ufu.getId());
			CmsUser cu=cmsUserMng.findById(ufu.getId());
			boolean pass = cmsUserMng.isPasswordValid(ufu.getId(), password);
			if(pass){
				jStatus.put("code", 0);
				jStatus.put("msg", "登陆成功!");
				JSONObject jUser = new JSONObject();
				jUser.put("id", ufu.getId());
				jUser.put("username", ufu.getUsername());
				jUser.put("nickname", cu.getNickname()==null?"":cu.getNickname());
				jUser.put("name", cue.getRealname()==null?"":cue.getRealname());
				jUser.put("email", cu.getEmail()==null?"":cu.getEmail());
				jUser.put("areacode", cu.getAreacode()==null?"":cu.getAreacode());
				jUser.put("moblie", cue.getMobile()==null?"":cue.getMobile());
				jUser.put("time",DateUtils.getCurrentDate());
				if(cue.getGender()!=null&&!cue.getGender()){
					jUser.put("gender","0");
				}else if(cue.getGender()!=null&&cue.getGender()){
					jUser.put("gender","1");
				}else{
					jUser.put("gender","");
				}
				jData.put("user", jUser);
				return this.getResponseData("login", jStatus, jData);
			}else{
				jStatus.put("code", 1);
				jStatus.put("msg", "密码错误!");
				return this.getResponseData("login", jStatus, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jStatus.put("code", 10000);
			jStatus.put("msg", "服务器内部错误!");
			return this.getResponseData("login",jStatus, null);
		}
	}
	
	@Override
	public Object changepassword(InterfaceParam param) {
		JSONObject jStatus = new JSONObject();
		try {
			if(StringUtils.isEmpty(param.getId())||StringUtils.isEmpty(param.getOldpassword())||StringUtils.isEmpty(param.getNewpassword())){
				jStatus.put("code", 9999);
				jStatus.put("msg", "参数错误!请检查是否有为空字段!");
				return this.getResponseData("changepassword",jStatus, null);
			}
			CmsUser cu=cmsUserMng.findById(Integer.valueOf(param.getId()));
			try {
				if(cu!=null){
					UnifiedUser ufu=unifiedUserMng.findById(cu.getId());
					boolean pass = cmsUserMng.isPasswordValid(ufu.getId(), param.getOldpassword());
					if(pass){
						cmsUserMng.updatePwdEmail(Integer.valueOf(param.getId()), param.getNewpassword(), cu.getEmail());
						jStatus.put("code", 0);
						jStatus.put("msg", "密码修改成功!");
						return this.getResponseData("changepassword",jStatus, null);
					}else{
						jStatus.put("code", 1);
						jStatus.put("msg", "密码修改失败,旧密码输入错误!");
						return this.getResponseData("changepassword",jStatus, null);
					}
				}else{
					jStatus.put("code", 1);
					jStatus.put("msg", "该用户不存在!");
					return this.getResponseData("changepassword",jStatus, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				jStatus.put("code", 1);
				jStatus.put("msg", "密码修改失败!");
				return this.getResponseData("changepassword",jStatus, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jStatus.put("code", 10000);
			jStatus.put("msg", "服务器内部错误!");
			return this.getResponseData("changepassword",jStatus, null);
		}
	}

	@Override
	public Object resetpassword(InterfaceParam param) {
		JSONObject jStatus = new JSONObject();
		try {
			if(StringUtils.isEmpty(param.getId())||StringUtils.isEmpty(param.getPassword())){
				jStatus.put("code", 9999);
				jStatus.put("msg", "参数错误!请检查是否有为空字段!");
				return this.getResponseData("resetpassword",jStatus, null);
			}
			CmsUser cu=cmsUserMng.findById(Integer.valueOf(param.getId()));
			try {
				if(cu!=null){
					cmsUserMng.updatePwdEmail(Integer.valueOf(param.getId()), param.getPassword(), cu.getEmail());
					jStatus.put("code", 0);
					jStatus.put("msg", "密码重置成功!");
					return this.getResponseData("resetpassword",jStatus, null);
				}else{
					jStatus.put("code", 1);
					jStatus.put("msg", "该用户不存在!");
					return this.getResponseData("resetpassword",jStatus, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				jStatus.put("code", 1);
				jStatus.put("msg", "密码重置失败!");
				return this.getResponseData("resetpassword",jStatus, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jStatus.put("code", 10000);
			jStatus.put("msg", "服务器内部错误!");
			return this.getResponseData("changepassword",jStatus, null);
		}
	}
	
	
	
	
	@Override
	public Object eidtporfileinfo(InterfaceParam param) {
		JSONObject jStatus = new JSONObject();
		try {
			if(StringUtils.isEmpty(param.getId())){
				jStatus.put("code", 9999);
				jStatus.put("msg", "参数错误,用户id不能为空!");
				return this.getResponseData("eidtporfile",jStatus, null);
			}
			CmsUser user=cmsUserMng.findById(Integer.valueOf(param.getId()));
			CmsUserExt ext=new CmsUserExt();
			ext.setId(user.getId());
			ext.setMobile(param.getMobile()==null?null:param.getMobile());
			if(param.getGender()!=null&&param.getGender().equals("0")){
				ext.setGender(false);
			}else if(param.getGender()!=null&&param.getGender().equals("1")){
				ext.setGender(true);
			}
			ext.setRealname(param.getName()==null?null:param.getName());
			if(StringUtils.isNotEmpty(param.getBrithdate())){
				ext.setBirthday(DateUtils.parseDate(param.getBrithdate(), "yyyy-MM-dd"));
			}
			user.setEmail(param.getEmail()==null?null:param.getEmail());
			user.setNickname(param.getNickname()==null?null:param.getNickname());
			try {
				cmsUserMng.updatePwdEmail(user.getId(),null, param.getEmail());
				cmsUserExtMng.update(ext, user);
				jStatus.put("code", 0);
				jStatus.put("msg", "更新成功!");
				return this.getResponseData("eidtporfile",jStatus, null);
			} catch (Exception e) {
				e.printStackTrace();
				jStatus.put("code", 1);
				jStatus.put("msg", "更新失败!");
				return this.getResponseData("eidtporfile",jStatus, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jStatus.put("code", 10000);
			jStatus.put("msg", "服务器内部错误!");
			return this.getResponseData("eidtporfile",jStatus, null);
		}
	}
	

	@Override
	public Object articleinfo(InterfaceParam param) {
		JSONObject jStatus = new JSONObject();
		JSONObject jData = new JSONObject();
		JSONObject jb = null;
		JSONObject ja = null;
		JSONArray listJ= null;
		JSONArray listA= null;
		if(StringUtils.isEmpty(param.getCategory())||StringUtils.isEmpty(param.getContenttype())||StringUtils.isEmpty(param.getCount())){
			jStatus.put("code", 9999);
			jStatus.put("msg", "参数错误,检查是否有为空字段!");
			return this.getResponseData("articl",jStatus, null);
		}
		if((param.getContenttype().equals("2")&&param.getContentid()==null)
			||(param.getContenttype().equals("3")&&param.getContentid()==null)){
			jStatus.put("code", 9999);
			jStatus.put("msg", "参数错误,该情况文章ID为必传字段!");
			return this.getResponseData("articl",jStatus, null);
		}
		try {
			List<Content> list = getList(param, null);
			if(list!=null&&list.size()>0){
				listJ=new JSONArray();
				jb = new JSONObject();
				int size=list.size();
				for(int i=0;i<size;i++){
					int contentid=list.get(i).getId();
					int userid=list.get(i).getUser().getId();
					String author=list.get(i).getContentExt().getAuthor()==null?"":list.get(i).getContentExt().getAuthor();
					int category=list.get(i).getChannel().getId();
					String title=list.get(i).getContentExt().getTitle();
					String description=list.get(i).getContentExt().getDescription()==null?"":list.get(i).getContentExt().getDescription();
					String content=list.get(i).getContentTxt().getTxt()==null?"":list.get(i).getContentTxt().getTxt();
					String url="http://www.cerhy.com/"+list.get(i).getChannel().getPath()+"/"+list.get(i).getId()+".jhtml";
					String time=DateUtils.date2Str(list.get(i).getSortDate(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
					String videourl=list.get(i).getContentExt().getMediaPath();
					String cu=content;
					if(videourl!=null){
						cu=content+"<div>"
								+"<script src='http://www.cerhy.com/r/cms/ckplayer/ckplayer.js' type='text/javascript'></script>"
								+"<script src='http://www.cerhy.com/r/cms/ckplayer/offlights.js' type='text/javascript'></script>"
					            +"<div id='video' style='position:relative;z-index: 100;width:100%;height:500px;float: left;'><div id='a1'></div></div>"
					            +"<script type='text/javascript'>"
					            +"var flashvars={f:'"+videourl+"',c:0,b:1,my_url:'"+url+"',my_pic:''};"
								+"var params={bgcolor:'#FFF',allowFullScreen:true,allowScriptAccess:'always'};"
								+"CKobject.embedSWF('http://www.cerhy.com/r/cms/ckplayer/ckplayer.swf','a1','ckplayer_a1','100%','500',flashvars,params);"
								+"var box = new LightBox();"
								+"function closelights(){"
								+"	box.Show();"
								+"}"
								+"function openlights(){"
								+"	box.Close();"
					            +"}"
								+"</script>"
								+"</div>";
					}
					jb.put("id", contentid);
					jb.put("pubid", userid);
					jb.put("author", author);
					jb.put("category", category);
					jb.put("title", title);
					jb.put("abstract", description);
					jb.put("content", cu);
					jb.put("url", url);
					jb.put("time", time);
					jb.put("videourl", videourl);
					List<ContentAttachment> attachments=list.get(i).getAttachments();
					if(attachments!=null&&attachments.size()>0){
						listA=new JSONArray();
						ja = new JSONObject();
						int sizeA=attachments.size();
						for(int j=0;j<sizeA;j++){
							ja.put("name", attachments.get(j).getName());
							ja.put("url", "http://www.cerhy.com"+attachments.get(j).getPath());
							listA.add(ja);
						}
						jb.put("attachments", listA);
					}else{
						jb.put("attachments", null);
					}
					listJ.add(jb);
				}
				jData.put("articles", listJ);
				jStatus.put("code", 0);
				jStatus.put("msg", "获取成功!");
				return this.getResponseData("articl",jStatus, jData);
			}else{
				jStatus.put("code", 0);
				jStatus.put("msg", "无内容!");
				return this.getResponseData("articl",jStatus, null);
			}
		} catch (TemplateException e) {
			e.printStackTrace();
			jStatus.put("code", 10000);
			jStatus.put("msg", "服务器内部错误!");
			return this.getResponseData("articl",jStatus, null);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<Content> getList(InterfaceParam params,
			Environment env) throws TemplateException {
		List<Content> list=(List<Content>)getData(params, env);
		return list;
	}

	protected Object getData(InterfaceParam params, Environment env)throws TemplateException {
		Integer count; 
		if(params.getCount()!=null){
		    count = Integer.valueOf(params.getCount());
		}else{
			count=null;
		}
		Integer channelId;
		if(params.getCategory()!=null){
			channelId=Integer.valueOf(params.getCategory());
		}else{
			channelId=null;
		}
		Integer contentid;
		if(params.getContentid()!=null){
			contentid=Integer.valueOf(params.getContentid());
		}else{
			contentid=null;
		}
		Integer userid;
		if(params.getUserid()!=null){
			userid=Integer.valueOf(params.getUserid());
		}else{
			userid=null;
		}
		Integer type;
		if(params.getContenttype()!=null){
			type=Integer.valueOf(params.getContenttype());
		}else{
			type=null;
		}
		if (channelId != null) {
			int option = 1;
			return getListByChannelIds(channelId,0,option,count,contentid,userid,type);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected List<Content> getListByChannelIds(Integer channelIds,Integer orderBy, Integer option
			,Integer count,Integer contentid,Integer userid,Integer type) {
		Finder f = byChannelIds(channelIds,orderBy, option,contentid,userid,type);
		if (count != null) {
			if(type==1){
				f.setMaxResults(10);
			}else{
				f.setMaxResults(count);
			}
		}
		f.setCacheable(true);
		return find(f);
	}
	
	
	private Finder byChannelIds(Integer channelIds,Integer orderBy,Integer option
			,Integer contentid,Integer userid,Integer type) {
		Finder f = Finder.create();
		// 如果多个栏目
		 if (option == 1) {
			if(channelIds!=null){
				//高中、初中、小学美术
				if(channelIds.toString().equals("316")||channelIds.toString().equals("323")){
					channelIds=305;
				}
				//高中、初中、小学音乐
				if(channelIds.toString().equals("315")||channelIds.toString().equals("322")){
					channelIds=304;
				}
				//高中、初中、小学体育
				if(channelIds.toString().equals("314")||channelIds.toString().equals("321")){
					channelIds=303;
				}
				//高中、初中、小学综合实践
				if(channelIds.toString().equals("417")||channelIds.toString().equals("418")){
					channelIds=416;
				}
				//高中、初中地理
				if(channelIds.toString().equals("311")){
					channelIds=300;
				}
				//高中、初中历史
				if(channelIds.toString().equals("310")){
					channelIds=299;
				}
				//初中、小学信息技术
				if(channelIds.toString().equals("319")){
					channelIds=312;
				}
				//初中、小学政治
				if(channelIds.toString().equals("317")){
					channelIds=309;
				}
				//高中、初中化学
				if(channelIds.toString().equals("307")){
					channelIds=296;
				}
				//高中、初中物理
				if(channelIds.toString().equals("306")){
					channelIds=295;
				}
				//高中、初中生物
				if(channelIds.toString().equals("308")){
					channelIds=297;
				}
				//高中、初中语文
				if(channelIds.toString().equals("142")){
					channelIds=140;
				}
				
			}
			f.append("select  bean from Content bean");
			f.append(" join bean.channel node,Channel parent");
			f.append(" where node.lft between parent.lft and parent.rgt");
			f.append(" and bean.site.id=parent.site.id");
			f.append(" and parent.id=:channelId");
			f.setParam("channelId", channelIds);
			if(type==1){
				if(userid!=null){
					f.append(" and bean.user.id="+userid);
				}
				f.append(" order by bean.id desc");
			}else if(type==2){
				if(userid!=null){
					f.append(" and bean.user.id="+userid);
				}
				f.append(" and bean.id>"+contentid);
				f.append(" order by bean.id desc");
			}else if(type==3){
				if(userid!=null){
					f.append(" and bean.user.id="+userid);
				}
				f.append(" and bean.id<"+contentid);
				f.append(" order by bean.id desc");
			}
		}else {
			throw new RuntimeException("option value must be 0 or 1 or 2.");
		}
		return f;
	}
	
	
	
	@Override
	public Object searchinfo(InterfaceParam param) {
		JSONObject jStatus = new JSONObject();
		JSONObject jData = new JSONObject();
		JSONObject jb = null;
		JSONObject ja = null;
		JSONArray listJ= null;
		JSONArray listA= null;
		if(StringUtils.isEmpty(param.getKeyword())||StringUtils.isEmpty(param.getPage())
				||StringUtils.isEmpty(param.getCount())){
			jStatus.put("code", 9999);
			jStatus.put("msg", "参数错误,请检查是否有为空字段!");
			return this.getResponseData("search",jStatus, null);
		}
		String query=param.getKeyword();
		Integer pageNo=Integer.valueOf(param.getPage());
		Integer count=Integer.valueOf(param.getCount());
		String userId=param.getUserid();
		Pagination page;
		String path = realPathResolver.get(Constants.LUCENE_PATH);
		try {
			page = luceneContentSvc.searchPage(path, query,null,null, null, null,
					null, null, pageNo, count);
			@SuppressWarnings("unchecked")
			List<Content> list=(List<Content>) page.getList();
			int num=0;
			if(list!=null&&list.size()>0){
				listJ=new JSONArray();
				jb = new JSONObject();
				int size=list.size();
				for(int i=0;i<size;i++){
					int userid=list.get(i).getUser().getId();
					if(!StringUtils.isEmpty(param.getUserid())){
						if(Integer.valueOf(userId).equals(userid)){
							num=num+1;
							int contentid=list.get(i).getId();
							String author=list.get(i).getContentExt().getAuthor()==null?"":list.get(i).getContentExt().getAuthor();
							int category=list.get(i).getChannel().getId();
							String title=list.get(i).getContentExt().getTitle();
							String description=list.get(i).getContentExt().getDescription()==null?"":list.get(i).getContentExt().getDescription();
							String content=list.get(i).getContentTxt().getTxt()==null?"":list.get(i).getContentTxt().getTxt();
							String url="http://www.cerhy.com/"+list.get(i).getChannel().getPath()+"/"+list.get(i).getId()+".jhtml";
							String time=DateUtils.date2Str(list.get(i).getSortDate(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
							String videourl=list.get(i).getContentExt().getMediaPath();
							String cu=content;
							if(videourl!=null){
								cu=content+"<div>"
										+"<script src='http://www.cerhy.com/r/cms/ckplayer/ckplayer.js' type='text/javascript'></script>"
										+"<script src='http://www.cerhy.com/r/cms/ckplayer/offlights.js' type='text/javascript'></script>"
										+"<div id='video' style='position:relative;z-index: 100;width:100%;height:500px;float: left;'><div id='a1'></div></div>"
										+"<script type='text/javascript'>"
										+"var flashvars={f:'"+videourl+"',c:0,b:1,my_url:'"+url+"',my_pic:''};"
										+"var params={bgcolor:'#FFF',allowFullScreen:true,allowScriptAccess:'always'};"
										+"CKobject.embedSWF('http://www.cerhy.com/r/cms/ckplayer/ckplayer.swf','a1','ckplayer_a1','100%','500',flashvars,params);"
										+"var box = new LightBox();"
										+"function closelights(){"
										+"	box.Show();"
										+"}"
										+"function openlights(){"
										+"	box.Close();"
										+"}"
										+"</script>"
										+"</div>";
							}
							jb.put("id", contentid);
							jb.put("pubid", userid);
							jb.put("author", author);
							jb.put("category", category);
							jb.put("title", title);
							jb.put("abstract", description);
							jb.put("content", cu);
							jb.put("url", url);
							jb.put("time", time);
							jb.put("videourl", videourl);
							List<ContentAttachment> attachments=list.get(i).getAttachments();
							if(attachments!=null&&attachments.size()>0){
								listA=new JSONArray();
								ja = new JSONObject();
								int sizeA=attachments.size();
								for(int j=0;j<sizeA;j++){
									ja.put("name", attachments.get(j).getName());
									ja.put("url", "http://www.cerhy.com"+attachments.get(j).getPath());
									listA.add(ja);
								}
								jb.put("attachments", listA);
							}else{
								jb.put("attachments", null);
							}
							listJ.add(jb);
						}
					}else{
						num=page.getTotalCount();
						int contentid=list.get(i).getId();
						String author=list.get(i).getContentExt().getAuthor()==null?"":list.get(i).getContentExt().getAuthor();
						int category=list.get(i).getChannel().getId();
						String title=list.get(i).getContentExt().getTitle();
						String description=list.get(i).getContentExt().getDescription()==null?"":list.get(i).getContentExt().getDescription();
						String content=list.get(i).getContentTxt().getTxt()==null?"":list.get(i).getContentTxt().getTxt();
						String url="http://www.cerhy.com/"+list.get(i).getChannel().getPath()+"/"+list.get(i).getId()+".jhtml";
						String time=DateUtils.date2Str(list.get(i).getSortDate(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
						String videourl=list.get(i).getContentExt().getMediaPath();
						String cu=content;
						if(videourl!=null){
							cu=content+"<div>"
									+"<script src='http://www.cerhy.com/r/cms/ckplayer/ckplayer.js' type='text/javascript'></script>"
									+"<script src='http://www.cerhy.com/r/cms/ckplayer/offlights.js' type='text/javascript'></script>"
									+"<div id='video' style='position:relative;z-index: 100;width:100%;height:500px;float: left;'><div id='a1'></div></div>"
									+"<script type='text/javascript'>"
									+"var flashvars={f:'"+videourl+"',c:0,b:1,my_url:'"+url+"',my_pic:''};"
									+"var params={bgcolor:'#FFF',allowFullScreen:true,allowScriptAccess:'always'};"
									+"CKobject.embedSWF('http://www.cerhy.com/r/cms/ckplayer/ckplayer.swf','a1','ckplayer_a1','100%','500',flashvars,params);"
									+"var box = new LightBox();"
									+"function closelights(){"
									+"	box.Show();"
									+"}"
									+"function openlights(){"
									+"	box.Close();"
									+"}"
									+"</script>"
									+"</div>";
						}
						jb.put("id", contentid);
						jb.put("pubid", userid);
						jb.put("author", author);
						jb.put("category", category);
						jb.put("title", title);
						jb.put("abstract", description);
						jb.put("content", cu);
						jb.put("url", url);
						jb.put("time", time);
						jb.put("videourl", videourl);
						List<ContentAttachment> attachments=list.get(i).getAttachments();
						if(attachments!=null&&attachments.size()>0){
							listA=new JSONArray();
							ja = new JSONObject();
							int sizeA=attachments.size();
							for(int j=0;j<sizeA;j++){
								ja.put("name", attachments.get(j).getName());
								ja.put("url", "http://www.cerhy.com"+attachments.get(j).getPath());
								listA.add(ja);
							}
							jb.put("attachments", listA);
						}else{
							jb.put("attachments", null);
						}
						listJ.add(jb);
					}
				}
				jData.put("total", num);
				if(listJ.size()>0){
					jData.put("articles", listJ);
				}else{
					jData.put("articles", null);
				}
				jStatus.put("code", 0);
				jStatus.put("msg", "获取成功!");
				return this.getResponseData("search",jStatus, jData);
			}else{
				jData.put("total", num);
				jData.put("articles", null);
				jStatus.put("code", 0);
				jStatus.put("msg", "无内容!");
				return this.getResponseData("search",jStatus, jData);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.apache.lucene.queryParser.ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/* 
	 * @param urlPath
	 *            下载路径
	 * @param downloadDir
	 *            下载存放目录
	 * @return 返回下载文件
	 */
	@Override
	public Object downloadFile(InterfaceParam param,HttpServletRequest request) {
		JSONObject jStatus = new JSONObject();
		File file = null;
		String urlPath=param.getReporturl();
		try {
			// 统一资源
			URL url = new URL(urlPath);
			// 连接类的父类，抽象类
			URLConnection urlConnection = url.openConnection();
			// http的连接类
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
			// 设定请求的方法，默认是GET
			httpURLConnection.setRequestMethod("POST");
			// 设置字符编码
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			// 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
			httpURLConnection.connect();
			// 文件大小
			int fileLength = httpURLConnection.getContentLength();
			// 文件名
			String filePathUrl = httpURLConnection.getURL().getFile();
			String fileFullName = param.getReportname()+filePathUrl.substring(filePathUrl.lastIndexOf("."));
			
			//System.out.println("file length---->" + fileLength);
			
			//URLConnection con = url.openConnection();
			
			BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
			
			String path = request.getSession().getServletContext().getRealPath("/")+"u/cms/www/"+DateUtils.getDataString(new SimpleDateFormat("yyyyMM"))+File.separatorChar + fileFullName;
			file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			OutputStream out = new FileOutputStream(file);
			int size = 0;
			int len = 0;
			byte[] buf = new byte[1024];
			while ((size = bin.read(buf)) != -1) {
				len += size;
				out.write(buf, 0, size);
				// 打印下载百分比
				//System.out.println("下载了-------> " + len * 100 / fileLength +"%\n");
			}
			CmsReportDoc crd=new CmsReportDoc();
			crd.setReportname(param.getReportname());
			crd.setReporturl(request.getContextPath()+"/u/cms/www/"+DateUtils.getDataString(new SimpleDateFormat("yyyyMM"))+File.separatorChar + fileFullName);
			try {
				crd.setReporttime(DateUtils.parseDate(param.getReporttime(), "yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int i=columnsMng.saveReportDoc(crd);
			if(i<1){
				jStatus.put("code", 10000);
				jStatus.put("msg", "服务器内部错误!");
			}else{
				jStatus.put("code", 0);
				jStatus.put("msg", "成功!");
			}
			bin.close();
			out.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			jStatus.put("code", 10000);
			jStatus.put("msg", "服务器内部错误!");
			return this.getResponseData("downloadFile",jStatus, null);
		} catch (IOException e) {
			e.printStackTrace();
			jStatus.put("code", 10000);
			jStatus.put("msg", "服务器内部错误!");
			return this.getResponseData("downloadFile",jStatus, null);
		} 
		return this.getResponseData("downloadFile",jStatus, null);
	}
	
	
	
	@Override
	public Object getResponseData(String method,Object status, Object data) {
		JSONObject obj = new JSONObject();
		JSONObject objstatus = new JSONObject();
		if(method.equals("empty")){
			objstatus.put("code", 1);
			objstatus.put("msg", "未获取到数据!");
			obj.put("status", objstatus);
		}else{
			obj.put("status", status);
			obj.put("data", data);
		}
		return obj.toString();
	}
	
	@Override
	public Object getResponseDataSearch(Object status,Object data, Integer search) {
		JSONObject obj = new JSONObject();
		obj.put("total", search);
		obj.put("data", data);
		obj.put("status", status);
		return obj.toString();
	}

	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private CmsUserExtMng cmsUserExtMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	protected ContentMng contentMng;
	@Autowired
	protected ColumnsMng columnsMng;
	@Autowired
	private LuceneContentSvc luceneContentSvc;
	@Autowired
	private RealPathResolver realPathResolver;
	@Override
	protected Class<Content> getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	
}
