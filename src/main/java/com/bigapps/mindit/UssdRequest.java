package com.bigapps.mindit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 *
 * This class serves as a facade to the ussd Request recieved from SMSGH (now HUBTEL).
 * It is used by the spring framework to map each json object in the request to a java variable type .
 *
 * Created by edem on 1/31/17.
 */



@JsonIgnoreProperties(ignoreUnknown = true)
public class UssdRequest {

    private String sessionId;
    private Integer menu;
    private String mobileNetwork;
    private String message;
    private String msisdn;
    private String clientState;

    public String getSessionId() {
        return sessionId;
    }

    @JsonSetter
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMenu() {
        return menu;
    }

    @JsonSetter
    public void setMenu(Integer menu) {
        this.menu = menu;
    }

    public String getMobileNetwork() {
        return mobileNetwork;
    }

    @JsonSetter
    public void setMobileNetwork(String mobileNetwork) {
        this.mobileNetwork = mobileNetwork;
    }

    public String getMessage() {
        return message;
    }

    @JsonSetter
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsisdn() {
        return msisdn;
    }

    @JsonSetter
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getClientState() {
        return clientState;
    }

    @JsonSetter
    public void setClientState(String clientState) {
        this.clientState = clientState;
    }
}
