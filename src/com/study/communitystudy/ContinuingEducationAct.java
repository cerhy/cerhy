package com.study.communitystudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
@Controller
public class ContinuingEducationAct {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(WorkShopAct.class);
	
    @RequestMapping("/tea")    
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response, ModelMap model) { 
        return new ModelAndView("WEB-INF/t/cms/www/default/index/tea.html");    
    } 
}
