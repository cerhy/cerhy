package com.jeecms.cms.action.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/blog")
public class testAct {
	
	 @RequestMapping(method=RequestMethod.GET,value="{id}")
	    public void viewUser(@PathVariable("id")String id){
		 System.out.println("=====");
	  }
}