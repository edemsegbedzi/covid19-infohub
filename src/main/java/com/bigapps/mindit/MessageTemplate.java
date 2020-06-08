package com.bigapps.mindit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageTemplate {
    String from,to,content,RegisteredDelivery,Time;


    @JsonProperty("From")
    public String getFrom() {
        return from;
    }

    @JsonProperty("To")
    public String getTo() {
        return to;
    }

    @JsonProperty("Content")
    public String getContent() {
        return content;
    }

    @JsonProperty("RegisterDelivery")
    public String getRegisteredDelivery() {
        return RegisteredDelivery;
    }
    @JsonProperty("Time")
    public String getTime() {
        return Time;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRegisteredDelivery(String registeredDelivery) {
        RegisteredDelivery = registeredDelivery;
    }

    public void setTime(String time) {
        Time = time;
    }
}
