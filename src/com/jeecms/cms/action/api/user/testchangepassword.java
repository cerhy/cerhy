package com.jeecms.cms.action.api.user;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class testchangepassword {
	@SuppressWarnings("deprecation")
	public static void main(String[]args){
		try {   
            @SuppressWarnings("resource")
			HttpClient httpclient = new DefaultHttpClient();    
            String uri = "http://192.168.191.1:8080/cerhy/api/user/changepassword";   
            HttpPost httppost = new HttpPost(uri);     
            //添加http头信息   
            httppost.addHeader("Authorization", "your token"); //认证token   
            httppost.addHeader("Content-Type", "application/json");   
            httppost.addHeader("User-Agent", "imgfornote");    
            JSONObject obj = new JSONObject();  
            obj.put("id", "9969");   
            obj.put("password", "1234567");   
            httppost.setEntity(new StringEntity(obj.toString()));       
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
