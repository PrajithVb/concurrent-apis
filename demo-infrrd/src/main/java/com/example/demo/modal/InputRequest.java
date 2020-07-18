package com.example.demo.modal;



import com.example.demo.Status;

import lombok.Data;

@Data
public class InputRequest {
	
	private String payloadId;
	private Status status;
	private String data;
	private String requestId;

}
