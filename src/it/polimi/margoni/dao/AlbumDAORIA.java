package it.polimi.margoni.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import it.polimi.margoni.beans.AlbumBeanRIA;


public class AlbumDAORIA
{
	private Connection connection;
	
	public AlbumDAORIA(Connection con)
	{
		this.connection = con;
	}
	
	public List<AlbumBeanRIA> findAlbums() throws SQLException
	{
		List<AlbumBeanRIA> albums = new ArrayList<AlbumBeanRIA>();
		
		String query = "SELECT * from album order by date desc";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) 
		{
			
			try (ResultSet result = pstatement.executeQuery();) 
			{
				while (result.next()) 
				{
					AlbumBeanRIA album = new AlbumBeanRIA();
					album.setId(result.getInt("albumID"));
					album.setTitle(result.getString("title"));
					album.setCreationDate(result.getDate("date"));
					albums.add(album);
				}
			}
		}
		
	
		
		return albums;
		
		
	}
	
	public List<AlbumBeanRIA> findPersonalSortedAlbums(int userid) throws SQLException
	{
		List<AlbumBeanRIA> albums = new ArrayList<AlbumBeanRIA>();
		
		String query = "SELECT A.albumID, A.date, A.title, O.ordinamento from album as A JOIN ordinamento as O "
				+ "ON A.albumid = O.albumID WHERE O.userID = ? order by ordinamento asc";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) 
		{
			pstatement.setInt(1, userid);
			
			try (ResultSet result = pstatement.executeQuery();) 
			{
				while (result.next()) 
				{
					AlbumBeanRIA album = new AlbumBeanRIA();
					album.setId(result.getInt("albumID"));
					album.setTitle(result.getString("title"));
					album.setCreationDate(result.getDate("date"));
					albums.add(album);
				}
			}
		}
		
	
		
		return albums;
		
		
	}
	
	
	
	

	
}
