package com.lixw.rest.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.MediaType;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpURLConnectionDemo {

    public static void main(String[] args) throws Throwable {
        URI uri = new URI("http://localhost:8080/api/login");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
        connection.setDoOutput(true);
        Map<String, String> map = new HashMap<>();
        map.put("username", "12345");
        map.put("password", "e10adc3949ba59abbe56e057f20f883e");
        String body = new ObjectMapper().writeValueAsString(map);
        try (final OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body.getBytes(StandardCharsets.UTF_8));
        }
        try (InputStream inputStream = connection.getInputStream()) {
            System.out.println(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        }
        // 关闭连接
        connection.disconnect();
    }
}

