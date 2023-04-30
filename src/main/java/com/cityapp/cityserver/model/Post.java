package com.cityapp.cityserver.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;



public class Post extends BaseCityAppModel{
	
	@JsonProperty("updated_time")
	private Date updatedTime;
	
	private String message;
	
	public Post() {
		super();
	}
	
	public Post(Date updatedTime, String id, String message) {
		super();
		this.updatedTime = updatedTime;
		this.id = id;
		this.message = message;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
