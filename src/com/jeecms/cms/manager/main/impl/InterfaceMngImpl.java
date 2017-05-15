package com.jeecms.cms.manager.main.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.cms.dao.main.InterfaceDao;
import com.jeecms.cms.entity.main.InterfaceParam;
import com.jeecms.cms.manager.main.InterfaceMng;

@Service
@Transactional
public class InterfaceMngImpl implements InterfaceMng {
	
	@Autowired
	private InterfaceDao interfaceDao;

	public Object register(InterfaceParam param,HttpServletRequest request) {
		return interfaceDao.register(param,request);
	}

	public Object getResponseData(String method,Object status, Object object) {
		return interfaceDao.getResponseData(method,status,object);
	}

	public Object login(InterfaceParam param) {
		return interfaceDao.login(param);
	}

	public Object changepassword(InterfaceParam param) {
		return interfaceDao.changepassword(param);
	}

	public Object eidtporfileinfo(InterfaceParam param) {
		return interfaceDao.eidtporfileinfo(param);
	}

	public Object resetpassword(InterfaceParam param) {
		return interfaceDao.resetpassword(param);
	}

	public Object articleinfo(InterfaceParam param) {
		return interfaceDao.articleinfo(param);
	}
	
	
	

}
