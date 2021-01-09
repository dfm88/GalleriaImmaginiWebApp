package it.polimi.margoni.controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;




import com.google.gson.Gson;

import it.polimi.margoni.beans.CommentBeanRIA;
import it.polimi.margoni.beans.ImageBeanRIA;
import it.polimi.margoni.beans.UserBeanRIA;
import it.polimi.margoni.dao.CommentDAORIA;
import it.polimi.margoni.dao.ImageDAORIA;
import it.polimi.margoni.utils.DBConnectionRIA;



/**
 * Servlet implementation class GetImageDetailsRIA
 */
@WebServlet("/GeImageDetailsRIA")
public class GetImageDetailsRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetImageDetailsRIA() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {

		connection = DBConnectionRIA.getConnection(getServletContext());	
		
	
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub

		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
	
		// get and check params
		Integer albumId = null;
		String imagePathString = null;
		
		
		
		System.out.println("E' stata chiamata la servlet GeImagesDetailsRIA");
		
		
		try {
			if(request.getParameter("albumid")!=null)
				albumId = Integer.parseInt(request.getParameter("albumid"));
	//		if(request.getParameter("pageno")!=null)
	//			pageNo = Integer.parseInt(request.getParameter("pageno"));
			if(request.getParameter("percorso")!=null)
				imagePathString = request.getParameter("percorso");
		
			System.out.println("Album id ricevuto nella GET: "+albumId+" /// apercorso immagine nella GET: "+imagePathString);
			
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values Servlet GetImageDetailsRIA");
			return;
		}
				
		ImageBeanRIA im = new ImageBeanRIA();
		List<CommentBeanRIA> comments = new ArrayList<CommentBeanRIA>();
		
		ImageDAORIA imageDAORIA = new ImageDAORIA(connection);
		CommentDAORIA commentDAORIA = new CommentDAORIA(connection);
		UserBeanRIA us = (UserBeanRIA) session.getAttribute("user");	
		
		
		try { 
			

			im = imageDAORIA.findImgeDetails(imagePathString, albumId);
			comments = commentDAORIA.findCommentsByImage(imagePathString, albumId, us); 
		
			
						
			if (im == null) { //omesso test sui commenti in qunto possono essere vuoti
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Image not found");
				return;
				} 
			
			
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
			response.getWriter().println("Not possible to recover image");		
			return; 
		}
			
		System.out.println("Percorso dell'imagine nella servlet "+ im.getPath());
		
		String jsonIM = new Gson().toJson(im);
		String jsonCM = new Gson().toJson(comments);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		String imCommJson = "["+jsonIM+","+jsonCM+"]";
		
		response.getWriter().write(imCommJson);
		
	
}

	public void destroy() {
		try {
			DBConnectionRIA.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
	
}