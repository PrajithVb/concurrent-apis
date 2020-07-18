package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Paths;
import com.example.demo.Status;
import com.example.demo.SubErrorCode;
import com.example.demo.modal.InputRequest;
import com.example.demo.modal.OutputResponse;
import com.example.demo.service.ApiService;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(Paths.VERSION)
public class ApiController {
	
	@Autowired
	ApiService apiService;

	@PostMapping(value = Paths.START)
	@ApiOperation(httpMethod = "POST", value = "Create Request", notes = "Start API for adding details", response = OutputResponse.class)
	public ResponseEntity<OutputResponse> start(@RequestBody InputRequest input) {
		if(input.getPayloadId()==null || StringUtils.isEmpty(input.getPayloadId())) {
			OutputResponse out = new OutputResponse("", false, "");
			out.setStatus(Status.ERROR);
			out.setMessage("Input Id Missing");
			return ResponseEntity.badRequest().body(out);
		}else {
			OutputResponse out= apiService.start(input);
			if(out.isSuccess()) {
				return ResponseEntity.ok(out);
			}
			else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(out);
			}
		}
		
	}
	
	@GetMapping(value = Paths.END)
	@ApiOperation(httpMethod = "GET", value = "End Request", notes = "End API for complete the request", response = OutputResponse.class)
	
	public ResponseEntity<OutputResponse> end(@PathVariable String id, @PathVariable String reqId) {
		if(StringUtils.isEmpty(id)) {
			OutputResponse out = new OutputResponse("", false, "");
			out.setStatus(Status.ERROR);
			out.setMessage("Input Id Missing");
			return ResponseEntity.badRequest().body(out);
		}else if(StringUtils.isEmpty(reqId)) {
			OutputResponse out = new OutputResponse("", false, "");
			out.setMessage("request Id Missing");
			out.setStatus(Status.ERROR);
			return ResponseEntity.badRequest().body(out);
		}else {
			OutputResponse out= apiService.end(id,reqId);
			if(out.isSuccess()) {
				return ResponseEntity.ok(out);
			}
			else {
				if(out.getStatus().equals(Status.INVALID))
				return ResponseEntity.status(HttpStatus.CONFLICT).body(out);
				else {
					if(out.getSubErrorCode().equals(SubErrorCode.DATA_ENTRY_ERROR)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(out);
					}else {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(out);
					}
				}
			}
			
		}
	}


}