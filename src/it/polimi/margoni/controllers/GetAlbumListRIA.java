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


import it.polimi.margoni.beans.AlbumBeanRIA;
import it.polimi.margoni.beans.UserBeanRIA;
import it.polimi.margoni.dao.AlbumDAORIA;
import it.polimi.margoni.dao.OrdinamentoDAORIA;
import it.polimi.margoni.utils.DBConnectionRIA;
import it.polimi.tiw.missions.utils.ConnectionHandler;





/**
 * Servlet implementation class GetAlbumListRIA
 */
@WebServlet("/GetAlbumListRIA")
public class GetAlbumListRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAlbumListRIA() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		
		connection = DBConnectionRIA.getConnection(getServletContext());
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
    
    	
    	String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		UserBeanRIA user = (UserBeanRIA) session.getAttribute("user");

		
		AlbumDAORIA AlbumDAORIA = new AlbumDAORIA(connection);
		OrdinamentoDAORIA ordinamentoDAORIA = new OrdinamentoDAORIA(connection);
		List<AlbumBeanRIA> albums = new ArrayList<AlbumBeanRIA>();
		List<UserBeanRIA> users = new ArrayList<UserBeanRIA>();
		
		
		
		{
		};

		try {
			
			users = ordinamentoDAORIA.findOrdinamento(user.getId());
	//SCELTA ORDINAMENTO PERSONALIZZATO		
	//se l'user non ha mai salvato un nuovo ordinamento, usa quello standar secondo la data degli album
			if(users.isEmpty() || users.size()==0 || users == null)
			{
				albums = AlbumDAORIA.findAlbums();
				System.out.println("Nella GetAlbumRIA è partito l'ordinamento standard");
			} else {
				albums = AlbumDAORIA.findPersonalSortedAlbums(user.getId());
				System.out.println("Nella GetAlbumRIA è partito l'ordinamento personalizzato");
			}
			
			
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
			response.getWriter().println("Not possible to recover albums");		
			return; 
		}
		
		

		// Redirect to the Home page and add missions to the parameters
		
				
				String json = new Gson().toJson(albums);
				
				
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
			}

			protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
				doGet(request, response);
			}

			public void destroy() {
				try {
					ConnectionHandler.closeConnection(connection);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
