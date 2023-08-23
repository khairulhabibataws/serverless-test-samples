package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.sdk.EdaClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnitTest {

    @Test
    public void testSendPayload(){
        EdaClient client = new EdaClient("xxx", "invoice", "https://d28zg3kr8whca8.cloudfront.net/customerCreated-v1.0.0.json");
        ObjectMapper mapper = new ObjectMapper();
        InputStream is1 = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("events/customerCreated-event-1.1.0.json");
        try {
            JsonNode payload = mapper.readTree(is1);
            System.out.println("payload >>>> " + payload.toString());
            client.send(payload);
        } catch (IOException | URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
    
}
