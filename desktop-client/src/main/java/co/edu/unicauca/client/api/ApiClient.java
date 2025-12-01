package co.edu.unicauca.client.api;

import co.edu.unicauca.client.Session;
import co.edu.unicauca.client.dto.AuthRequest;
import co.edu.unicauca.client.dto.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private static final String USER_API = "http://localhost:8081";
    private static final String PROJECT_API = "http://localhost:8082/api/v1/proyectos";

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static AuthResponse login(String email, String password) throws Exception {
        URI uri = URI.create(USER_API + "/api/auth/login");
        AuthRequest req = new AuthRequest(email, password);
        String body = mapper.writeValueAsString(req);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readValue(response.body(), AuthResponse.class);
        } else {
            throw new RuntimeException("Error de login: " + response.statusCode());
        }
    }

    public static String getProjectsForStudent(String email) throws Exception {
        URI uri = URI.create(PROJECT_API + "/estudiante/" + email);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer " + Session.getToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new RuntimeException("Error al obtener proyectos: " + response.statusCode());
        }
    }

    // Se añadirá: subirFormatoA, evaluar, asignarEvaluadores, etc.
}
