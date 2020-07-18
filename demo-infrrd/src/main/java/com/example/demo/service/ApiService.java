package com.example.demo.service;

import com.example.demo.modal.InputRequest;
import com.example.demo.modal.OutputResponse;

public interface ApiService {
	
	public OutputResponse start(InputRequest input);
	public OutputResponse end(String input, String requestId);

}
