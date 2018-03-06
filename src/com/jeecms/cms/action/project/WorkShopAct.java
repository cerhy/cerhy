package com.jeecms.cms.action.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

/**
 * 工作坊
 */
@Controller
public class WorkShopAct {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(WorkShopAct.class);
	
    @RequestMapping("/ws")    
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response, ModelMap model) { 
    	CmsSite site=CmsUtils.getSite(request);
    	FrontUtils.frontData(request, model, site);
        return new ModelAndView("WEB-INF/t/cms/www/default/index/workshop.html");    
    } 
    
    @RequestMapping(value="/{id}",method=RequestMethod.GET)    
    public ModelAndView user(@PathVariable("id") Integer id,HttpServletRequest request,HttpServletResponse response, ModelMap model) {    
    	CmsSite site=CmsUtils.getSite(request);
    	FrontUtils.frontData(request, model, site);
    	if(id==20000){
    		return new ModelAndView("WEB-INF/t/cms/www/default/index/recomhall.html");    
    	}else{
    		return new ModelAndView("WEB-INF/t/cms/www/default/index/personalworkshop.html");    
    	}
    } 
}
