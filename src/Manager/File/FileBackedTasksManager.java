package Manager.File;

import Manager.History.HistoryManager;
import Manager.Exception.ManagerSaveException;
import Manager.Memory.InMemoryTaskManager;
import Tasks.Tasks;
import Tasks.Task;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.TypeOfTasks;
import Tasks.StatusOfTasks;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager <T extends Tasks> extends InMemoryTaskManager {
    protected File file;
    public final String firstLine = "Id,type,name,status,description,epic,StartTime,EndTime,Duration";


    public FileBackedTasksManager(File file) {
        this.file = file;
    }
    public FileBackedTasksManager() {
    }



    // метод сохраняет текущее состояние менеджера в указанный файл


    public void save() {
        try (Writer fileWriter = new FileWriter("file.csv")) {
            try {
                fileWriter.write(firstLine + "\n ");
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка записи первой линии");
            }

            List<Task> tasks = getTasks();
            if (tasks != null) {
                for (Task task : tasks) {
                    String lineOfTask = toString((T) task);
                    try {
                        fileWriter.write(lineOfTask + "\n");
                    } catch (IOException e) {
                        throw new ManagerSaveException("Не удалось сохранить task");
                    }
                }
            }

            List<Epic> epics = getEpics();
            if (epics != null) {
                for (Epic epic : epics) {
                    String lineOfEpic = toString((T) epic);
                    try {
                        fileWriter.write(lineOfEpic + "\n");
                    } catch (IOException e) {
                        throw new ManagerSaveException("Не удалось сохранить epic");
                    }
                }
            }

            List<Subtask> subtasks = getSubtasks();
            if (subtasks != null) {
                for (Subtask subtask : subtasks) {
                    String lineOfSubtask = toString((T) subtask);
                    try {
                        fileWriter.write(lineOfSubtask + "\n");
                    } catch (IOException e) {
                        throw new ManagerSaveException("Не удалось сохранить subtask");
                    }
                }
            }

            fileWriter.write("\n");

            try {
                fileWriter.write(historyToString(getHistoryManager()) + "\n");
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось сохранить историю просмотров");
            }

        } catch (ManagerSaveException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        }
    }

        //метод сохранения задачи в строку
    public String toString(T Tasks) {
        return Tasks.getId() + "," + Tasks.getType() + "," + Tasks.getName() + "," + Tasks.getStatus() + "," + Tasks.getDescription() + "," + Tasks.getEpicId()+ "," + Tasks.getStartTime()+ ","+ Tasks.getEndTime() + "," + Tasks.getDuration().toMinutes();
    }

    // метод сохраняет менеджер истории
    public static String historyToString(HistoryManager manager) {
        List<Tasks> listOfHistory = manager.getHistory();
        String listId = "";
        if(!listOfHistory.isEmpty()) {
            List<Integer> listOfId = new ArrayList<>();
            for (Tasks task : listOfHistory) {
                listOfId.add(task.getId());
            }
            listId = String.valueOf(listOfId.get(0));
            for (int i = 1; i < listOfId.size(); i++) {
                listId += "," + listOfId.get(i);
            }
        }
        return listId;
    }

    // метод создает задачу из строки
    public static Tasks fromString(String value) {
        String[] lines = value.split(",");
        Tasks task = null;
        if (!value.isBlank() && !value.isEmpty()) {
            String type = lines[1];
            switch (type) {
                case "TASK":
                    task = new Task(Integer.parseInt(lines[0].trim()), TypeOfTasks.valueOf(lines[1].trim()), lines[2].trim(), StatusOfTasks.valueOf(lines[3].trim()), lines[4].trim(), LocalDateTime.parse(lines[6].trim()),LocalDateTime.parse(lines[7].trim()), Duration.ofMinutes(Long.parseLong(lines[8].trim())));
                    break;
                case "EPIC":
                    task = new Epic(Integer.parseInt(lines[0].trim()), TypeOfTasks.valueOf(lines[1].trim()), lines[2].trim(), StatusOfTasks.valueOf(lines[3].trim()), lines[4].trim(), LocalDateTime.parse(lines[6].trim()),LocalDateTime.parse(lines[7].trim()), Duration.ofMinutes(Long.parseLong(lines[8].trim())));
                    break;
                case "SUBTASK":
                    task = new Subtask(Integer.parseInt(lines[0].trim()), TypeOfTasks.valueOf(lines[1].trim()), lines[2].trim(), StatusOfTasks.valueOf(lines[3].trim()), lines[4].trim(), Integer.parseInt(lines[5].trim()), LocalDateTime.parse(lines[6].trim()),LocalDateTime.parse(lines[7].trim()), Duration.ofMinutes(Long.parseLong(lines[8].trim())));
                    break;
                default:
                    System.out.println("Невозможно прочитать строку из файла");
                    break;
            }
        }
        return task;
    }


    //метод восстанавливает менеджер истории
    public static List<Integer> historyFromString(String value) {
        List<Integer> listOfHistory = new ArrayList<>();
        if (!value.isBlank() && !value.isEmpty()) {
            String[] lines = value.split(",");
            for (int i = 0; i < lines.length; i++) {
                listOfHistory.add(Integer.valueOf(lines[i]));
            }
            return listOfHistory;
        } else {
            return listOfHistory;
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager();
        List<String> listOfLines = manager.readFile(file);
        for (int i = 1; i < listOfLines.size(); i++) {
            String line = listOfLines.get(i);
            Tasks task = fromString(line);
            if (task != null) {
                String type = String.valueOf(task.getType());
                switch (type) {
                    case "TASK":
                        Task taskNew = (Task) task;
                        manager.addTask(taskNew);
                        break;
                    case "EPIC":
                        Epic epicNew = (Epic) task;
                        manager.addEpic(epicNew);
                        break;
                    case "SUBTASK":
                        Subtask subtaskNew = (Subtask) task;
                        manager.addSubtask(subtaskNew);
                        break;
                    default:
                        System.out.println("Невозможно прочитать файл");
                        break;
                }
            } else {
                break;
            }
        }
                String lastLine = (listOfLines.get(listOfLines.size()-1));
                    List<Integer> listOfHistory = historyFromString(lastLine);
                    for (Integer j : listOfHistory) {
                        if (manager.getIdTask().contains(j)) {
                            getHistoryManager().addTask(manager.getTaskById(j));
                        } else if (manager.getIdEpic().contains(j)) {
                            getHistoryManager().addTask(manager.getEpicById(j));
                        } else if (manager.getIdSubtask().contains(j)) {
                            getHistoryManager().addTask(manager.getSubtaskById(j));
                        }
                    }
        return manager;
    }


    // метод читает строки из файла
    public List<String> readFile(File file) throws IOException {
        List<String> listOfLines = new ArrayList<>();
        FileReader reader = new FileReader(file);
        try (BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                listOfLines.add(line);
            }
        }
        return listOfLines;
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Task getTaskById(Integer Id) {
        Task task = super.getTaskById(Id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(Integer Id) {
        Epic epic = super.getEpicById(Id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer Id) {
        Subtask subtask = super.getSubtaskById(Id);
        save();
        return subtask;
    }

    @Override
    public void deleteTaskById(Integer Id) {
        super.deleteTaskById(Id);
        save();
    }

    @Override
    public void deleteEpicById(Integer Id) {
        super.deleteEpicById(Id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer Id) {
        super.deleteSubtaskById(Id);
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }
}
