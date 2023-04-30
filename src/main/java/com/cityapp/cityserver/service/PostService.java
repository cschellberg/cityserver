package com.cityapp.cityserver.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClient;
import com.cityapp.cityserver.config.SystemConfiguration;
import com.cityapp.cityserver.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchResponse;


import com.restfb.batch.BatchRequest.BatchRequestBuilder;

@Service
public class PostService {

	private AWSSecretsManager secretsClient;

	public PostService(SystemConfiguration systemConfiguration) {
		
		secretsClient = AWSSecretsManagerClient.builder().withRegion(systemConfiguration.getAwsRegion())
				.withCredentials(new DefaultAWSCredentialsProviderChain()).build();
	}

	@Autowired
	DynamoDBService dynamoDBService;

	private String getAccessToken(ObjectMapper objectMapper) throws URISyntaxException, Exception {

		GetSecretValueRequest valueRequest = new GetSecretValueRequest();
		valueRequest.setSecretId("CityAppStuff");
		GetSecretValueResult valueResponse = secretsClient.getSecretValue(valueRequest);
		String secret = valueResponse.getSecretString();
		var jsonNode=objectMapper.readTree(secret);
		var appId = jsonNode.get("city_app_id").asText();
		var appSecret = jsonNode.get("city_app_secret").asText();
		var url = String.format(
				"https://graph.facebook.com/oauth/access_token?grant_type=client_credentials&client_id=%s&client_secret=%s&fb_exchange_token=SHORT-LIVED-USER-ACCESS-TOKEN",
				appId, appSecret);
		var request = HttpRequest.newBuilder(new URI(url)).GET().build();
		var client = HttpClient.newBuilder().build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		var responseBody = response.body();
		jsonNode = objectMapper.readTree(responseBody);
		var token = jsonNode.get("access_token").asText();
		return token;
	}

	@Scheduled(fixedDelay = 60 * 60 * 1000)
	public List<Post> getPosts() {
		ObjectMapper objectMapper = new ObjectMapper();
		var lastPostUpdate = dynamoDBService.getLastPostUpdate();
		System.out.println(lastPostUpdate);
		FacebookClient facebookClient;
		try {
			facebookClient = new DefaultFacebookClient(getAccessToken(objectMapper), Version.VERSION_6_0);
		} catch (Exception e) {
			System.out.println("could not get access token");
			return new ArrayList<Post>();
		}
		BatchRequestBuilder batchRequestBuilder = new BatchRequestBuilder("/754760502650127/feed");
		BatchRequest batchRequest = batchRequestBuilder.method("GET").build();
		List<BatchResponse> batchResponseList = facebookClient.executeBatch(batchRequest);

		var postList = new ArrayList<Post>();

		for (var batchResponse : batchResponseList) {
			try {
				var jsonNode = objectMapper.readTree(batchResponse.getBody());
				var dataNode = jsonNode.get("data");
				var str = dataNode.toString();
				Post[] posts = objectMapper.readValue(str, Post[].class);
				postList.addAll(Arrays.asList(posts));
			} catch (Exception ex) {
				System.out.println(ex);
			}

		}
		System.out.println(postList);
		dynamoDBService.setLastPostUpdate();
		return new ArrayList<Post>();
	}

}
