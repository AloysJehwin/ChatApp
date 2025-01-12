package com.aloysjehwin.registration;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegistrationServlet
 */
@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name=request.getParameter("name");
        String username=request.getParameter("username");
        String password=request.getParameter("pass");
        String mobile=request.getParameter("contact");
        
//        PrintWriter out=response.getWriter();
//        out.print(name);
//        out.print(username);
//        out.print(password);
//        out.print(mobile);
        
        RequestDispatcher dispatcher=null;
        Connection con=null;
        
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
        	con= DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp?useSSL=false","root","Ausnet@1975");
        	PreparedStatement pst=con.prepareStatement("insert into users(name,username,password,mobile) values(?,?,?,?)");
        	pst.setString(1, name);
        	pst.setString(2, username);
        	pst.setString(3, password);
        	pst.setString(4, mobile);
        	
        	int rowCount=pst.executeUpdate();
        	dispatcher=request.getRequestDispatcher("registration.jsp");
        	if(rowCount>0) {
        		request.setAttribute("status","success");
        	}else {
        		request.setAttribute("status","failed");
        	}
        	dispatcher.forward(request, response);
        }catch(Exception e) {
        	e.printStackTrace();
        }finally {
        	try {
        		con.close();        	
        	}catch (SQLException e){
        		e.printStackTrace();
        	}
        }
    }
}

