package com.bigapps.mindit;

import com.hubtel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class Consumer {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    @Value("${hubtel.merchantId}")
    String merchantId;
    @Value("${hubtel.merchantSecret}")
    String merchantSecret;


    @RabbitListener(queues = "mindit_corona")
    public void postUssdService(String  mobile){

        try {
        sendSms(mobile);
        }catch (Exception e){
            logger.error(e.getMessage());
        }

    }



    public void sendSms(String mobile){
        BasicAuth auth = new BasicAuth(merchantId,  merchantSecret);
        ApiHost host = new ApiHost(auth);

        // Instance of the Messaging API
        MessagingApi messagingApi = new MessagingApi(host);
        final MessageResponse[] response = {null};

            try {
                Message message = new Message();
                message.setContent("Emergency Contact for COVID-19.\n 0552222004, 0552222005, 0509497700, 0558439868. \n There is help Available.");
                message.setFrom("iMind");
                message.setTo(mobile);
                message.setRegisteredDelivery(true);
                response[0] = messagingApi.sendMessage(message);
                System.out.println("Response from Hubtel status: " + response[0].getStatus()+" details: "+ response[0].getDetail());
            } catch (HttpRequestException ex) {
                System.out.println("Exception Server Response Status " + ex.getHttpResponse().getStatus());
                System.out.println("Exception Server Response Body " + ex.getHttpResponse().getBodyAsString());
            }

    }



}
