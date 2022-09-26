package Manager.HTTP;

import HTTP.KVTaskClient;
import Manager.File.FileBackedTasksManager;
import Manager.History.HistoryManager;
import Manager.Exception.ManagerSaveException;
import Manager.Managers;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    String url;
    KVTaskClient client;
    Gson gson;

    public HTTPTaskManager(String port) throws ManagerSaveException {
        super();
        this.url = "http://localhost:" + port + "/";
        client = new KVTaskClient("8078");
        this.gson = Managers.getGson();
    }

    @Override
    public void save() {
        try {
            String jsonTasks = gson.toJson(getTasks());
            client.put("tasks", jsonTasks);

            String jsonEpics = gson.toJson(getEpics());
            client.put("epics", jsonEpics);

            String jsonSubtasks = gson.toJson(getSubtasks());
            client.put("subtasks", jsonSubtasks);

            String jsonHistory = gson.toJson(getHistory());
            client.put("history", jsonHistory);
        } catch(ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    public void load() {
        try {
            HistoryManager historyManager = Managers.getDefaultHistory();
            List<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>(){}.getType());

            List<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>(){}.getType());

            List<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>(){}.getType());

            List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>(){}.getType());
            for(Integer id : history) {
                if(getIdTask().contains(id)) {
                    historyManager.addTask(getTaskById(id));
                } else if(getIdEpic().contains(id)){
                    historyManager.addTask(getEpicById(id));
                } else {
                    historyManager.addTask(getSubtaskById(id));
                }
            }


        } catch(ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
}
