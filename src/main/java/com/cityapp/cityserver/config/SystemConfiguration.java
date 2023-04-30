package com.cityapp.cityserver.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfiguration {
	
	private String awsRegion="us-east-1";
	
	private String dynamoDBTable="CityAppDB";

	public String getAwsRegion() {
		return awsRegion;
	}

	public String getDynamoDBTable() {
		return dynamoDBTable;
	}
	
	

}
