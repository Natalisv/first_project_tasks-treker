package HTTP;

import Manager.Managers;
import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson;
    private final HttpServer server;
    private final TaskManager taskManager = Managers.getDefaultFile();

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
        gson = Managers.getGson();
    }

    public static void main(String[] args) throws IOException {

        final HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public void handler(HttpExchange h) {
        try (h) {
            String response;
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath().split("/")[2];

            switch (path) {
                case "" -> {
                    if (method.equals("GET")) {
                        response = gson.toJson(taskManager.getPrioritizedTasks());
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else {
                        System.out.println("Нужно отправить GET запрос");
                        h.sendResponseHeaders(405, 0);
                    }
                }
                case "history" -> {
                    if (method.equals("GET")) {
                        response = gson.toJson(taskManager.getHistory());
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else {
                        System.out.println("Нужно отправить GET запрос");
                        h.sendResponseHeaders(405, 0);
                    }
                }
                case "task" -> handlerTask(h);
                case "subtask" -> handlerSubtask(h);
                case "epic" -> handlerEpic(h);
                default -> {
                    System.out.println("Не верный запрос");
                    response = "";
                    h.sendResponseHeaders(404, 0);
                    sendResponse(h, response);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendResponse(HttpExchange h, String response) throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public void handlerTask(HttpExchange h) {
        try (h) {
            String response;
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath().substring(7);
            switch (method) {
                case "GET" -> {
                    if (path.contains("?id=")) {
                        int id = Integer.parseInt(path.substring(4));
                        response = gson.toJson(taskManager.getTaskById(id));
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else if (path != null) {
                        response = gson.toJson(taskManager.getTasks());
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else {
                        System.out.println("Введен не верный запрос");
                        h.sendResponseHeaders(404, 0);
                    }
                }
                case "POST" -> {
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    if (!body.isEmpty()) {
                        Task task = gson.fromJson(body, Task.class);
                        Integer id = task.getId();
                        if (id != null) {
                            taskManager.updateTask(task);
                            response = "Задача обновлена";
                            h.sendResponseHeaders(200, 0);
                            sendResponse(h, response);
                        } else {
                            taskManager.addTask(task);
                            response = "Задача создана";
                            h.sendResponseHeaders(200, 0);
                            sendResponse(h, response);
                        }
                    } else {
                        System.out.println("Необходимо передать в теле запроса задачу");
                        h.sendResponseHeaders(400, 0);
                    }
                }
                case "DELETE" -> {
                    if (path.startsWith("?id=")) {
                        int id = Integer.parseInt(path.substring(4));
                        taskManager.deleteTaskById(id);
                        response = "Задача удалена";
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else if (path == null) {
                        taskManager.deleteTasks();
                        response = "Задачи удалены";
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else {
                        System.out.println("Введен не верный запрос");
                        h.sendResponseHeaders(404, 0);
                    }
                }
                default -> {
                    System.out.println("Нужно передать GET, POST или DELETE метод");
                    response = "";
                    h.sendResponseHeaders(404, 0);
                    sendResponse(h, response);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void handlerSubtask(HttpExchange h) {
        try (h) {
            String response;
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath().substring(7);
            switch (method) {
                case "GET" -> {
                    if (path.contains("?id=")) {
                        int id = Integer.parseInt(path.substring(4));
                        response = gson.toJson(taskManager.getSubtaskById(id));
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else if (path != null) {
                        response = gson.toJson(taskManager.getSubtasks());
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else if(path.equals("epic")) {
                        String newPath = h.getRequestURI().getPath().split("/")[4];
                        int idOfEpic = Integer.parseInt(newPath.substring(4));
                        response = gson.toJson(taskManager.getEpicSubtasks(idOfEpic));
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else {
                        System.out.println("Введен не верный запрос");
                        h.sendResponseHeaders(404, 0);
                    }
                }
                case "POST" -> {
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    if (!body.isEmpty()) {
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        Integer id = subtask.getId();
                        if (id != null) {
                            taskManager.updateSubtask(subtask);
                            response = "Эпик обновлен";
                            h.sendResponseHeaders(200, 0);
                            sendResponse(h, response);
                        } else {
                            taskManager.addSubtask(subtask);
                            response = "Эпик создан";
                            h.sendResponseHeaders(200, 0);
                            sendResponse(h, response);
                        }
                    } else {
                        System.out.println("Необходимо передать в теле запроса подзадачу");
                        h.sendResponseHeaders(400, 0);
                    }
                }
                case "DELETE" -> {
                    if (path.startsWith("?id=")) {
                        int id = Integer.parseInt(path.substring(4));
                        taskManager.deleteSubtaskById(id);
                        response = "Эпик удален";
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else if (path == null) {
                        taskManager.deleteSubtasks();
                        response = "Эпики удалены";
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else {
                        System.out.println("Введен не верный запрос");
                        h.sendResponseHeaders(404, 0);
                    }
                }
                default -> {
                    System.out.println("Нужно передать GET, POST или DELETE метод");
                    response = "";
                    h.sendResponseHeaders(404, 0);
                    sendResponse(h, response);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void handlerEpic(HttpExchange h) {
        try (h) {
            String response;
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath().substring(7);
            switch (method) {
                case "GET" -> {
                    if (path.contains("?id=")) {
                        int id = Integer.parseInt(path.substring(4));
                        response = gson.toJson(taskManager.getEpicById(id));
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else if (path != null) {
                        response = gson.toJson(taskManager.getEpics());
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else {
                        System.out.println("Введен не верный запрос");
                        h.sendResponseHeaders(404, 0);
                    }
                }
                case "POST" -> {
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    if (!body.isEmpty()) {
                        Epic epic = gson.fromJson(body, Epic.class);
                        Integer id = epic.getId();
                        if (id != null) {
                            taskManager.updateEpic(epic);
                            response = "Подзадача обновлена";
                            h.sendResponseHeaders(200, 0);
                            sendResponse(h, response);
                        } else {
                            taskManager.addEpic(epic);
                            response = "Подзадача добавлена";
                            h.sendResponseHeaders(200, 0);
                            sendResponse(h, response);
                        }
                    } else {
                        System.out.println("Необходимо передать в теле запроса эпик");
                        h.sendResponseHeaders(400, 0);
                    }
                }
                case "DELETE" -> {
                    if (path.startsWith("?id=")) {
                        int id = Integer.parseInt(path.substring(4));
                        taskManager.deleteEpicById(id);
                        response = "Подзадача удалена";
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else if (path == null) {
                        taskManager.deleteEpics();
                        response = "Все подзадачи удалены";
                        h.sendResponseHeaders(200, 0);
                        sendResponse(h, response);
                    } else {
                        System.out.println("Введен не верный запрос");
                        h.sendResponseHeaders(404, 0);
                    }
                }
                default -> {
                    System.out.println("Нужно передать GET, POST или DELETE метод");
                    response = "";
                    h.sendResponseHeaders(404, 0);
                    sendResponse(h, response);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void start() {
        this.server.start();
    }
}
