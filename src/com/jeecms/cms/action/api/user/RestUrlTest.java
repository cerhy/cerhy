package com.jeecms.cms.action.api.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class RestUrlTest extends HttpServlet {
	
	 public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
         response.setContentType("text/html");
         String uri = request.getPathInfo();
         //request.getRequestDispatcher("/blog/index.jspx?name="+uri).forward(request,response);
         response.sendRedirect(request.getContextPath()+"/blog/find_all_url_friend.jspx?name="+uri);
         
     }
}
