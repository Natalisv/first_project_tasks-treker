package Manager;

import Manager.Adapter.DurationAdapter;
import Manager.Adapter.LocalDateTimeAdapter;
import Manager.Exception.ManagerSaveException;
import Manager.File.FileBackedTasksManager;
import Manager.HTTP.HTTPTaskManager;
import Manager.History.HistoryManager;
import Manager.History.InMemoryHistoryManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static HTTPTaskManager getDefault() throws ManagerSaveException {
        return new HTTPTaskManager("8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static TaskManager getDefaultFile() {
        return new FileBackedTasksManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
       return gsonBuilder.create();
    }
}

