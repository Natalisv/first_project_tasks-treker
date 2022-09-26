package Test.file;

import Manager.File.FileBackedTasksManager;
import Manager.Exception.ManagerSaveException;
import Test.TaskManagerTest;
import Tasks.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    protected File file;

    @Override
    public FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(file);
    }


    @Test
    void shouldSaveToFileAndRead() throws IOException, ManagerSaveException {
        taskManager.addTask(task);

        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);
        taskManager.setEpicTime(epic);
        taskManager.save();


        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(new File("file.csv"));
        List<Task> listOfTask = newManager.getTasks();
        List<Epic> listOfEpic = newManager.getEpics();
        List<Subtask> listOfSubtask = newManager.getSubtasks();

        Task savedTask = listOfTask.get(0);
        assertNotNull(listOfTask, "Задача не записалась в файл");
        assertEquals(1, listOfTask.size());
        Assertions.assertEquals(task, savedTask, "Сохраненная в файл задача и прочтенная задача не совпадают");

        Epic savedEpic = listOfEpic.get(0);
        assertNotNull(listOfEpic, "Эпик не записался в файл");
        assertEquals(1, listOfEpic.size());
        Assertions.assertEquals(epic, savedEpic, "Сохраненный в файл эпик и прочтенный эпик не совпадают");

        Subtask savedSubtask = listOfSubtask.get(0);
        assertNotNull(listOfSubtask, "Сабтаск не записался в файл");
        assertEquals(1, listOfSubtask.size());
        Assertions.assertEquals(subtask, savedSubtask, "Сохраненный в файл сабтаск и прочтенный сабтаск не совпадают");
    }

    @Test
    void shouldSaveHistoryToFileAndRead()  throws IOException {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        List<Tasks> savedListOfHistory = taskManager.getHistory();

        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(new File("file.csv"));

        List<Tasks> readListOfHistory = newManager.getHistory();
        assertNotNull(readListOfHistory, "История не сохранилась");
        assertEquals(2,readListOfHistory.size());

        assertArrayEquals(savedListOfHistory.toArray(new Tasks[2]), readListOfHistory.toArray(new Tasks[2]), "Сохраненная история и причитанная не совпадают");
    }

    // перед запуском теста проверки сохранения пустого файла надо очистить файл "file.csv" от информации из предыдущих тестов, я не смогла это сделать в коде
    @Test
    void shouldSaveEmptyTaskAndHistory() throws IOException, ManagerSaveException {
        taskManager.save();
        taskManager.getEpicById(1);

        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(new File("file.csv"));
        List<Task> listOfTask = newManager.getTasks();
        List<Tasks> listOfHistory = newManager.getHistory();

        assertEquals(0, listOfTask.size(), "Должен возвращаться пустой список");
        assertEquals(0, listOfHistory.size(), "Должна возращаться пустая история");

    }

}