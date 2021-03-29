package com.lixw.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lixw.rest.core.DefaultResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author lixw
 * @date 2021/03/29
 */
public class HttpPostInvocation implements Invocation {

    private final URL url;
    private final MultivaluedMap<String, Object> headers;
    private final Entity entity;

    public HttpPostInvocation(URL url, MultivaluedMap<String, Object> headers, Entity entity) {
        this.url = url;
        this.headers = headers;
        this.entity = entity;
    }


    @Override
    public Invocation property(String s, Object o) {
        return this;
    }

    @Override
    public Response invoke() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
            connection.setDoOutput(true);
            setRequestParams(connection);
            int statusCode = connection.getResponseCode();
            DefaultResponse response = new DefaultResponse();
            response.setConnection(connection);
            response.setStatus(statusCode);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setRequestParams(HttpURLConnection connection) {
        for (Map.Entry<String, List<Object>> listEntry : headers.entrySet()) {
            final String entryKey = listEntry.getKey();
            for (Object o : listEntry.getValue()) {
                connection.setRequestProperty(entryKey, o.toString());
            }
        }
        try (final OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(entity.getEntity().toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T invoke(Class<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public <T> T invoke(GenericType<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }


    @Override
    public Future<Response> submit() {
        return null;
    }

    @Override
    public <T> Future<T> submit(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> Future<T> submit(GenericType<T> genericType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(InvocationCallback<T> invocationCallback) {
        return null;
    }
}
