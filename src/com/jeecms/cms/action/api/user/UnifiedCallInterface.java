package com.jeecms.cms.action.api.user;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeecms.cms.entity.main.InterfaceParam;
import com.jeecms.cms.manager.main.InterfaceMng;
import com.jeecms.core.web.util.DateUtils;

/**
 * 统一调用接口
 * 
 */
@Controller
public class UnifiedCallInterface {
	private static final Logger logger = LoggerFactory.getLogger(UnifiedCallInterface.class);
	@RequestMapping(value="/api/user/register",produces={"application/json;charset=UTF-8"})
	public @ResponseBody Object getRegisterInfo(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		logger.info("register interface call start,startDate is ："+ DateUtils.getCurrentDate());
		InterfaceParam param = new InterfaceParam();
		JSONObject obj=null;
		Object jsonStr = null;
        try {  
        	InputStream is = request.getInputStream();
        	String json = NetUtils.readString(is);
        	if(StringUtils.isNotEmpty(json)){
				obj= JSONObject.fromObject(json);
			}
        }catch (Exception e) {  
        	e.printStackTrace(); 
        	logger.info("register interface Exception is ：",e);
        } 
        if(obj!=null){
        	if(obj.get("areacode")!=null&&obj.get("areacode")!=""){
        		param.setAreacode(obj.get("areacode").toString());
        	}
        	if(obj.get("mobile")!=null&&obj.get("mobile")!=""){
        		param.setMobile(obj.get("mobile").toString());
        	}
        	if(obj.get("password")!=null&&obj.get("password")!=""){
        		param.setPassword(obj.get("password").toString());
        	}
        	if(obj.get("nickname")!=null&&obj.get("nickname")!=""){
        		param.setNickname(obj.get("nickname").toString());
        	}
        	if(obj.get("type")!=null&&obj.get("type")!=""){
        		param.setType(obj.get("type").toString());
        	}
        	jsonStr = handle(param,"register",request, response);
        }else{
        	jsonStr = handleMessage(request, response);
        }
		// 通过流写给客户端
		jsonWrite(jsonStr, response);
		System.out.println("jsonStr="+jsonStr);
		logger.info("register interface return client json data is " + jsonStr);
		logger.info("register interface call end ，结束时间   ： " + DateUtils.getCurrentDate());
		return null;
	}
	
	
	@RequestMapping(value="/api/user/login")
	public Object getloginInfo(HttpServletRequest request,HttpServletResponse response) {
		logger.info("login interface call start,startDate is ："+ DateUtils.getCurrentDate());
		InterfaceParam param = new InterfaceParam();
		JSONObject obj=null;
		Object jsonStr = null;
        try {  
        	InputStream is = request.getInputStream();
        	String json = NetUtils.readString(is);
        	if(StringUtils.isNotEmpty(json)){
				obj= JSONObject.fromObject(json);
			}
        }catch (Exception e) {
        	e.printStackTrace(); 
        	logger.info("login interface Exception is ：",e);  
        }
        if(obj!=null){
        	if(obj.get("username")!=null&&obj.get("username")!=""){
        		param.setUsername(obj.get("username").toString());
        	}
        	if(obj.get("password")!=null&&obj.get("password")!=""){
        		param.setPassword(obj.get("password").toString());
        	}
        	jsonStr = handle(param,"login",request, response);
        }else{
        	jsonStr = handleMessage(request, response);
        	
        }
		// 通过流写给客户端
		jsonWrite(jsonStr, response);
		System.out.println("jsonStr="+jsonStr);
		logger.info("login interface return client json data is " + jsonStr);
		logger.info("login interface call end ，结束时间   ： " + DateUtils.getCurrentDate());
		return null;
	}
	
