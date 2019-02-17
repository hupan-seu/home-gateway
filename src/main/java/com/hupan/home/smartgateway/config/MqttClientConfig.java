package com.hupan.home.smartgateway.config;

import com.hupan.home.smartgateway.constant.MqttConstant;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.UUID;

@Configuration
public class MqttClientConfig {
    @Value("${smartgateway.mqtt.server.url}")
    private String serverUrl;

    @Value("${smartgateway.mqtt.client.client-id}")
    private String clientId;

    @Value("${smartgateway.mqtt.client.username}")
    private String username;

    @Value("${smartgateway.mqtt.client.password}")
    private String password;

    /**
     * mqtt 连接配置
     * */
    @Bean
    public MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{serverUrl});
        mqttConnectOptions.setKeepAliveInterval(30);
        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    /**
     * mqtt 发送通道
     * */
//    @Bean
//    @ServiceActivator(inputChannel = "mqttOutboundChannel")
//    public MessageHandler mqttOutbound() {
//        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "1", mqttClientFactory());
//        messageHandler.setAsync(true);
//        messageHandler.setDefaultTopic("topic");
//        return messageHandler;
//    }
//
//    @Bean
//    public MessageChannel mqttOutboundChannel() {
//        return new DirectChannel();
//    }

    /**
     * mqtt 接收通道
     * */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "2", mqttClientFactory(), MqttConstant.TOPIC_SUBSCRIBE);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
}
