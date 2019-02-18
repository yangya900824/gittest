package com.yangya.controller;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yangya.comsumer.MsgDate;

/**
 * 消息接口.
 */
@RestController
@RequestMapping("/rabbit")
public class SendController {
	@Resource
	private RabbitTemplate rabbitTemplate;

	/**
	 * 测试死信队列.
	 */
	@RequestMapping("/dead")
	public void deadLetter(String p) {
		MsgDate msg = new MsgDate();
/*		if (msg.getCont() <= 5) {
			CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
			// 声明消息处理器 这个对消息进行处理 可以设置一些参数 对消息进行一些定制化处理 我们这里 来设置消息的编码 以及消息的过期时间
			// 因为在.net 以及其他版本过期时间不一致 这里的时间毫秒值 为字符串
			MessagePostProcessor messagePostProcessor = message -> {
				MessageProperties messageProperties = message.getMessageProperties();
				// 设置编码
				messageProperties.setContentEncoding("utf-8");
				// 设置过期时间10*1000毫秒
				messageProperties.setExpiration(msg.getTime()*msg.getCont()+"");
				return message;
			};
			rabbitTemplate.convertAndSend("DL_EXCHANGE", "DL_KEY", JSONObject.toJSONString(msg), messagePostProcessor, correlationData);
		}
*/		// 向DL_QUEUE 发送消息 10*1000毫秒后过期 形成死信
		// rabbitTemplate.convertAndSend("DL_EXCHANGE", "DL_KEY", p,
		// messagePostProcessor, correlationData);
		rabbitTemplate.convertAndSend("REDIRECT_QUEUE", JSONObject.toJSONString(msg));
	}

}
