package Test.history;

import Manager.History.HistoryManager;
import Manager.History.InMemoryHistoryManager;
import Manager.Memory.InMemoryTaskManager;
import Tasks.Task;
import Tasks.Epic;
import Tasks.Tasks;
import Tasks.TypeOfTasks;
import Tasks.StatusOfTasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class HistoryManagerTest <T extends HistoryManager> {

    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    protected Task task;
    protected Epic epic;

    @BeforeEach
    void beforeEach() {
        task = new Task("Задача1", "Описание задачи1", StatusOfTasks.New, TypeOfTasks.TASK, 100);
        epic = new Epic("Эпик2", "Без сабтасков", TypeOfTasks.EPIC);
    }

    @Test
    void shouldGetHistory() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        historyManager.addTask(task);
        historyManager.addTask(epic);

        List<T> list = historyManager.getHistory();

        assertNotNull(list);
        assertEquals(2, list.size(), "Задачи выводятся не верно");

    }

    @Test
    void shouldAddTask() {
        taskManager.addTask(task);
        historyManager.addTask(task);
        List<Tasks> list = historyManager.getHistory();

        assertNotNull(list);
        assertEquals(1, list.size(), "Задача не записалась");

        historyManager.addTask(epic);
        historyManager.addTask(task);
        list = historyManager.getHistory();

        assertEquals(2,list.size(), "В истории не может быть двух одинаковых задач");


    }

    @Test
    void shouldRemove() {
        taskManager.addTask(task);
        historyManager.addTask(task);

        assertNotNull(historyManager.getHistory());
        assertEquals(1,(historyManager.getHistory()).size(), "В списке должна быть одна задача");

        historyManager.remove(task.getId());
        List<Tasks> listAfterRemove = historyManager.getHistory();

        assertEquals(0, listAfterRemove.size(), "Задача не удалена");

        historyManager.remove(task.getId());
        assertEquals(0, listAfterRemove.size(), "При удалении задачи из пустого списка, список должен остаться пустым");

        taskManager.addEpic(epic);
        historyManager.addTask(epic);
        Integer incorrectId = 100;
        historyManager.remove(incorrectId);

        listAfterRemove = historyManager.getHistory();
        assertEquals(1, listAfterRemove.size(),"Задача не должна быть удалена при невалидном Id");
    }





}