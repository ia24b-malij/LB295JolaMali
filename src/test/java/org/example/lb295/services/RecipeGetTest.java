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

public class RecipeGetTest {

    private static final String BASE_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/rezepte";
    private static final String KATEGORIEN_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/kategorien";
    private static final String AUTH_ADMIN =
            "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes(StandardCharsets.ISO_8859_1));

    private int kategorieId;
    private int rezeptId;

    @BeforeEach
    public void setUp() throws Exception {
        HttpPost katPost = new HttpPost(KATEGORIEN_URL);
        katPost.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        katPost.setHeader("Content-Type", "application/json");
        katPost.setEntity(new StringEntity("{\"name\":\"TestKategorie\"}"));
        String katResult = EntityUtils.toString(HttpClientBuilder.create().build().execute(katPost).getEntity());
        int s = katResult.indexOf("\"kategorieId\":") + 14;
        int e = katResult.indexOf(",", s);
        if (e == -1) e = katResult.indexOf("}", s);
        kategorieId = Integer.parseInt(katResult.substring(s, e).trim());

        HttpPost rezPost = new HttpPost(BASE_URL);
        rezPost.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        rezPost.setHeader("Content-Type", "application/json");
        rezPost.setEntity(new StringEntity("{\"name\":\"SetupRezept\",\"zubereitungszeit\":10,\"vegetarisch\":true,\"bewertung\":4.0,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieId + "}}"));
        String rezResult = EntityUtils.toString(HttpClientBuilder.create().build().execute(rezPost).getEntity());
        int rs = rezResult.indexOf("\"rezeptId\":") + 11;
        int re = rezResult.indexOf(",", rs);
        rezeptId = Integer.parseInt(rezResult.substring(rs, re).trim());
    }

    @Test
    public void pingPositiv() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/ping");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET ping: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getAllePositiv() throws Exception {
        HttpGet request = new HttpGet(BASE_URL);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET alle: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getByIdPositiv() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/" + rezeptId);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET by ID " + rezeptId + ": " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getByIdNegativ() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/99999");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET by ID 99999: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getCountPositiv() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/count");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET count: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getFilterVegetarischPositiv() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/filter?vegetarisch=true");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET filter vegetarisch=true: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getFilterMinBewertungPositiv() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/filter?minBewertung=4.0");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET filter minBewertung=4.0: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getFilterNegativKeinParameter() throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/filter");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("GET filter ohne Parameter: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        }
    }
}
