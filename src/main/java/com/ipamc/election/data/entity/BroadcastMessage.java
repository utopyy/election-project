package com.ipamc.election.data.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ipamc.election.data.BroadcastMessageType;

public class BroadcastMessage implements Serializable {

    private static final long serialVersionUID = 5637577096751222106L;

    public BroadcastMessageType messageType;
    public Map<String, String> params;

    public BroadcastMessage() {
    }

    public BroadcastMessage(BroadcastMessageType messageType) {
        this.messageType = messageType;
        this.params = new HashMap<String, String>();
    }

    public BroadcastMessage(BroadcastMessageType messageType, Map<String, String> params) {
        this.messageType = messageType;
        this.params = params;
    }

    public BroadcastMessageType getMessageType() {
        return messageType;
    }
    public void setMessageType(BroadcastMessageType messageType) {
        this.messageType = messageType;
    }
    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    
    

}
