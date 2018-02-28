package com.study.communitystudy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

/**
 * 社区研修
 */
@Controller
public class CommunityStudyController {
	@SuppressWarnings("unused")
	
	private static final Logger log = LoggerFactory.getLogger(CommunityStudyController.class);
	
    @RequestMapping("/CommunityStudy/index")    
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response) {    
    	//社区研修主页
        return new ModelAndView("WEB-INF/t/cms/www/default/study/community_study/index.html");    
    } 
    
    @RequestMapping(value="/CommunityStudy/indexData")    
    @ResponseBody
    public String indexDate(HttpServletRequest request,HttpServletResponse response) throws ParserConfigurationException, SAXException, IOException {   
    	String strs="<wow ver='3.0'> <Passport Account='0' UserRole='-1'/>"
               +"<Project ProjectId='37' Section='2017' Grade='省培'	Title='2017年西部九市县边远乡村教学点小学教师培训网络研修' Locked='0'/>"
               +"<Project ProjectId='36' Section='2016' Grade='省培'	Title='2016年西部九市县边远乡村教学点小学教师培训网络研修' Locked='1'/>"
               +"<Project	ProjectId='35' Section='2015' Grade='国培'	Title='国培计划（2015）—海南省乡村幼儿园园长培训' Locked='1'/>"
               +" <Project	ProjectId='34' Section='2015' Grade='市县'	Title='儋州市2015年边远乡村教学点小学教师普及性培训项目' Locked='1'/> "
               +" <Project	ProjectId='33' Section='2015' Grade='国培'	Title='国培计划(2015)-海南省小学乡村教师信息技术应用能力提升培训项目' Locked='1'/>"
               +"</wow>";
        return strs;    
    }
    
    @RequestMapping("/CommunityStudy/specialTeacher")    
    public ModelAndView specialTeacher(HttpServletRequest request,HttpServletResponse response) {    
    	//特级教师工作站
        return new ModelAndView("WEB-INF/t/cms/www/default/study/community_study/workstation/special_teacher.html");    
    } 
    
    @RequestMapping("/CommunityStudy/primarySchool")    
    public ModelAndView primarySchool(HttpServletRequest request,HttpServletResponse response) {    
    	//小学工作站
        return new ModelAndView("WEB-INF/t/cms/www/default/study/community_study/workstation/primary_school.html");    
    } 
    
    
    @RequestMapping("/CommunityStudy/paceSetter")    
    public ModelAndView paceSetter(HttpServletRequest request,HttpServletResponse response) {    
    	//小学骨干带头人工作站
        return new ModelAndView("WEB-INF/t/cms/www/default/study/community_study/workstation/pace_setter.html");    
    } 
    
    @RequestMapping("/CommunityStudy/cultivationTarget")    
    public ModelAndView cultivationTarget(HttpServletRequest request,HttpServletResponse response) {    
        return new ModelAndView("WEB-INF/t/cms/www/default/study/community_study/workstation/cultivation_target.html");    
    } 
    
    
}
