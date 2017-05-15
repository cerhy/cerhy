package com.jeecms.cms.action.api.user;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class testregister {
	@SuppressWarnings("deprecation")
	public static void main(String[]args){
		try {   
            @SuppressWarnings("resource")
			HttpClient httpclient = new DefaultHttpClient();    
            String uri = "http://221.226.106.154:6680/cerhy/api/user/register";   
            HttpPost httppost = new HttpPost(uri);     
            //添加http头信息   
            httppost.addHeader("Authorization", "your token"); //认证token   
            httppost.addHeader("Content-Type", "application/json;charset=UTF-8");   
            httppost.addHeader("User-Agent", "imgfornote");    
            JSONObject obj = new JSONObject();
            obj.put("areacode", "86");
            obj.put("mobile", "13588444758");   
            obj.put("nickname", "哇的飞");   
            obj.put("password", "123456");   
            obj.put("type", "0"); 
            StringEntity se = new StringEntity(obj.toString(),"UTF-8");   
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));   
            httppost.setEntity(se);  
            HttpResponse response;
            response = httpclient.execute(httppost);    
            //检验状态码，如果成功接收数据    
            int code = response.getStatusLine().getStatusCode();  
            if (code == 200) {    
                String rev = EntityUtils.toString(response.getEntity());//返回json格式： {"id": "","name": ""}           
                obj= JSONObject.fromObject(rev); 
            }   
            } catch (ClientProtocolException e) {   
                e.printStackTrace();  
            } catch (IOException e) {    
                e.printStackTrace();  
            } catch (Exception e) {   
                e.printStackTrace();  
            }   
		
	}
	

}
