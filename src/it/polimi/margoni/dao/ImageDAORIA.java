package it.polimi.margoni.dao;


import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;



import it.polimi.margoni.beans.AlbumBeanRIA;

import it.polimi.margoni.beans.ImageBeanRIA;



public class ImageDAORIA  ///FORMATO PERCORSO IMMAGINI ./Images/Astrattismo/astratto_unknown.jpg
{
	
	private Connection connection;
	private int noTotImag;
	
	public ImageDAORIA(Connection con)
	{
		this.connection = con;
	}
	
	public List<ImageBeanRIA> findImagesByAlbum(int AlbumId) throws SQLException, IOException
	{
		List<ImageBeanRIA> images = new ArrayList<ImageBeanRIA>();
		
		String query = "SELECT * from image where album = ? order by dateIm desc" ;
		
	
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) 
		{
			
			pstatement.setInt(1, AlbumId);
		
			try (ResultSet result = pstatement.executeQuery();) {
				
				while (result.next()) {
					ImageBeanRIA imag = new ImageBeanRIA();
							
					
					imag.setPath(result.getString("path"));
					imag.setAlbumid(result.getInt("album"));					
					imag.setTitleIm(result.getString("title"));
					imag.setDescriptionIm(result.getString("description"));
					imag.setDateIm(result.getDate("dateIm"));
					
					
					images.add(imag);
					
				
				}
			
			
			}
		}
		
	
		
		return images;
		
		
	}

	public int getNoTotImag()
	{
		return noTotImag;
	}
	
	public AlbumBeanRIA findAlbumByAlbum(int AlbumId) throws SQLException
	{
		
		AlbumBeanRIA alb = new AlbumBeanRIA();
		String query = "SELECT * from album where albumID = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) 
		{
			pstatement.setInt(1, AlbumId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					
					//imag.setPath(result.getString("path"));
					
					alb.setId(result.getInt("albumID"));
					alb.setCreationDate(result.getDate("date"));
					alb.setTitle(result.getString("title"));
					
					
					
				}
			
			
				
			}
		}
		
	
		return alb;
		
		
		
	}
	
	public ImageBeanRIA findImgeDetails(String ImagePath, int AlbumId) throws SQLException, IOException
	{
		
		ImageBeanRIA imag = new ImageBeanRIA();
		
		/*String query = "SELECT title, date, description, text * from image i JOIN comment c "
				+ "ON i.path = c.pathIm AND i.albumID  = c.albumID "
				+ "WHERE i.path = ? and i.albumID = ? ";*/
		
		String query = "SELECT * from image WHERE path = ? and album = ? ";
	
	
		try (PreparedStatement pstatement = connection.prepareStatement(query);) 
		{
			pstatement.setString(1, ImagePath);
			pstatement.setInt(2, AlbumId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					imag.setPath(result.getString("path"));
					imag.setTitleIm(result.getString("title"));
					imag.setDateIm(result.getDate("dateIm"));
					imag.setDescriptionIm(result.getString("description"));
										
				}
			
							
			}
		}
		
	
		return imag;
		
		
		
	}
	
	
	
	
		
		
		
	
	
}
