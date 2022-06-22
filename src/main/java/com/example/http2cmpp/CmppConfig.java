package com.example.http2cmpp;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinamobile.cmos.MessageReceiver;
import com.chinamobile.cmos.SmsClient;
import com.chinamobile.cmos.SmsClientBuilder;
import com.zx.sms.BaseMessage;
import com.zx.sms.connect.manager.EndpointEntity.ChannelType;
import com.zx.sms.connect.manager.cmpp.CMPPClientEndpointEntity;

@Configuration
public class CmppConfig {
	@Value("${cmpp.server}")
	private String server;


	@Bean
	public SmsClient build() {
		URI uri = URI.create(server);
		String protocol = uri.getScheme();
		Map<String, String> queryMap = queryToMap(uri.getQuery());
		CMPPClientEndpointEntity client = buildClient(queryMap);
		String proxy = queryMap.get("proxy");
		client.setId("c-SmsClient");
		client.setHost(uri.getHost());
		client.setPort(uri.getPort());
		client.setValid(true);
		client.setChannelType(ChannelType.DUPLEX);
		client.setMaxChannels((short) 1);
		client.setProxy(proxy);
		
		SmsClientBuilder builder = new SmsClientBuilder();
		
		SmsClient smsClient = builder.entity(client).keepAllIdleConnection() // 保持空闲连接，以便能接收上行或者状态报告消息
				.window(32) // 设置发送窗口
				.receiver(new MessageReceiver() {

					public void receive(BaseMessage message) {
//						logger.info("receive : {}", message.toString());
					}
				}).build();
		return smsClient;
	}
	
	private static Map<String, String> queryToMap(String query) {
		if (StringUtils.isBlank(query))
			return null;
		Map<String, String> result = new HashMap();
		String[] parameters = query.split("&");
		if (parameters.length > 0) {
			for (String pairs : parameters) {
				if (StringUtils.isBlank(pairs))
					continue;
				String[] kv = pairs.split("=");
				if (kv.length > 1) {
					result.put(kv[0], kv[1]);
				} else {
					result.put(kv[0], "");
				}
			}
		}
		return result;
	}
	private  CMPPClientEndpointEntity  buildClient(Map<String,String> queryMap) {
    	CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
        String userName = queryMap.get("username");
        String pass = queryMap.get("password");
        String version = queryMap.get("version");
        String spcode = queryMap.get("spcode");
        String msgsrc = queryMap.get("msgsrc");
        String serviceid = queryMap.get("serviceid");
        
        client.setPassword(pass);
        client.setUserName(userName);
        client.setVersion(Integer.valueOf(version).shortValue());
        client.setSpCode(spcode);
        client.setMsgSrc(msgsrc);
        client.setServiceId(serviceid);
        return client;
 }
}
