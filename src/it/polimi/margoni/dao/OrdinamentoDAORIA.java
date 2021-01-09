package it.polimi.margoni.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;



import it.polimi.margoni.beans.UserBeanRIA;


public class OrdinamentoDAORIA
{
	private Connection connection;
	
	public OrdinamentoDAORIA(Connection con)
	{
		this.connection = con;
	}
	
	public List<UserBeanRIA> findOrdinamento(int userid) throws SQLException
	{
		List<UserBeanRIA> userss = new ArrayList<UserBeanRIA>();
		
		String query = "SELECT * from ordinamento WHERE userID = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) 
		{
			pstatement.setInt(1, userid);
			
			try (ResultSet result = pstatement.executeQuery();) 
			{
				while (result.next()) 
				{
					UserBeanRIA u = new UserBeanRIA();
					u.setId(result.getInt("userID"));
					
					userss.add(u);
				}
			}
		}
		
	
		
		return userss;
		
		
	}
	
	
	//salva nuovo ordinamento per la prima volta
	public void aggiungiNuovoOrdinamento(int userid, List<Integer> albumids, List<Integer> ordinamenti) throws SQLException
	{
		for (int i=0; i< albumids.size(); i++)
		{
			String query = "INSERT into ordinamento (userID, albumID, ordinamento) VALUES(?, ?, ?)";
			
			try (PreparedStatement pstatement = connection.prepareStatement(query);) 
			{
				pstatement.setInt(1, userid);
				pstatement.setInt(2, albumids.get(i));
				pstatement.setInt(3, ordinamenti.get(i));
				
				pstatement.executeUpdate();
				
				
			}
			
		}
		
	}
	
	//aggiorna ordinamento esistente
	//
	public void modificaOrdinamento(int userid, List<Integer> albumids, List<Integer> ordinamenti) throws SQLException
	{
		for (int i=0; i< albumids.size(); i++)
		{
			String query = "UPDATE ordinamento SET ordinamento = ? WHERE userID = ? and albumID = ?";
			
			try (PreparedStatement pstatement = connection.prepareStatement(query);) 
			{
				
				pstatement.setInt(1, ordinamenti.get(i));
				pstatement.setInt(2, userid);
				pstatement.setInt(3, albumids.get(i));
				pstatement.executeUpdate();
				
				
			}
			
		}
		
	}
	

	
}
