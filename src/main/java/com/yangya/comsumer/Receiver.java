package com.yangya.comsumer;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Resource;

/**
 * 监听器.
 */
@Component
public class Receiver {
    private static final Logger log= LoggerFactory.getLogger(Receiver.class);
    
    @Resource
	private RabbitTemplate rabbitTemplate;
    /**
     * 监听替补队列 来验证死信.
     *
     * @param message the message
     * @param channel the channel
     * @throws IOException the io exception  这里异常需要处理
     */
    @RabbitListener(queues = {"REDIRECT_QUEUE"})
    public void redirect(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        String string = new String (message.getBody());
        System.out.println("dead message  10s 后 消费消息"+string);
        MsgDate msg = JSONObject.parseObject(string, MsgDate.class);
        if (msg.getCont() <= 5) {
        	msg.setCont(msg.getCont()+1);
			CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
			// 声明消息处理器 这个对消息进行处理 可以设置一些参数 对消息进行一些定制化处理 我们这里 来设置消息的编码 以及消息的过期时间
			// 因为在.net 以及其他版本过期时间不一致 这里的时间毫秒值 为字符串
			MessagePostProcessor messagePostProcessor = message2 -> {
				MessageProperties messageProperties = message2.getMessageProperties();
				// 设置编码
				messageProperties.setContentEncoding("utf-8");
				// 设置过期时间10*1000毫秒
				messageProperties.setExpiration(msg.getTime()*msg.getCont()+"");
				return message2;
			};
			msg.setTime(msg.getTime()*msg.getCont());
			rabbitTemplate.convertAndSend("DL_EXCHANGE", "DL_KEY", JSONObject.toJSONString(msg), messagePostProcessor, correlationData);
		}
    }
    
}
