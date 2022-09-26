package Test;

import Manager.Exception.ManagerSaveException;
import Manager.TaskManager;
import Tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {
    protected T taskManager;

    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");


    public abstract T createTaskManager() throws ManagerSaveException;

    @BeforeEach
    void setTaskManager() throws ManagerSaveException {
        taskManager = createTaskManager();
    }


    @BeforeEach
    void beforeEach() {
        task = new Task("Задача1", "Описание задачи1", StatusOfTasks.New, TypeOfTasks.TASK,  100);
        epic = new Epic("Эпик2", "Без сабтасков", TypeOfTasks.EPIC);

    }
    // запустить отдельно
    @Test
    void shouldGetPrioritizedTasks() {
        taskManager.addTask(task);
        Task firstTask = new Task("Задача2", "Описание", StatusOfTasks.New, TypeOfTasks.TASK, 100);
        taskManager.addTask(firstTask);
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Подзадача1 Эпика2", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100));

        Set<Tasks> set = taskManager.getPrioritizedTasks();
        Tasks savedFirstTask = set.iterator().next();

        assertEquals(3, set.size(), "Список задач сохранен не верно");
        assertEquals(savedFirstTask, task, "Сортировка указана не верно");
    }

    @Test
    void shouldSetTimeofTask() {
        taskManager.addTask(task);
        task.setDurationAndStarTime(100, "25.08.2022, 08:00");
        LocalDateTime savedStarTime = task.getStartTime();
        LocalDateTime savedEndTime = task.getEndTime();

        assertEquals(savedStarTime.format(formatter), "25.08.2022, 08:00");
        assertEquals(savedEndTime.format(formatter), "25.08.2022, 09:40");
    }

    @Test
    void shouldSetTimeofSubtask() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика2", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);
        subtask.setDurationAndStarTime(100, "25.08.2022, 08:00");
        LocalDateTime savedStarTime = subtask.getStartTime();
        LocalDateTime savedEndTime = subtask.getEndTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        assertEquals(savedStarTime.format(formatter), "25.08.2022, 08:00");
        assertEquals(savedEndTime.format(formatter), "25.08.2022, 09:40");
    }

    @Test
    void shouldSetTimeofEpic() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика2", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);

        Subtask subtask1 = new Subtask("Подзадача2 Эпика2", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 180);
        taskManager.addSubtask(subtask1);

        taskManager.setEpicTime(epic);

        LocalDateTime savedStarTime = epic.getStartTime();
        LocalDateTime savedEndTime = epic.getEndTime();
        long savedduration = epic.getDuration().toMinutes();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        assertEquals(savedStarTime.format(formatter), subtask.getStartTime().format(formatter));
        assertEquals(savedEndTime.format(formatter), subtask1.getEndTime().format(formatter));
        assertEquals(280, savedduration);
    }

    @Test
    void shouldAddTask() {
        taskManager.addTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(savedTask, task, "Задачи не совпадают");
    }

    @Test
    void shouldAddEpic() {
        taskManager.addEpic(epic);
        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(savedEpic, epic, "Эпики не совпадают");
    }

    @Test
    void shouldAddSubtask() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика2", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);
        final int subtaskId = subtask.getId();
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(savedSubtask, subtask, "Подзадачи не совпадают");

        List<Epic> epicList = taskManager.getEpics();
        Epic epicOfSubtask = epicList.get(subtask.getEpicId());

        assertEquals(epic, epicOfSubtask);
    }

    @Test
    void shouldDeleteTasks() {
        taskManager.addTask(task);
        taskManager.addTask(new Task("Задача1", "Описание задачи1", StatusOfTasks.New, TypeOfTasks.TASK,  100));
        List<Task> listOfTasks = taskManager.getTasks();

        assertEquals(2, listOfTasks.size(), "Список задач должен состоять из двух задач");

        taskManager.deleteTasks();
        listOfTasks = taskManager.getTasks();

        assertEquals(0 ,listOfTasks.size(), "Все задачи должны быть удалены, после вызова метода deleteTasks()");
    }

    @Test
    void shouldDeleteEmptyTasks() {
        taskManager.deleteTasks();
        List<Task> listOfTasks = taskManager.getTasks();

        assertEquals(0 ,listOfTasks.size(), "Все задачи должны быть удалены, после вызова метода deleteTasks()");
    }

    @Test
    void shouldDeleteSubtasks() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика2", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);
        List<Subtask> listOfSubtasks = taskManager.getSubtasks();

        assertEquals(1, listOfSubtasks.size(), "Список подзадач должен состоять из одной подзадачи");

        taskManager.deleteSubtasks();
        listOfSubtasks = taskManager.getSubtasks();

        assertEquals(0, listOfSubtasks.size(), "Все подзадачи должны быть удалены, после вызова метода deleteSubtasks()");
    }

    @Test
    void shouldDeleteEpics() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика2", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);

        taskManager.deleteEpics();

        List<Epic> listOfEpic = taskManager.getEpics();
        List<Subtask> listOfSubtask = taskManager.getSubtasks();

        assertEquals(0, listOfEpic.size(), "Список епиков должен быть пустым");
        assertEquals(0, listOfSubtask.size(), "Список сабтасков должен быть пустым");
    }

    @Test
    void shouldDeleteEmptyEpics() {
        taskManager.deleteEpics();

        List<Epic> listOfEpic = taskManager.getEpics();
        List<Subtask> listOfSubtask = taskManager.getSubtasks();

        assertEquals(0, listOfEpic.size(), "Список епиков должен быть пустым");
        assertEquals(0, listOfSubtask.size(), "Список сабтасков должен быть пустым");
    }

    @Test
    void shouldGetTasks() {
        taskManager.addTask(task);
        Task task2 = new Task("Задача1", "Описание задачи1", StatusOfTasks.New, TypeOfTasks.TASK, 100);;
        taskManager.addTask(task2);

        Tasks[] tasksExpected = {task, task2};
        Tasks[] tasksResult = taskManager.getTasks().toArray(new Tasks[2]);

        assertArrayEquals(tasksExpected, tasksResult);

        List<Task> list = taskManager.getTasks();

        assertNotNull(list, "Задачи не позвращаются");
        assertEquals(2, list.size(), "Неверное количество задач");

    }

    @Test
    void shouldGetTasksWithEmptyList() {
        Tasks[] tasksExpected = {};
        Tasks[] tasksResult = taskManager.getTasks().toArray(new Tasks[0]);

        assertArrayEquals(tasksExpected, tasksResult);
    }

    @Test
    void shouldGetEpics() {
        taskManager.addEpic(epic);
        Epic epic2 = new Epic("Эпик4", "Без сабтасков", TypeOfTasks.EPIC);
        taskManager.addEpic(epic2);

        Epic[] epicsExpected = {epic, epic2};
        Epic[] epicsResult = taskManager.getEpics().toArray(new Epic[0]);

        assertArrayEquals(epicsExpected, epicsResult);

        List<Epic> list = taskManager.getEpics();

        assertNotNull(list, "Задачи не позвращаются");
        assertEquals(2, list.size(), "Неверное количество задач");
    }

    @Test
    void shouldGetEpicsWithEmptyList() {
        Epic[] epicsExpected = {};
        Epic[] epicsResult = taskManager.getEpics().toArray(new Epic[0]);

        assertArrayEquals(epicsExpected, epicsResult);
    }

    @Test
    void shouldGetSubtasks() {
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 120);
        taskManager.addSubtask(subtask2);

        Subtask[] subtasksExpected = {subtask1, subtask2};
        Subtask[] subtasksResult = taskManager.getSubtasks().toArray(new Subtask[2]);

        assertArrayEquals(subtasksExpected, subtasksResult);

        List<Subtask> list = taskManager.getSubtasks();

        assertNotNull(list, "Задачи не позвращаются");
        assertEquals(2, list.size(), "Неверное количество задач");
    }

    @Test
    void shouldDeleteTaskByIdWhenCreateOneTask() {
        taskManager.addTask(task);
        taskManager.deleteTaskById(task.getId());
        final List<Task> listOfTasks = taskManager.getTasks();

        assertEquals(0,listOfTasks.size());
    }

    @Test
    void shouldDeleteTaskByIdWhenImposibleId() {
        taskManager.addTask(task);
        Integer expectedId = task.getId();
        Integer resultId = 100;
        taskManager.deleteTaskById(resultId);
        final List<Task> listOfTasks = taskManager.getTasks();

        assertNotEquals(expectedId, resultId);
        assertEquals(1, listOfTasks.size());
    }

    @Test
    void shouldDeleteEpicByIdWhenCreateOneEpic() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100);
        taskManager.addSubtask(subtask);

        List<Integer> listOfSubtask = epic.getSubtaskIds();
        Integer subtaskId = listOfSubtask.get(0);
        Integer epicId = epic.getEpicId();
        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertEquals(subtask, savedSubtask);

        taskManager.deleteEpicById(epic.getId());
        Epic epicAfterRemove = taskManager.getEpicById(epicId);
        Subtask subtaskAfterRemove = taskManager.getSubtaskById(subtaskId);

        assertNull(epicAfterRemove);
        assertNull(subtaskAfterRemove);
    }

    @Test
    void shouldDeleteEpicByIdWhenImposibleId() {
        taskManager.addEpic(epic);
        Integer expectedId = epic.getId();
        Integer resultId = 100;
        taskManager.deleteEpicById(resultId);

        final List<Epic> listOfEpics = taskManager.getEpics();

        assertNotEquals(expectedId, resultId);
        assertEquals(1, listOfEpics.size());
    }

    @Test
    void shouldDeleteSubtaskByIdWhenCreateOneSubtask() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());

        taskManager.deleteSubtaskById(subtask.getId());
        Subtask subtaskAfterRemove = taskManager.getSubtaskById(savedSubtask.getId());

        assertNull(subtaskAfterRemove);

        final List<Epic> listOfEpic = taskManager.getEpics();

        assertEquals(1, listOfEpic.size(), "Эпик не должен быть удален при удалении подзадачи");
    }

    @Test
    void shouldDeleteSubtaskByIdWhenImposibleId() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);

        Integer expectedId = subtask.getId();
        Integer resultId = 100;

        taskManager.deleteSubtaskById(resultId);

        final List<Subtask> listOfSubtasks = taskManager.getSubtasks();

        assertNotEquals(expectedId, resultId);
        assertEquals(1, listOfSubtasks.size());
    }

    @Test
    void shouldGetTaskById() {
        taskManager.addTask(task);
        Integer taskId = task.getId();
        Task savedTask = taskManager.getTaskById(taskId);

        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void shouldGetEpicById() {
        taskManager.addEpic(epic);
        Integer epicId = epic.getId();
        Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    void shouldGetSubtaskById() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);

        Integer subtaskId = subtask.getId();
        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertEquals(subtask, savedSubtask, "Эпики не совпадают");
    }

    @Test
    void shouldUpdateTask() {
        taskManager.addTask(task);
        Integer newTaskId = task.getId();
        Task savedTask = taskManager.getTaskById(newTaskId);
        Task newTask = new Task(newTaskId,TypeOfTasks.TASK,"Задача2", StatusOfTasks.DONE, "Описание задачи1",  100);
        taskManager.updateTask(newTask);

        assertNotEquals(savedTask, taskManager.getTaskById(newTaskId), "Задача не обновилась");
    }

    @Test
    void shouldNotUpdateTaskWhenIncorrectId() {
        taskManager.addTask(task);
        Integer taskId = task.getId();
        Integer newTaskId = 100;
        Task newTask = new Task(newTaskId,TypeOfTasks.TASK,"Задача2", StatusOfTasks.DONE, "Описание задачи1");
        taskManager.updateTask(newTask);

        Task savedTask = taskManager.getTaskById(taskId);
        Task savedNewTask = taskManager.getTaskById(newTaskId);

        assertEquals(task,savedTask);
        assertNull(savedNewTask, "Задача с невалидным Id не должна обновляться");
    }

    @Test
    void shouldUpdateEpic() {
        taskManager.addEpic(epic);
        Integer newEpicId = epic.getId();
        Epic savedEpic = taskManager.getEpicById(newEpicId);
        Epic newEpic = new Epic(newEpicId, TypeOfTasks.EPIC,  "новый эпик", StatusOfTasks.DONE, "Без сабтасков");
        taskManager.updateEpic(newEpic);

        assertNotEquals(savedEpic, taskManager.getEpicById(newEpicId), "Задача не обновилась");
    }

    @Test
    void shouldNotUpdateEpicWhenIncorrectId() {
        taskManager.addEpic(epic);
        Integer newEpicId = 100;
        Epic newEpic = new Epic(newEpicId, TypeOfTasks.EPIC,  "новый эпик", StatusOfTasks.DONE, "Без сабтасков");
        taskManager.updateEpic(newEpic);

        Epic savedEpic = taskManager.getEpicById(epic.getEpicId());
        Epic savedNewEpic = taskManager.getEpicById(newEpicId);

        assertNull(savedNewEpic);
    }

    @Test
    void shouldUpdateSubtask() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);

        Integer newSubtaskId = subtask.getId();
        Subtask savedSbtask = taskManager.getSubtaskById(newSubtaskId);
        Subtask newSubtask = new Subtask(newSubtaskId,TypeOfTasks.SUBTASK, "Подзадача1 Эпика", StatusOfTasks.DONE, "Описание", epic.getId());
        taskManager.updateTask(newSubtask);

        assertNotEquals(savedSbtask, taskManager.getTaskById(newSubtaskId), "Задача не обновилась");
    }

    @Test
    void shouldNotUpdateSubtaskWhenIncorrectId() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);

        Integer newSubtaskId = 100;
        Subtask savedSbtask = taskManager.getSubtaskById(subtask.getId());
        Subtask newSubtask = new Subtask(newSubtaskId,TypeOfTasks.SUBTASK, "Подзадача1 Эпика", StatusOfTasks.DONE, "Описание", epic.getId());
        taskManager.updateTask(newSubtask);
        Subtask newSavedSubtask = taskManager.getSubtaskById(newSubtaskId);

        assertNull(newSavedSubtask);
    }

    @Test
    void shouldgetEpicSubtasks() {
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 100 );
        taskManager.addSubtask(subtask1);

        List<Subtask> list = taskManager.getEpicSubtasks(0);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    void shouldSetEpicStatusWithoutSubtaskAndSubtaskStatusNew() {
        taskManager.addEpic(epic);
        taskManager.setEpicStatus(epic);

        assertEquals(StatusOfTasks.New, epic.getStatus(), "Статус эпика без подзадач расчитан не верно");

        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);

        taskManager.setEpicStatus(epic);

        assertEquals(StatusOfTasks.New, epic.getStatus(), "Статус эпика расчитан не верно");
    }
        @Test
        void shouldSetEpicStatusWithSubtaskStatusDone(){
            taskManager.addEpic(epic);
            subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.DONE, TypeOfTasks.SUBTASK, 100);
            taskManager.addSubtask(subtask);
            Subtask subtask2 = new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.DONE, TypeOfTasks.SUBTASK, 100);
            taskManager.addSubtask(subtask2);

            taskManager.setEpicStatus(epic);

            assertEquals(StatusOfTasks.DONE, epic.getStatus(), "Статус эпика расчитан не верно");
        }

    @Test
    void shouldSetEpicStatusWithSubtaskStatusNewAndDone(){
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.DONE, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask2);

        taskManager.setEpicStatus(epic);

        assertEquals(StatusOfTasks.IN_PROGRESS, epic.getStatus(), "Статус эпика расчитан не верно");
    }

    @Test
    void shouldSetEpicStatusWithSubtaskStatusInProgress(){
        taskManager.addEpic(epic);
        subtask = new Subtask("Подзадача1 Эпика", "Описание", epic.getId(), StatusOfTasks.IN_PROGRESS, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("Подзадача2 Эпика", "Описание", epic.getId(), StatusOfTasks.IN_PROGRESS, TypeOfTasks.SUBTASK,  100);
        taskManager.addSubtask(subtask2);

        taskManager.setEpicStatus(epic);

        assertEquals(StatusOfTasks.IN_PROGRESS, epic.getStatus(), "Статус эпика расчитан не верно");
    }
    }

