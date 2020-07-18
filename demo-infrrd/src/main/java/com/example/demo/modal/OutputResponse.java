package com.example.demo.modal;


import com.example.demo.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputResponse extends ApiOutput{
	
private String id;
private String requestId;
private Status status;
	
	public OutputResponse(String id,boolean success, String requestId) {
		this.id = id;
		this.requestId= requestId;
		setSuccess(success);
	}

}
