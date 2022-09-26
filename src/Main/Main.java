
package Main;

import Manager.Exception.ManagerSaveException;
import Manager.HTTP.HTTPTaskManager;
import Manager.Managers;
import Server.KVServer;
import Tasks.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ManagerSaveException {
        new KVServer().start();

        HTTPTaskManager manager = Managers.getDefault();


        //Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
        Task task = new Task("Задача1", "Описание задачи1", StatusOfTasks.New, TypeOfTasks.TASK,  100);
        Task task1 = new Task("Задача2", "Описание задачи1", StatusOfTasks.New, TypeOfTasks.TASK,  120);
        manager.addTask(task);
        manager.addTask(task1);

        Epic epic = new Epic("Эпик1", "Три сабтаска", TypeOfTasks.EPIC);
        Epic epic2 = new Epic("Эпик2", "Без сабтасков", TypeOfTasks.EPIC);
        manager.addEpic(epic);
        manager.addEpic(epic2);

        Subtask subtask = new Subtask("Подзадача1 Эпика1", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK,  100);
        manager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("Подзадача2 Эпика1", "Описание", epic.getId(), StatusOfTasks.New, TypeOfTasks.SUBTASK, 180);
        manager.addSubtask(subtask1);

        //Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        // Вызовите нескоторые задачи

        manager.getTaskById(0);
        manager.getTaskById(1);
        manager.getEpicById(2);
        manager.getSubtaskById(4);

        System.out.println(manager.getHistory());

        System.out.println(manager.getPrioritizedTasks());

    }

}




