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

public class RecipePostTest {

    private static final String BASE_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/rezepte";
    private static final String KATEGORIEN_URL =
            "http://localhost:8080/LB295JolaMali_war_exploded/api/kategorien";
    private static final String AUTH_ADMIN =
            "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes());
    private static final String AUTH_USER =
            "Basic " + Base64.getEncoder().encodeToString("user:user123".getBytes());

    private int kategorieId;

    @BeforeEach
    public void setUp() throws Exception {
        HttpPost katPost = new HttpPost(KATEGORIEN_URL);
        katPost.setHeader("Authorization", AUTH_ADMIN);
        katPost.setHeader("Content-Type", "application/json");
        katPost.setEntity(new StringEntity("{\"name\":\"TestKategorie\"}"));
        String katResult = EntityUtils.toString(HttpClientBuilder.create().build().execute(katPost).getEntity());
        int s = katResult.indexOf("\"kategorieId\":") + 14;
        int e = katResult.indexOf(",", s);
        if (e == -1) e = katResult.indexOf("}", s);
        kategorieId = Integer.parseInt(katResult.substring(s, e).trim());
    }

    @Test
    public void createPositiv() throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Authorization", AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"Pasta\",\"zubereitungszeit\":20,\"vegetarisch\":true,\"bewertung\":4.5,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieId + "}}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("POST create: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void createNegativKeinName() throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Authorization", AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"\",\"zubereitungszeit\":20,\"vegetarisch\":true,\"bewertung\":4.5,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieId + "}}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("POST kein Name: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void createNegativZubereitungszeitNull() throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Authorization", AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"TestRezept\",\"zubereitungszeit\":0,\"vegetarisch\":true,\"bewertung\":4.5,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieId + "}}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("POST zubereitungszeit=0: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void createNegativBewertungZuHoch() throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Authorization", AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"TestRezept\",\"zubereitungszeit\":10,\"vegetarisch\":true,\"bewertung\":6.0,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieId + "}}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("POST bewertung=6.0: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void createNegativUnauthorized() throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"name\":\"Pasta\",\"zubereitungszeit\":20,\"vegetarisch\":true,\"bewertung\":4.5,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieId + "}}"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("POST unauthorized: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void createBatchPositiv() throws Exception {
        HttpPost request = new HttpPost(BASE_URL + "/batch");
        request.setHeader("Authorization", AUTH_USER);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("[{\"name\":\"Batch1\",\"zubereitungszeit\":10,\"vegetarisch\":true,\"bewertung\":3.0,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieId + "}},{\"name\":\"Batch2\",\"zubereitungszeit\":15,\"vegetarisch\":false,\"bewertung\":4.0,\"erstelltAm\":\"2026-01-01T10:00:00\",\"kategorie\":{\"kategorieId\":" + kategorieId + "}}]"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("POST batch: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void initPositiv() throws Exception {
        HttpPost request = new HttpPost(BASE_URL + "/init");
        request.setHeader("Authorization", AUTH_ADMIN);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println("POST init: " + EntityUtils.toString(response.getEntity()));
        assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
    }
}
