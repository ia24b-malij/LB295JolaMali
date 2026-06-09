package org.example.lb295.services;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryPutTest {

    private static final String BASE_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/kategorien";
    private static final String AUTH_ADMIN =
            "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes());
    private static final String AUTH_USER =
            "Basic " + Base64.getEncoder().encodeToString("user:user123".getBytes());

    private int kategorieId;

    @BeforeEach
    public void setUp() throws Exception {
        HttpPost post = new HttpPost(BASE_URL);
        post.setHeader("Authorization", AUTH_ADMIN);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity("{\"name\":\"TestKategorie\"}"));
        String result = EntityUtils.toString(HttpClientBuilder.create().build().execute(post).getEntity());
        int s = result.indexOf("\"kategorieId\":") + 14;
        int e = result.indexOf(",", s);
        if (e == -1) e = result.indexOf("}", s);
        kategorieId = Integer.parseInt(result.substring(s, e).trim());
    }

    @Test
    public void updatePositiv() throws Exception {
        HttpPut request = new HttpPut(BASE_URL + "/" + kategorieId);
        request.setHeader("Authorization", AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"UpdatedKategorie\"}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("PUT update ID " + kategorieId + ": " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void updateNegativNichtGefunden() throws Exception {
        HttpPut request = new HttpPut(BASE_URL + "/99999");
        request.setHeader("Authorization", AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"UpdatedKategorie\"}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("PUT update ID 99999: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    public void updateNegativKeinName() throws Exception {
        HttpPut request = new HttpPut(BASE_URL + "/" + kategorieId);
        request.setHeader("Authorization", AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"\"}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("PUT kein Name: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void updateNegativUnauthorized() throws Exception {
        HttpPut request = new HttpPut(BASE_URL + "/" + kategorieId);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"UpdatedKategorie\"}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("PUT unauthorized: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }
}
