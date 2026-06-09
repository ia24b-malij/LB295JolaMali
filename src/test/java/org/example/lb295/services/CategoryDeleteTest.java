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

public class CategoryDeleteTest {

    private static final String BASE_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/kategorien";
    private static final String REZEPTE_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/rezepte";
    private static final String AUTH_ADMIN =
            "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes(StandardCharsets.ISO_8859_1));
    private static final String AUTH_USER =
            "Basic " + Base64.getEncoder().encodeToString("user:user123".getBytes(StandardCharsets.ISO_8859_1));

    private int kategorieId;
    private int kategorieWithRezeptId;

    @BeforeEach
    public void setUp() throws Exception {
        // Kategorie without recipes (for successful delete)
        HttpPost post1 = new HttpPost(BASE_URL);
        post1.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        post1.setHeader("Content-Type", "application/json");
        post1.setEntity(new StringEntity("{\"name\":\"LeereKategorie\"}"));
        String result1 = EntityUtils.toString(HttpClientBuilder.create().build().execute(post1).getEntity());
        int s1 = result1.indexOf("\"kategorieId\":") + 14;
        int e1 = result1.indexOf(",", s1);
        if (e1 == -1) e1 = result1.indexOf("}", s1);
        kategorieId = Integer.parseInt(result1.substring(s1, e1).trim());

        // Kategorie with a recipe (for conflict test)
        HttpPost post2 = new HttpPost(BASE_URL);
        post2.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        post2.setHeader("Content-Type", "application/json");
        post2.setEntity(new StringEntity("{\"name\":\"KategorieWithRezept\"}"));
        String result2 = EntityUtils.toString(HttpClientBuilder.create().build().execute(post2).getEntity());
        int s2 = result2.indexOf("\"kategorieId\":") + 14;
        int e2 = result2.indexOf(",", s2);
        if (e2 == -1) e2 = result2.indexOf("}", s2);
        kategorieWithRezeptId = Integer.parseInt(result2.substring(s2, e2).trim());

        HttpPost rezPost = new HttpPost(REZEPTE_URL);
        rezPost.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        rezPost.setHeader("Content-Type", "application/json");
        rezPost.setEntity(new StringEntity("{\"name\":\"TestRezept\",\"zubereitungszeit\":10,\"vegetarisch\":true,\"bewertung\":4.0,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieWithRezeptId + "}}"));
        HttpClientBuilder.create().build().execute(rezPost);
    }

    @Test
    public void deletePositiv() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/" + kategorieId);
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE Kategorie ID " + kategorieId + ": " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteNegativHatRezepte() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/" + kategorieWithRezeptId);
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE Kategorie mit Rezepten: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_CONFLICT, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteNegativNichtGefunden() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/99999");
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_ADMIN);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE ID 99999: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_CONFLICT, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteNegativForbidden() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/" + kategorieId);
        request.setHeader(HttpHeaders.AUTHORIZATION, AUTH_USER);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE forbidden (user role): " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteNegativUnauthorized() throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/" + kategorieId);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(request);
            System.out.println("DELETE unauthorized: " + EntityUtils.toString(response.getEntity()));
            assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
        }
    }
}
