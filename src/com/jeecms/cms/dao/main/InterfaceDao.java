package com.jeecms.cms.dao.main;

import javax.servlet.http.HttpServletRequest;

import com.jeecms.cms.entity.main.InterfaceParam;

public interface InterfaceDao {

	Object register(InterfaceParam param,HttpServletRequest request);

	Object getResponseData(String method,Object status, Object object);

	Object login(InterfaceParam param);

	Object changepassword(InterfaceParam param);

	Object eidtporfileinfo(InterfaceParam param);

	Object resetpassword(InterfaceParam param);

	Object articleinfo(InterfaceParam param);

}
