package it.polimi.margoni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
@WebServlet("/AddNewUserRIA")
@MultipartConfig
public class AddNewUserRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewUserRIA() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		connection = DBConnectionRIA.getConnection(getServletContext());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String usrn = null;
		String pwd1 = null;
		String pwd2 = null;
		String userNameEsistenti = null;
		String email = null;
		
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
		pwd1 = StringEscapeUtils.escapeJava(request.getParameter("pwd1"));
		pwd2 = StringEscapeUtils.escapeJava(request.getParameter("pwd2"));
		
		if (usrn == null || pwd1 == null || usrn.isEmpty() || pwd1.isEmpty() || pwd2 == null || pwd2.isEmpty() ) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}
		
		System.out.println("dati ricevuti nella servlet : "+email+" --- "+usrn+" --- "+pwd1+" --- "+pwd2);
		System.out.println("sono diverse le password? "+(!pwd1.equals(pwd2)));
		
		if (!pwd1.equals(pwd2)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Passwords are different");
			return;
		}
		
		
		//check email validation
		boolean valid;
		
		valid = emailChecker(email);
		System.out.println("L'email è valida? "+valid);
		
		if(!valid)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid email format");
			return;
		}
		
		
		
		// query db to authenticate for user
		UserDAORIA userDao = new UserDAORIA(connection);
		List<UserBeanRIA> users = new ArrayList<UserBeanRIA>();
		
		
		
		try {
			users = userDao.getAllUsername();
			
			
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		for(int i = 0; i < users.size(); i++)
		{
			UserBeanRIA uuu = new UserBeanRIA();
			uuu = users.get(i);
			userNameEsistenti = uuu.getUsername();
			
			System.out.println("stringhe ricevute dall'array" +userNameEsistenti);
			
			if (usrn.equals(userNameEsistenti) )
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Username '"+usrn+"' already exist");
				return;
			}
		}
		
		
				try
				{
					userDao.signUpNewUser(email, usrn, pwd1);
				} catch (SQLException e)
				{
					System.out.println("Internal server error, retry later");
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Internal server error, retry later");
					return;
				}
			
			
			
			
		
		

		
	
		
	}

	public void destroy() {
		try {
			DBConnectionRIA.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean emailChecker(String emailToCheck) {
	      String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	      return emailToCheck.matches(regex);
	   }
}