	@RequestMapping(value="/api/user/changepassword")
	public Object changePassword(HttpServletRequest request,HttpServletResponse response) {
		logger.info("changepassword interface call start,startDate is ："+ DateUtils.getCurrentDate());
		InterfaceParam param = new InterfaceParam();
		JSONObject obj=null;
		Object jsonStr = null;
        try {  
        	InputStream is = request.getInputStream();
        	String json = NetUtils.readString(is);
        	if(StringUtils.isNotEmpty(json)){
				obj= JSONObject.fromObject(json);
			}
        }catch (Exception ex) { 
        	ex.printStackTrace();  
        	logger.info("changepassword interface Exception is ：",ex);
        }
        if(obj!=null){
        	if(obj.get("id")!=null&&obj.get("id")!=""){
        		param.setId(obj.get("id").toString());
        	}
        	if(obj.get("oldpassword")!=null&&obj.get("oldpassword")!=""){
        		param.setOldpassword(obj.get("oldpassword").toString());
        	}
        	if(obj.get("newpassword")!=null&&obj.get("newpassword")!=""){
        		param.setNewpassword(obj.get("newpassword").toString());
        	}
        	jsonStr = handle(param,"changepassword",request, response);
        }else{
        	jsonStr = handleMessage(request, response);
        }
		// 通过流写给客户端
		jsonWrite(jsonStr, response);
		System.out.println("jsonStr="+jsonStr);
		logger.info("changepassword interface return client json data is " + jsonStr);
		logger.info("changepassword interface call end ，结束时间   ： " + DateUtils.getCurrentDate());
		return null;
	}
	
	@RequestMapping(value="/api/user/resetpassword")
	public Object resetpassword(HttpServletRequest request,HttpServletResponse response) {
		logger.info("resetpassword interface call start,startDate is ："+ DateUtils.getCurrentDate());
		InterfaceParam param = new InterfaceParam();
		JSONObject obj=null;
		Object jsonStr = null;
		try {  
			InputStream is = request.getInputStream();
			String json = NetUtils.readString(is);
			if(StringUtils.isNotEmpty(json)){
				obj= JSONObject.fromObject(json);
			}
		}catch (Exception ex) { 
			ex.printStackTrace();  
			logger.info("resetpassword interface Exception is ：",ex);
		}
		if(obj!=null){
			if(obj.get("id")!=null&&obj.get("id")!=""){
				param.setId(obj.get("id").toString());
			}
			if(obj.get("password")!=null&&obj.get("password")!=""){
				param.setPassword(obj.get("password").toString());
			}
			jsonStr = handle(param,"resetpassword",request, response);
		}else{
			jsonStr = handleMessage(request, response);
		}
		// 通过流写给客户端
		jsonWrite(jsonStr, response);
		System.out.println("jsonStr="+jsonStr);
		logger.info("resetpassword interface return client json data is " + jsonStr);
		logger.info("resetpassword interface call end ，结束时间   ： " + DateUtils.getCurrentDate());
		return null;
	}
	
	@RequestMapping(value="/api/user/editprofile")
	public Object editprofile(HttpServletRequest request,HttpServletResponse response) {
		logger.info("editprofile interface call start,startDate is ："+ DateUtils.getCurrentDate());
		InterfaceParam param = new InterfaceParam();
		JSONObject obj=null;
		Object jsonStr = null;
        try {  
        	InputStream is = request.getInputStream();
        	String json = NetUtils.readString(is);
        	if(StringUtils.isNotEmpty(json)){
				obj= JSONObject.fromObject(json);
			}
        }catch (Exception e) {  
        	e.printStackTrace();
        	logger.info("editprofile interface Exception is ：",e); 
        } 
        if(obj!=null){
        	if(obj.get("id")!=null&&obj.get("id")!=""){
        		param.setId(obj.get("id").toString());
        	}
        	if(obj.get("areacode")!=null&&obj.get("areacode")!=""){
        		param.setAreacode(obj.get("areacode").toString());
        	}
        	if(obj.get("mobile")!=null&&obj.get("mobile")!=""){
        		param.setMobile(obj.get("mobile").toString());
        	}
        	if(obj.get("ename")!=null&&obj.get("ename")!=""){
        		param.setName(obj.get("name").toString());
        	}
        	if(obj.get("nickename")!=null&&obj.get("nickename")!=""){
        		param.setNickname(obj.get("nickename").toString());
        	}
        	if(obj.get("email")!=null&&obj.get("email")!=""){
        		param.setEmail(obj.get("email").toString());
        	}
        	if(obj.get("gender")!=null&&obj.get("gender")!=""){
        		param.setGender(obj.get("gender").toString());
        	}
        	if(obj.get("birthdate")!=null&&obj.get("birthdate")!=""){
        		param.setBrithdate(obj.get("birthdate").toString());
        	}
        	jsonStr = handle(param,"editprofile",request, response);
        }else{
        	jsonStr = handleMessage(request, response);
        }
		// 通过流写给客户端
		jsonWrite(jsonStr, response);
		System.out.println("jsonStr="+jsonStr);
		logger.info("editprofile interface return client json data is " + jsonStr);
		logger.info("editprofile interface call end ，结束时间   ： " + DateUtils.getCurrentDate());
		return null;
	}
	
