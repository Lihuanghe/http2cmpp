package com.example.http2cmpp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinamobile.cmos.SmsClient;
import com.zx.sms.BaseMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;

@Controller
public class ApiController {
	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
	@Autowired
	private SmsClient smsClient;
	
	@ResponseBody
	@RequestMapping(value="/send",method = RequestMethod.POST)
	public Map send(String telephone,String txt) {
		Map result= new HashMap();
		
		CmppSubmitRequestMessage msg = CmppSubmitRequestMessage.create(telephone, "", txt);
		try {
			//设置接收状态报告
			msg.setRegisteredDelivery((short)1);
			
			BaseMessage response = smsClient.send(msg);
			
			result.put("rtnCode", "0");
			result.put("rtnMsg", "success");
		}catch(Exception ex) {
			logger.error("http error.",ex);
			result.put("rtnCode", "-1");
			result.put("rtnMsg", ex.getMessage());
		}
		return  result;
	}
}
