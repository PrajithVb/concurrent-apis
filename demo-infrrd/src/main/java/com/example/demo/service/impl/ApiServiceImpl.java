package com.example.demo.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.ErrorGroup;
import com.example.demo.Status;
import com.example.demo.SubErrorCode;
import com.example.demo.modal.InputRequest;
import com.example.demo.modal.OutputResponse;
import com.example.demo.service.ApiService;

import lombok.extern.slf4j.Slf4j;

@Service("apiService")
@Slf4j
public class ApiServiceImpl implements ApiService {

	private static Map<String, InputRequest> map = new HashMap<String, InputRequest>();
	private static Map<String, InputRequest> inValidMap = new HashMap<String, InputRequest>();

	@Override
	public OutputResponse start(InputRequest input) {
		log.info("Create Service Started "+ input.getPayloadId());
		OutputResponse out = new OutputResponse();
		try {
			input.setRequestId(Long.toString(Calendar.getInstance().getTimeInMillis()));
			input.setStatus(Status.PENDING);
			out.setRequestId(input.getRequestId());
			out.setId(input.getPayloadId());
			if (map.isEmpty()) {
				log.info("Empty map- New Data Added " + input.getPayloadId()+ " - "+ input.getRequestId());
				map.put(input.getPayloadId(), input);

			} else if (map.get(input.getPayloadId()) != null) {
				
				InputRequest existing = map.get(input.getPayloadId());
				String key = existing.getRequestId();
				log.info("Data Already Present " + input.getPayloadId() + " , Request ID - "+ key);
				existing.setStatus(Status.INVALID);
				inValidMap.put(key, existing);
				input.setStatus(Status.PENDING);
				log.info("Data Added To Invalid Table "+ key);
				map.remove(existing.getPayloadId());
				map.put(input.getPayloadId(), input);
				log.info("New Data Added " + input.getPayloadId());
			} else {
				log.info("Non Empty map- New Data Added " + input.getPayloadId());
				map.put(input.getPayloadId(), input);
			}
			// TODO Auto-generated method stub

			out.setStatus(Status.PENDING);
			out.setSuccess(true);

		} catch (Exception e) {
			log.error(e.getMessage());
			out.setStatus(Status.ERROR);
			out.setSuccess(false);
			out.setErrorGroup(ErrorGroup.CREATE_TXN_ERROR);
			out.setSubErrorCode(SubErrorCode.DATA_ENTRY_ERROR);
			out.setMessage(e.getMessage());
			out.setRequestId("");
			out.setId("");
		}
		return out;
	}

	@Override
	public OutputResponse end(String input, String reqId) {
		log.info("End Service Started "+ input+ " - "+ reqId);
		OutputResponse out = new OutputResponse();
		try {
			out.setId(input);
			out.setRequestId(reqId);
			if (map.isEmpty()) {
				log.info("DB Is Empty");
				out.setStatus(Status.ERROR);
				out.setSuccess(false);
				out.setErrorGroup(ErrorGroup.UPDATE_TXN_ERROR);
				out.setSubErrorCode(SubErrorCode.NOT_FOUND_ON_SERVER);
				out.setMessage("No Data in the Server");
				out.setRequestId("");

			} else if (map.get(input) != null) {
				InputRequest in = map.get(input);
				log.info("Data Exists In DB");
				if (reqId.equalsIgnoreCase(in.getRequestId())) {
					out.setStatus(Status.SUCCESS);
					log.info("Request Id Matched With DB Data");
					out.setRequestId(in.getRequestId());
					out.setSuccess(true);
				}
				else if (inValidMap.get(reqId) == null) {
					log.info("Request Id Not Found In DB And Invalid Requests");
					out.setStatus(Status.ERROR);
					out.setErrorGroup(ErrorGroup.UPDATE_TXN_ERROR);
					out.setSubErrorCode(SubErrorCode.DATA_NOT_MATCH);
					out.setMessage("Request Id not in the Server");
					out.setRequestId(reqId);
					out.setSuccess(false);

				} else {
					log.info("Request Id Not In DB, But Found In Invalid Requests");
					out.setStatus(Status.INVALID);
					out.setSuccess(false);
					out.setErrorGroup(ErrorGroup.UPDATE_TXN_ERROR);
					out.setSubErrorCode(SubErrorCode.DATA_INVALID_ON_SERVER);
					out.setMessage("Data has been updated by another request");
					//out.setId(reqId);
				}

			} else {
				log.info("Payload Id Not In DB");
				out.setStatus(Status.ERROR);
				out.setSuccess(false);
				out.setErrorGroup(ErrorGroup.UPDATE_TXN_ERROR);
				out.setSubErrorCode(SubErrorCode.NOT_FOUND_ON_SERVER);
				out.setMessage("Payload ID not in the Server");
				out.setRequestId("");
			}
			// TODO Auto-generated method stub


		} catch (Exception e) {
			log.error(e.getMessage());
			out.setStatus(Status.ERROR);
			out.setSuccess(false);
			out.setErrorGroup(ErrorGroup.UPDATE_TXN_ERROR);
			out.setSubErrorCode(SubErrorCode.DATA_ENTRY_ERROR);
			out.setMessage("Server Failure, Please retry");
			out.setMessage(e.getMessage());
			out.setId(input);
			out.setRequestId(reqId);
		}
		return out;
	}

	public static void main(String args[]) {
		System.out.println(Long.toString(Calendar.getInstance().getTimeInMillis()));
	}

}
