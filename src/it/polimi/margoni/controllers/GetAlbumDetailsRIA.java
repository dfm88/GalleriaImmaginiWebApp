package it.polimi.margoni.controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.google.gson.Gson;

import it.polimi.margoni.beans.AlbumBeanRIA;
import it.polimi.margoni.beans.ImageBeanRIA;
import it.polimi.margoni.dao.ImageDAORIA;
import it.polimi.margoni.utils.DBConnectionRIA;




/**
 * Servlet implementation class GetAlbumDetailsRIA
 */
@WebServlet("/GetAlbumDetailsRIA")
@MultipartConfig
public class GetAlbumDetailsRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
		
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAlbumDetailsRIA() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

		connection = DBConnectionRIA.getConnection(getServletContext());
	
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession(false);
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			System.out.println("401");
			
			return;
		}

		// get and check params
		Integer albumId = null;

			

		ImageDAORIA imageDAO = new ImageDAORIA(connection);	
		
		
		List<ImageBeanRIA> images = new ArrayList<ImageBeanRIA>();
		
		
		
		
		try {
			if(request.getParameter("albumid")!=null)
				albumId = Integer.parseInt(request.getParameter("albumid"));
			
			System.out.println("album id nella servlet "+albumId);
		
			
		//	if(request.getParameter("pageno")!=null)
		//		pageNo = Integer.parseInt(request.getParameter("pageno"));
	
			
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values servlet GetAlbumDetailsRIA");

			return;
		}

	
		
		try { 
			
			images = imageDAO.findImagesByAlbum(albumId);
	
			
			
			if (images == null ) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			System.out.println("Resource not found");
				return;
				} 
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Not possible to recover images");
			System.out.println("Not possible to recover images");
			return;
		}
			
		String json = new Gson().toJson(images);
		
	
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
		
		
	
}

	public void destroy() {
		try {
			DBConnectionRIA.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
