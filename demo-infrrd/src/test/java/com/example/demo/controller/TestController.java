package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.Paths;
import com.example.demo.Status;
import com.example.demo.modal.InputRequest;
import com.example.demo.modal.OutputResponse;
import com.example.demo.service.ApiService;




@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestController {
	
	@LocalServerPort
	private int randomServerPort;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	ApiService apiService;
	
	@Test
	public void failure_createNoID() throws Exception {
		InputRequest a = new InputRequest();
		assertEquals(this.restTemplate.postForEntity("http://localhost:"+randomServerPort+Paths.VERSION+Paths.START, 
				a,OutputResponse.class).getStatusCode(),HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void success_createID() throws Exception {
		InputRequest a = new InputRequest();
		a.setPayloadId("12");
		a.setData("Hi");
		assertEquals(this.restTemplate.postForEntity("http://localhost:"+randomServerPort+Paths.VERSION+Paths.START, 
				a,OutputResponse.class).getStatusCode(),HttpStatus.OK);
		OutputResponse response = this.restTemplate.postForEntity("http://localhost:"+randomServerPort+Paths.VERSION+Paths.START, 
				a,OutputResponse.class).getBody();
		assertTrue(response.isSuccess());
		assertEquals(response.getStatus(),Status.PENDING);
	}
	
	
	@Test
	public void failure_endNoID() throws Exception {
		
		assertEquals(this.restTemplate.getForEntity("http://localhost:"+randomServerPort+Paths.VERSION+"/end/input/12/request/123", 
				OutputResponse.class).getStatusCode(),HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void success_endID() throws Exception {
		InputRequest a = new InputRequest();
		a.setPayloadId("12");
		a.setData("Hi");
		OutputResponse ac= apiService.start(a);
		assertEquals(this.restTemplate.getForEntity("http://localhost:"+randomServerPort+Paths.VERSION+"/end/input/12/request/"+ ac.getRequestId(), 
				OutputResponse.class).getStatusCode(),HttpStatus.OK);
		
		OutputResponse response = this.restTemplate.getForEntity("http://localhost:"+randomServerPort+Paths.VERSION+"/end/input/12/request/"+ ac.getRequestId(), 
				OutputResponse.class).getBody();
		assertTrue(response.isSuccess());
		assertEquals(response.getStatus(),Status.SUCCESS);
	}
	
	@Test
	public void success_end_conflictID() throws Exception {
		InputRequest a = new InputRequest();
		a.setPayloadId("12");
		a.setData("Hi");
		OutputResponse ab =this.restTemplate.postForEntity("http://localhost:"+randomServerPort+Paths.VERSION+Paths.START, 
				a,OutputResponse.class).getBody();
		OutputResponse ac = this.restTemplate.postForEntity("http://localhost:"+randomServerPort+Paths.VERSION+Paths.START, 
				a,OutputResponse.class).getBody();
		System.out.println(ab.getRequestId());
		assertEquals(this.restTemplate.getForEntity("http://localhost:"+randomServerPort+Paths.VERSION+"/end/input/12/request/"+ ac.getRequestId(), 
				OutputResponse.class).getStatusCode(),HttpStatus.OK);
		assertEquals(this.restTemplate.getForEntity("http://localhost:"+randomServerPort+Paths.VERSION+"/end/input/12/request/"+ ab.getRequestId(), 
				OutputResponse.class).getStatusCode(),HttpStatus.CONFLICT);
		
		OutputResponse response = this.restTemplate.getForEntity("http://localhost:"+randomServerPort+Paths.VERSION+"/end/input/12/request/"+ ac.getRequestId(), 
				OutputResponse.class).getBody();
		assertTrue(response.isSuccess());
		assertEquals(response.getStatus(),Status.SUCCESS);
	}

}
