package it.polimi.margoni.dao;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import it.polimi.margoni.beans.CommentBeanRIA;
import it.polimi.margoni.beans.UserBeanRIA;



public class CommentDAORIA
{
	private Connection connection;

	public CommentDAORIA(Connection con)
	{
		this.connection = con;
	}
	
	public List<CommentBeanRIA> findCommentsByImage(String imagePath, int albumID, UserBeanRIA us) throws SQLException, IOException
	{
		List<CommentBeanRIA> comments = new ArrayList<CommentBeanRIA>();
		
		
		String query = "SELECT  * from comment where pathIm = ? and album = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) 
		{
			pstatement.setString(1, imagePath);
			pstatement.setInt(2, albumID);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					CommentBeanRIA commentBean = new CommentBeanRIA();
					
					commentBean.setAlbumIDComment(result.getInt("commentID"));
					commentBean.setPathImComment(result.getString("pathIm"));
					commentBean.setAlbumIDComment(result.getInt("album"));
					commentBean.setUserIdComment(result.getInt("user"));
					commentBean.setCommentoString(result.getString("text"));
					commentBean.setUserNameComment(result.getString("userNameComm"));
					
					comments.add(commentBean);
					
					
				}
			
				
			}
		}
		
		return comments;
		
	}
	
	public void addComment(int albumId, int userId, String percorsoIm, String testoCommento, String username)
			throws SQLException {

		String query = "INSERT into comment (pathIm, album, user, text, userNameComm) VALUES(?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, percorsoIm );
			pstatement.setInt(2, albumId);
			pstatement.setInt(3, userId);
			pstatement.setString(4, testoCommento);
			pstatement.setString(5, username);
			
			
			pstatement.executeUpdate();
		}
	}

}
