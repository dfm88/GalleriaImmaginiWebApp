package it.polimi.margoni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;


import it.polimi.margoni.beans.UserBeanRIA;
import it.polimi.margoni.dao.OrdinamentoDAORIA;
import it.polimi.margoni.utils.DBConnectionRIA;



/**
 * Servlet implementation class AddCommentRIA
 */
@WebServlet("/SaveOrderRIA")
@MultipartConfig
public class SaveOrderRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveOrderRIA() {
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
		
		UserBeanRIA user = (UserBeanRIA) session.getAttribute("user");
		Integer[] arr = null;
		Integer[] arr2 = null;
	
		
		
		try {
		
			if(request.getParameter("arrayOrd")!=null && request.getParameter("arrayAlb")!=null )
			{
				Gson gson = new Gson();
				arr = gson.fromJson(request.getParameter("arrayOrd"), Integer[].class); 
				arr2 = gson.fromJson(request.getParameter("arrayAlb"), Integer[].class);			
			}
			
			
		
				
	
			
		} catch (NumberFormatException | NullPointerException e) {
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Errore");
			return;
		}
		
		//converto gli array in ArraList
		ArrayList<Integer> idOrdinametoList = new ArrayList<Integer>(Arrays.asList(arr));
		ArrayList<Integer> idAlbumIDsList = new ArrayList<Integer>(Arrays.asList(arr2));
		
		OrdinamentoDAORIA ordinamentoDAORIA = new OrdinamentoDAORIA(connection);
		List<UserBeanRIA> users = new ArrayList<UserBeanRIA>();
		
			try {
				
				//controllo se i numeri dell'ordinamento sono uguale del numero di album
				for(int i=0; i<idOrdinametoList.size(); i++)
				{
					if (idOrdinametoList.get(i)>idAlbumIDsList.size())
					{
						
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
						response.getWriter().println("Not possible to recover albums");		
						
					}
				}
				
		
			users = ordinamentoDAORIA.findOrdinamento(user.getId());
			
			
	//SCELTA SE INSERIRE L'ORDINAMENTO NUOVO SE NON ESISTEVA O SE AGGIORNARE QUELLO ESISTENTE	
	//se l'user non ha mai salvato un nuovo ordinamento, INSERT, altrmenti UPDATe
			if(users.isEmpty() || users.size()==0 || users == null)
			{
				
				ordinamentoDAORIA.aggiungiNuovoOrdinamento(user.getId(), idAlbumIDsList, idOrdinametoList);
				System.out.println("Nella SaveOrderRIA è partito l'INSERT");
			} else {
				ordinamentoDAORIA.modificaOrdinamento(user.getId(), idAlbumIDsList, idOrdinametoList);
				System.out.println("Nella SaveOrderRIA è partito l'UPDATE");
				
			}
			
			
			
		} catch (SQLException e)  {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
			response.getWriter().println("Not possible to recover albums");		
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
