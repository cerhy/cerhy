package com.jeecms.cms.manager.main;

import javax.servlet.http.HttpServletRequest;

import com.jeecms.cms.entity.main.InterfaceParam;

public interface InterfaceMng {

	Object register(InterfaceParam param,HttpServletRequest request);

	Object getResponseData(String method,Object status, Object object);

	Object login(InterfaceParam param);

	Object changepassword(InterfaceParam param);

	Object eidtporfileinfo(InterfaceParam param);

	Object resetpassword(InterfaceParam param);

	Object articleinfo(InterfaceParam param);

}
