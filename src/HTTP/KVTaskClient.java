package HTTP;

import Manager.Exception.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String url;
    private final String apiToken;

    public  KVTaskClient(String port) throws ManagerSaveException {
        this.url = "http://localhost:" + port + "/";
        this.apiToken = register(url);
    }

    public String register(String url) throws ManagerSaveException {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url + "register"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200) {
                throw new ManagerSaveException("Невозможно сохранить запрос");
            }
            return response.body();
        } catch(IOException | InterruptedException e) {
            throw new ManagerSaveException("Невозможно сохранить запрос");
        }
    }

    public void put(String key, String json) throws ManagerSaveException {
        try {
            URI uri = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(uri)
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.	discarding());
            if(response.statusCode() != 200) {
                throw new ManagerSaveException("Невозможно сохранить запрос");
            }

        } catch(IOException | InterruptedException e) {
            throw new ManagerSaveException("Невозможно сохранить запрос");
        }
    }

    public String load(String key) throws ManagerSaveException {
        try {
            URI uri = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!= 200) {
                throw new ManagerSaveException("Невозможно обработать запрос");
            } else {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Невозможно сохранить запрос");
        }
    }
}
