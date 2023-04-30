package com.cityapp.cityserver.service;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.cityapp.cityserver.config.SystemConfiguration;
import com.cityapp.cityserver.model.CityAppSystem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DynamoDBService {

	private final static String SYSTEM_KEY="system";
	
	private AmazonDynamoDB dynamoDbClient;
	
	private String dynamoDBTable;
	
	
	public DynamoDBService(SystemConfiguration systemConfiguration) {
			dynamoDbClient = AmazonDynamoDBClient.builder()
		    .withCredentials(new DefaultAWSCredentialsProviderChain() )
            .withRegion(systemConfiguration.getAwsRegion())
            .build();
			dynamoDBTable=systemConfiguration.getDynamoDBTable();
	}
	
	public Date getLastPostUpdate() {
		try {
		ObjectMapper objectMapper = new ObjectMapper();
        GetItemRequest request = new GetItemRequest();
        request.setTableName(dynamoDBTable);
        request.setKey(Map.of("Id",new AttributeValue(SYSTEM_KEY),"Category",new AttributeValue("SYSTEM")));
		var response=dynamoDbClient.getItem(request);
		var systemMap=response.getItem();
		AttributeValue value=systemMap.get("data");
		CityAppSystem system=objectMapper.readValue(value.getS(), CityAppSystem.class);
		System.out.println(value.getS());
		}catch(Exception ex) {
			System.out.println(ex);
		}
		return new Date();
	}
	
	public void setLastPostUpdate() {
		try {
		ObjectMapper objectMapper = new ObjectMapper();
		var cityAppSystem=new CityAppSystem();
		cityAppSystem.setLastPostUpdate(new Date());
        PutItemRequest request = new PutItemRequest();
        request.setTableName(dynamoDBTable);
        Map<String,AttributeValue> itemValues = Map.of("Id", new AttributeValue(SYSTEM_KEY),
        		"Category",new AttributeValue("SYSTEM"),"UserId",new AttributeValue("SYSTEM"),"data",new AttributeValue(objectMapper.writeValueAsString(cityAppSystem)));

        request.setItem(itemValues);
		var response=dynamoDbClient.putItem(request);
		System.out.println(response);
		}catch(Exception ex) {
			System.out.println(ex);
		} 
	}
	
	
}
