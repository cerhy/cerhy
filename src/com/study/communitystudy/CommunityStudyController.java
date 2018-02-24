package com.study.communitystudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 社区研修
 */
@Controller
@RequestMapping("/CommunityStudy")    
public class CommunityStudyController {
	@SuppressWarnings("unused")
	
	private static final Logger log = LoggerFactory.getLogger(CommunityStudyController.class);
	
    @RequestMapping("/index")    
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response) {    
    	System.out.println("++++++++++++++++++++++++++++++++++++++");
        return new ModelAndView("WEB-INF/study/community.html");    
    } 
}
