package it.polimi.margoni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.margoni.utils.DBConnectionRIA;
import it.polimi.margoni.beans.UserBeanRIA;
import it.polimi.margoni.dao.UserDAORIA;


/**
 * Servlet implementation class CheckLoginRIA
 */
@WebServlet("/CheckLoginRIA")
@MultipartConfig
public class CheckLoginRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLoginRIA() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		connection = DBConnectionRIA.getConnection(getServletContext());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String usrn = null;
		String pwd = null;
		
		usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
		pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
		
		if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty() ) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			System.out.println("Credentials must be not null");
			return;
		}
		
		// query db to authenticate for user
		UserDAORIA userDao = new UserDAORIA(connection);
		UserBeanRIA user = null;
		
		
		try {
			user = userDao.checkCredentials(usrn, pwd);
			
			
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			System.out.println("Internal server error, retry later");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message

		
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Incorrect credentials");
			System.out.println("Incorrect credentials");
		} else {
			request.getSession().setAttribute("user", user);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(usrn);
		}
	}

	public void destroy() {
		try {
			DBConnectionRIA.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
