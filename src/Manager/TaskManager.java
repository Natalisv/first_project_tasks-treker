package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.Tasks;

import java.util.List;
import java.util.Set;

public interface TaskManager {

        // Метод добавляет Задачу/Эпик/Подзадачу в список

        void addTask(Task task);

        void addEpic(Epic epic);

        // метод добавляет подзадачу в писок, добавляет подзадачу в список Эпика и обновляет статус эпика
        void addSubtask(Subtask subtask);

        // метод получает список Задач/Эпиков/Подзадач

        List<Task> getTasks();

        List<Epic> getEpics();

        List<Subtask> getSubtasks();

        // метод получает список подзадач определенного эпика

        List<Subtask> getEpicSubtasks(int id);

        // метод удаляет все Задачи/Эпики/Подзадачи

        void deleteTasks();

        void deleteEpics();

        void deleteSubtasks();

        // метод получает Задачу/Эпик/Подзадау по Id

        Task getTaskById(Integer Id);

        Epic getEpicById(Integer Id);

        Subtask getSubtaskById(Integer Id);

        // метод удаляет Задачу/Эпик по Id

        void deleteTaskById(Integer Id);

        void deleteEpicById(Integer Id);

        // метод удаляет подзадачу из мапа и из спика эпика, и обновляет статус эпика по Id

        void deleteSubtaskById(Integer Id);

        // метод заменяет Задачу/Эпик/Подзадау

        void updateTask(Task newTask);

        void updateEpic(Epic newEpic);

        void updateSubtask(Subtask newSubtask);

        //метод изменяет статус эпика

        void setEpicStatus(Epic epic);

        // метод печатает список просмотренных задач
        List<Tasks> getHistory();

        // метод устанавливает время эпику
        void setEpicTime(Epic epic);
        Set<Tasks> getPrioritizedTasks();
        void addToTreeSet(Tasks task);
}
