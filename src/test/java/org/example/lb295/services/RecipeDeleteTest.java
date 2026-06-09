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

public class RecipeDeleteTest {

    private static final String BASE_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/rezepte";
    private static final String KATEGORIEN_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/kategorien";
    private static final String AUTH_ADMIN =
            "Basic " + Base64.getEncoder().encodeToString("admin:1234".getBytes(StandardCharsets.ISO_8859_1));
    private static final String AUTH_USER =
            "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes(StandardCharsets.ISO_8859_1));

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
    public void deletePositiv() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/" + rezeptId);
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE ID " + rezeptId + ": " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteNegativNichtGefunden() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/99999");
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE ID 99999: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteNegativForbidden() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/" + rezeptId);
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_USER);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE forbidden (user role): " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteNegativUnauthorized() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/" + rezeptId);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE unauthorized: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteVorDatumPositiv() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/vor/2027-01-01T00:00:00");
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE vor 2027-01-01: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }
}
