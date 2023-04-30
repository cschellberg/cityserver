package com.cityapp.cityserver.restcontrollers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cityapp.cityserver.model.Post;
import com.cityapp.cityserver.service.PostService;

@RestController
public class CityAppController {
	
	@Autowired
	private PostService postService;
	    
		@GetMapping("/posts")
	    public List<Post> getPosts() {
	    	return postService.getPosts();
	    }


}