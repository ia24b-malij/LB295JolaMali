package org.example.lb295.services;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryGetTest {

    private static final String BASE_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/kategorien";
    private static final String AUTH_ADMIN =
            "Basic " + Base64.getEncoder().encodeToString("admin:1234".getBytes(StandardCharsets.ISO_8859_1));

    @BeforeEach
    public void setUp() throws Exception {
        HttpPost post = new HttpPost(BASE_URL);
        post.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity("{\"name\":\"TestKategorie\"}"));
        HttpClientBuilder.create().build().execute(post);
    }

    @Test
    public void getAllePositiv() throws Exception {
        HttpGet request = new HttpGet(BASE_URL);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET alle Kategorien: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }
}
