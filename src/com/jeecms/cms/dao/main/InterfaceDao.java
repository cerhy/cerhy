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

	Object downloadFile(InterfaceParam param,HttpServletRequest request);

	Object searchinfo(InterfaceParam param);

	Object getResponseDataSearch(Object status, Object object, Integer search);

}
