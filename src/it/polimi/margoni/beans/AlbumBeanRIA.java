package it.polimi.margoni.beans;

import java.sql.Date;

public class AlbumBeanRIA
{
	private int id;
	private String title;
	private Date creationDate;



	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getTitle()
	{
		return title;
	} 
	public void setTitle(String title)
	{
		this.title = title;
	}
	public Date getCreationDate()
	{
		return creationDate;
	}
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}
	
	
	
}
