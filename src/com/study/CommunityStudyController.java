package com.study;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.jeecms.cms.action.project.CommunityAct;

/**
 * 工作坊
 */
@Controller
public class CommunityStudyController {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CommunityStudyController.class);
	
    @RequestMapping("/cte")    
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response) {    
    	System.out.println("++++++++++++++++++++++++++++++++++++++");
        return new ModelAndView("WEB-INF/t/cms/www/default/index/community.html");    
    } 
}
