package com.lixw.rest.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lixw.rest.client.DefaultClientBuilder;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lixw
 * @date 2021/03/29
 */
public class RestClientDemo {

    public static void main(String[] args) throws Throwable{
        //javax.ws.rs.client.ClientBuilder
        Client client = ClientBuilder.newClient();

        doPost(client);
    }

    @Test
    public void test() throws JsonProcessingException {
        //com.lixw.rest.client.DefaultClientBuilder
        Client client = new DefaultClientBuilder().build();

        doPost(client);
    }

    private static void doPost(Client client) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("username", "12345");
        map.put("password", "e10adc3949ba59abbe56e057f20f883e");
        String body = new ObjectMapper().writeValueAsString(map);

        String response = client
                .target("http://localhost:8080/api/login")
                .request()
                .post(Entity.entity(body, MediaType.APPLICATION_JSON), String.class);
        System.out.println("response = " + response);
    }
}
