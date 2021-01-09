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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;


import it.polimi.margoni.beans.UserBeanRIA;
import it.polimi.margoni.dao.CommentDAORIA;

import it.polimi.margoni.utils.DBConnectionRIA;



/**
 * Servlet implementation class AddCommentRIA
 */
@WebServlet("/AddCommentRIA")
@MultipartConfig
public class AddCommentRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddCommentRIA() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
    	connection = DBConnectionRIA.getConnection(getServletContext());	
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		
		
		String commentoInserito = null;
		Integer albumId = null;
		String imagePathString = null;
		Boolean commentoVuoto = false;
		
		
		try {
			if(request.getParameter("commm")!=null)
				commentoInserito = StringEscapeUtils.escapeJava(request.getParameter("commm"));
			
			System.out.println("commento ricevuto nella servelt AddComment "+commentoInserito);
			
			commentoVuoto = commentoInserito.isEmpty();
			
			if(request.getParameter("albumid")!=null)
				albumId = Integer.parseInt(request.getParameter("albumid"));
			
			if(request.getParameter("percorso")!=null)
				imagePathString = request.getParameter("percorso");
			
			
		} catch (NumberFormatException | NullPointerException e) {
			commentoVuoto = true;
			e.printStackTrace();
		}
		if (commentoVuoto) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}

		
		UserBeanRIA user = (UserBeanRIA) session.getAttribute("user");
		
		CommentDAORIA  commentDAO = new CommentDAORIA (connection);
		
		try {
			commentDAO.addComment(albumId, user.getId(), imagePathString, commentoInserito, user.getUsername());
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to insert comment");
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
		
		
		
		
		
		
	

}
