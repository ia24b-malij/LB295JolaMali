package org.example.lb295.services;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryPostTest {

    private static final String BASE_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/kategorien";
    private static final String AUTH_ADMIN =
            "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes(StandardCharsets.ISO_8859_1));
    private static final String AUTH_USER =
            "Basic " + Base64.getEncoder().encodeToString("user:user123".getBytes(StandardCharsets.ISO_8859_1));

    @Test
    public void createPositiv() throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"NeueKategorie\"}"));
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("POST create: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void createNegativKeinName() throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"\"}"));
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("POST kein Name: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void createNegativUnauthorized() throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"NeueKategorie\"}"));
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("POST unauthorized: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
        }
    }
}