	@RequestMapping(value="/api/article")
	public Object getArticleInfo(HttpServletRequest request,HttpServletResponse response) {
		logger.info("article interface call start,startDate is ："+ DateUtils.getCurrentDate());
		InterfaceParam param = new InterfaceParam();
		JSONObject obj=null;
		Object jsonStr = null;
		try {  
			InputStream is = request.getInputStream();
			String json = NetUtils.readString(is);
			if(StringUtils.isNotEmpty(json)){
				obj= JSONObject.fromObject(json);
			}
		}catch (Exception ex) { 
			ex.printStackTrace();  
			logger.info("article interface Exception is ：",ex);
		}
		if(obj!=null){
			if(obj.get("category")!=null&&StringUtils.isNotEmpty(obj.get("category").toString())){
				param.setCategory(obj.get("category").toString());
			}
			if(obj.get("id")!=null&&StringUtils.isNotEmpty(obj.get("id").toString())){
				param.setContentid(obj.get("id").toString());
			}
			if(obj.get("type")!=null&&StringUtils.isNotEmpty(obj.get("type").toString())){
				param.setContenttype(obj.get("type").toString());
			}
			if(obj.get("count")!=null&&StringUtils.isNotEmpty(obj.get("count").toString())){
				param.setCount(obj.get("count").toString());
			}
			if(obj.get("userid")!=null&&StringUtils.isNotEmpty(obj.get("userid").toString())){
				param.setUserid(obj.get("userid").toString());
			}
			jsonStr = handle(param,"article",request, response);
		}else{
			jsonStr = handleMessage(request, response);
		}
		// 通过流写给客户端
		jsonWrite(jsonStr, response);
		System.out.println("jsonStr="+jsonStr);
		logger.info("article interface return client json data is " + jsonStr);
		logger.info("article interface call end ，结束时间   ： " + DateUtils.getCurrentDate());
		return null;
	}

	
	

	private Object handle(InterfaceParam param,String method,HttpServletRequest request,HttpServletResponse response) {
		if ("register".equalsIgnoreCase(method)) {
			logger.info("注册开始时间：" + new Date());
			Object register = null;
			register = interfaceMng.register(param,request);
			logger.info("注册结束标识:" + register);
			return register;
		}
		if("login".equalsIgnoreCase(method)){
			logger.info("登陆开始时间：" + new Date());
			Object login = null;
			login = interfaceMng.login(param);
			logger.info("登陆结束标识:" + login);
			return login;
		}
		if("changepassword".equalsIgnoreCase(method)){
			logger.info("修改密码开始时间：" + new Date());
			Object changepassword = null;
			changepassword = interfaceMng.changepassword(param);
			logger.info("修改密码结束标识:" + changepassword);
			return changepassword;
		}
		if("resetpassword".equalsIgnoreCase(method)){
			logger.info("重置密码开始时间：" + new Date());
			Object resetpassword = null;
			resetpassword = interfaceMng.resetpassword(param);
			logger.info("重置密码结束标识:" + resetpassword);
			return resetpassword;
		}
		if("editprofile".equalsIgnoreCase(method)){
			logger.info("编辑信息开始时间：" + new Date());
			Object editprofile = null;
			editprofile = interfaceMng.eidtporfileinfo(param);
			logger.info("编辑信息结束标识:" + editprofile);
			return editprofile;
		}
		if("article".equalsIgnoreCase(method)){
			logger.info("获取文章信息开始时间：" + new Date());
			Object article = null;
			article = interfaceMng.articleinfo(param);
			logger.info("获取文章信息结束标识:" + article);
			return article;
		}
		return null;
	}
	
	
	private Object handleMessage(HttpServletRequest request,HttpServletResponse response) {
		return interfaceMng.getResponseData("empty",null, null);
	}
	
	
	
	public void jsonWrite(Object obj, HttpServletResponse response) {
		response.setContentType("application/x-json;charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			out.print(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    @Autowired
	private InterfaceMng interfaceMng;
    
    public static class NetUtils {
    	public static byte[] readBytes(InputStream is){
    		try {
    			byte[] buffer = new byte[1024];
    			int len = -1 ;
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			while((len = is.read(buffer)) != -1){
    				baos.write(buffer, 0, len);
    			}
    			baos.close();
    			return baos.toByteArray();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		return null ;
    	}
    	public static String readString(InputStream is){
    		return new String(readBytes(is));
    	}
    }
    
    
    
}

