package it.polimi.margoni.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import it.polimi.margoni.beans.UserBeanRIA;


public class UserDAORIA
{
	private Connection con;

	public UserDAORIA(Connection connection) {
		this.con = connection;
	}
	
	public UserBeanRIA checkCredentials(String usrn, String pwd) throws SQLException {
		
		
		
		String query = "SELECT  userID, username FROM user WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
				
					result.next();
					UserBeanRIA user = new UserBeanRIA();
					user.setId(result.getInt("userID"));
					user.setUsername(result.getString("username"));
					return user;
				}
			}
		}
	}
	
	public void signUpNewUser (String email, String username, String pwd1) throws SQLException {
		
		String query = "INSERT into user (username, password, email) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, pwd1);
			pstatement.setString(3, email);
			
			pstatement.executeUpdate();

			}
		}
	
	public List<UserBeanRIA> getAllUsername() throws SQLException {
		


		List<UserBeanRIA> users = new ArrayList<UserBeanRIA>();
		
		String query = "SELECT username from user";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
						
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					UserBeanRIA userBean = new UserBeanRIA();
					
					userBean.setUsername(result.getString("username"));
					
					
					users.add(userBean);
					
					
				}
			
				
			}
		}
		
		return users;
		
		
		
	}
		
	
	
	
	
	

}