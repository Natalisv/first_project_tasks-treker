package Test.http;

import Server.KVServer;
import Manager.HTTP.HTTPTaskManager;
import Manager.Exception.ManagerSaveException;
import Test.TaskManagerTest;
import Tasks.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {


    @Override
    public HTTPTaskManager createTaskManager() throws ManagerSaveException {
        return new HTTPTaskManager("8078");
    }

    @BeforeEach
    void setTaskManager() throws ManagerSaveException {
        taskManager = createTaskManager();
    }

    @BeforeAll
    static void start() throws IOException, ManagerSaveException {
        new KVServer().start();
    }

    void shouldPutAndGetTasks() {
        taskManager.addTask(task);
        Task firstTask = new Task("Задача2", "Описание", StatusOfTasks.New, TypeOfTasks.TASK,  100);
        taskManager.addTask(firstTask);

        List<Task> loadTask = taskManager.getTasks();
        assertEquals(loadTask.size(), 2, "Задачи сохранены не верно");
    }

    @Test
    void shouldPutAndGetTaskById() {
        taskManager.addTask(task);
        int id = task.getId();
        Task savedTask = taskManager.getTaskById(id);

        Assertions.assertEquals(savedTask, task, "Задача не сохранена");
    }

    @Test
    void shouldDeleteTasks() {
        taskManager.addTask(task);
        Task firstTask = new Task("Задача2", "Описание", StatusOfTasks.New, TypeOfTasks.TASK,  100);
        taskManager.addTask(firstTask);

        taskManager.deleteTasks();
        List<Task> loadTask = taskManager.getTasks();
        assertTrue(loadTask.isEmpty(), "Задачи не удалены");
    }

    @Test
    void shouldDeleteTaskById() {
        taskManager.addTask(task);
        Task savedTask = task;
        int id = savedTask.getId();
        Task firstTask = new Task("Задача2", "Описание", StatusOfTasks.New, TypeOfTasks.TASK,  100);
        taskManager.addTask(firstTask);

        taskManager.deleteTaskById(id);
        List<Task> loadTask = taskManager.getTasks();
        assertEquals(loadTask.size(), 1);
        assertFalse(loadTask.contains(savedTask), "Задача не удалена");
    }

    @Test
    void shouldPutAndGetEpics() {
        taskManager.addEpic(epic);
        Epic firstEpic = new Epic("Эпик", "Без сабтасков", TypeOfTasks.EPIC);
        taskManager.addEpic(firstEpic);

        List<Epic> loadEpic = taskManager.getEpics();
        assertEquals(loadEpic.size(), 2, "Эпики сохранены не верно");
    }

    @Test
    void shouldPutAndGetEpicById() {
        taskManager.addEpic(epic);
        int id = epic.getId();
        Epic savedEpic = taskManager.getEpicById(id);

        Assertions.assertEquals(savedEpic, epic, "Эпик не сохранен");
    }

    @Test
    void shouldDeleteEpics() {
        taskManager.addEpic(epic);
        Epic firstEpic = new Epic("Эпик", "Без сабтасков", TypeOfTasks.EPIC);
        taskManager.addEpic(firstEpic);

        taskManager.deleteEpics();
        List<Epic> loadEpics = taskManager.getEpics();
        assertTrue(loadEpics.isEmpty(), "Эпики не удалены");
    }

    @Test
    void shouldDeleteEpicsById() {
        taskManager.addEpic(epic);
        Epic savedEpic = epic;
        int id = savedEpic.getId();
        Epic firstEpic = new Epic("Эпик", "Без сабтасков", TypeOfTasks.EPIC);
        taskManager.addEpic(firstEpic);

        taskManager.deleteEpicById(id);
        List<Epic> loadEpics = taskManager.getEpics();
        assertEquals(loadEpics.size(), 1);
        assertFalse(loadEpics.contains(savedEpic), "Эпик не удален");
    }

    @Test
    void shouldPutAndGetSubtasks() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100));
        taskManager.addSubtask(new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 150));

        List<Subtask> loadSubtasks = taskManager.getSubtasks();
        assertEquals(loadSubtasks.size(), 2, "Подзадачи сохранены не верно");
    }

    @Test
    void shouldPutAndGetSubtaskById() {
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);
        int id = subtask.getId();
        Subtask savedSubtask = taskManager.getSubtaskById(id);

        assertEquals(savedSubtask, savedSubtask, "Подзадача не сохранена");
    }

    @Test
    void shouldDeleteSubtasks() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100));
        taskManager.addSubtask(new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 150));

        taskManager.deleteSubtasks();
        List<Subtask> loadSubtask = taskManager.getSubtasks();
        assertTrue(loadSubtask.isEmpty(), "Подзадачи не удалены");
    }

    @Test
    void shouldDeleteSubtaskById() {
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 150));
        int id = subtask.getId();

        taskManager.deleteSubtaskById(id);
        List<Subtask> loadSubtask = taskManager.getSubtasks();

        assertEquals(loadSubtask.size(), 1);
        assertFalse(loadSubtask.contains(subtask), "Подзадача не удалена");
    }

    @Test
    void shouldGetEpicSubtasks() {
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 150));
        int id = epic.getId();

        List<Subtask> subtasksByEpic = taskManager.getEpicSubtasks(id);
        assertEquals(subtasksByEpic.size(), 2);
        assertTrue(subtasksByEpic.contains(subtask));
    }

    @Test
    void shouldGetHistory() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());

        List<Tasks> history = taskManager.getHistory();
        assertEquals(history.size(), 2, "История сохранена не верно");
        assertTrue(history.contains(epic));
    }

    @Test
    void shouldGetPrioritizedTask() {
            taskManager.addTask(task);
            Task firstTask = new Task("Задача2", "Описание", StatusOfTasks.New, TypeOfTasks.TASK, 100);
            taskManager.addTask(firstTask);
            taskManager.addEpic(epic);
            taskManager.addSubtask(new Subtask("Подзадача1 Эпика2", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100));

            Set<Tasks> set = taskManager.getPrioritizedTasks();
            Tasks savedFirstTask = set.iterator().next();

            assertEquals(3, set.size(), "Список задач сохранен не верно");
            Assertions.assertEquals(savedFirstTask, task, "Сортировка указана не верно");
        }
}
