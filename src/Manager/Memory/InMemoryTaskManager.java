package Manager.Memory;

import Manager.Exception.TaskValidationException;
import Manager.History.HistoryManager;
import Manager.Managers;
import Manager.TaskManager;
import Tasks.StatusOfTasks;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Tasks;
import Tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int generatedId =0;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final Set<Tasks> setOfTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        setOfTasks = new TreeSet<>(comparator);
    }


    Comparator<Tasks> comparator = new Comparator<>() {
        @Override
        public int compare(Tasks o1, Tasks o2) {
            int i;
            if(o1.getStartTime().isBefore(o2.getStartTime())) {
                i = -1;
            } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
                i= 1;
            } else {
                i =0 ;
            }
            return i ;
        }
    };

    private static final HistoryManager<Tasks> historyManager = Managers.getDefaultHistory();

    public static HistoryManager<Tasks> getHistoryManager() {
            return historyManager;
        }

    @Override
    public void addTask(Task task) {
            task.setId(generatedId++);
            if (setOfTasks.isEmpty()) {
                tasks.put(task.getId(), task);
                addToTreeSet(task);
            } else {
                LocalDateTime timeStart = task.getStartTime();
                LocalDateTime timeEnd = task.getEndTime();
                Boolean isInvalidTime = setOfTasks.stream()
                        .anyMatch(s1 -> (s1.getStartTime().isBefore(timeStart) || s1.getStartTime().equals(timeStart)) &&
                                s1.getEndTime().isAfter(timeEnd));
                if (isInvalidTime) {
                    try {
                        throw new TaskValidationException("Время занято другой задачей. Задайте другое время");
                    } catch (TaskValidationException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    tasks.put(task.getId(), task);
                    addToTreeSet(task);
                }
            }
        }

    @Override
    public void addEpic(Epic epic){
        epic.setId(generatedId++);
        epics.put(epic.getId(), epic);
        }

    @Override
    public void addSubtask(Subtask subtask){
        subtask.setId(generatedId++);
        if(setOfTasks.isEmpty()) {
            subtasks.put(subtask.getId(), subtask);
            addToTreeSet(subtask);
        } else {
            LocalDateTime timeStart = subtask.getStartTime();
            LocalDateTime timeEnd = subtask.getEndTime();
            Boolean isInvalidTime =  setOfTasks.stream()
                    .anyMatch(s1 -> (s1.getStartTime().isBefore(timeStart) || s1.getStartTime().equals(timeStart)) &&
                            s1.getEndTime().isAfter(timeEnd) );
            if(isInvalidTime) {
                try {
                    throw new TaskValidationException("Время занято другой задачей. Задайте другое время");
                } catch (TaskValidationException e) {
                    System.out.println(e.getMessage());
                }
                return;
            } else {
                subtasks.put(subtask.getId(), subtask);
                addToTreeSet(subtask);
            }
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        setEpicStatus(epic);
    }

    @Override
    public List<Task> getTasks() {
        List<Task> arrayTasks = new ArrayList<>();
        arrayTasks.addAll(tasks.values());
        return arrayTasks;
    }


    @Override
    public List<Epic> getEpics() {
        List<Epic> arrayEpics = new ArrayList<>();
        arrayEpics.addAll(epics.values());
        return arrayEpics;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> arraySubtasks = new ArrayList<>();
        arraySubtasks.addAll(subtasks.values());
        return arraySubtasks;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        Epic epic = epics.get(id);
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer i : epic.subtaskId) {
            subtasksOfEpic.add(subtasks.get(i));
        }
        return subtasksOfEpic;
    }

    @Override
    public void deleteTasks() {
        List<Integer> idTasks = new ArrayList<>();
        idTasks.addAll(tasks.keySet());
        for(Integer i : idTasks) {
            historyManager.remove(i);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        List<Integer> idEpics = new ArrayList<>();
        idEpics.addAll(epics.keySet());
        for(Integer i : idEpics) {
            deleteEpicById(i);
        }
    }

    @Override
    public void deleteSubtasks() {
        List<Integer> idSubtasks = new ArrayList<>();
        idSubtasks.addAll(subtasks.keySet());
        for(Integer i : idSubtasks) {
            deleteSubtaskById(i);
        }
    }

    @Override
    public Task getTaskById(Integer Id) {
        if (tasks.containsKey(Id)) {
            historyManager.addTask(tasks.get(Id));
            return tasks.get(Id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(Integer Id) {
        if (epics.containsKey(Id)) {
            historyManager.addTask(epics.get(Id));
            return epics.get(Id);
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(Integer Id) {
        if (subtasks.containsKey(Id)) {
            historyManager.addTask(subtasks.get(Id));
            return subtasks.get(Id);
        } else {
            return null;
        }
    }

    @Override
    public void deleteTaskById(Integer Id) {
        if (tasks.containsKey(Id)) {
            tasks.remove(Id);
            historyManager.remove(Id);
        }
    }

    @Override
    public void deleteEpicById(Integer Id) {
        if (epics.containsKey(Id)) {
            Epic epic = epics.get(Id);
            List<Integer> subtaskId = new ArrayList<>(epic.getSubtaskIds());
            if(!subtaskId.isEmpty()) {
                for (Integer i : subtaskId) {
                    deleteSubtaskById(i);
                }
            }
            epics.remove(Id);
            historyManager.remove(Id);
        }
    }

    @Override
    public void deleteSubtaskById(Integer Id) {
        if (subtasks.containsKey(Id)) {
            Subtask subtask = subtasks.get(Id);
            Integer EpicId = subtask.getEpicId();
            Epic epic = epics.get(EpicId);
            epic.removeSubtask(Id);
            subtasks.remove(Id);
            setEpicStatus(epic);
            historyManager.remove(Id);
        }
    }

    @Override
    public void updateTask(Task newTask) {
        final Integer Id = newTask.getId();
       if (tasks.containsKey(Id)) {
           tasks.put(newTask.getId(), newTask);
       }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        final Integer Id = newEpic.getId();
        if (epics.containsKey(Id)) {
            epics.put(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        final Integer Id = newSubtask.getId();
        if (subtasks.containsKey(Id)) {
            subtasks.put(newSubtask.getId(), newSubtask);
            Integer EpicId = newSubtask.getEpicId();
            setEpicStatus(epics.get(EpicId));
        }
    }

    @Override
    public void setEpicStatus(Epic epic) {
        int counterNew =0;
        int counterDone =0;
        if (epic.subtaskId.size() == 0) {
            epic.setStatus(StatusOfTasks.New);
        } else {
            for (Integer i : epic.subtaskId) {
                Subtask subtask = subtasks.get(i);
                if (subtask.getStatus().equals(StatusOfTasks.New)) {
                    counterNew++;
                } else if (subtask.getStatus().equals(StatusOfTasks.DONE)) {
                    counterDone++;
                }
            }
                if(counterNew == epic.subtaskId.size()) {
                    epic.setStatus(StatusOfTasks.New);
                } else if (counterDone == epic.subtaskId.size()) {
                    epic.setStatus(StatusOfTasks.DONE);
                } else {
                    epic.setStatus(StatusOfTasks.IN_PROGRESS);
                }
            }
        }
        public void setEpicTime(Epic epic) {
        if(!epic.subtaskId.isEmpty()) {
            List<Subtask> listOfSubtask = new ArrayList<>();
            for (Integer i : epic.subtaskId) {
                listOfSubtask.add(subtasks.get(i));
            }
            Optional<LocalDateTime> start = listOfSubtask.stream()
                    .map(s1 -> s1.getStartTime())
                    .sorted(LocalDateTime::compareTo)
                    .findFirst();
            epic.setEpicStartTime(start.get());

            Optional<LocalDateTime> end = listOfSubtask.stream()
                    .sorted(Comparator.comparing(Subtask::getEndTime).reversed())
                    .map(s1 -> s1.getEndTime())
                    .findFirst();
            epic.setEpicEndTime(end.get());

            List<Long> duration = listOfSubtask.stream()
                    .map(s1 -> s1.getDuration().toMinutes())
                            .collect(Collectors.toList());
            long sumOfDuration = 0;
            for(long i: duration) {
                sumOfDuration += i;
            }
            epic.setEpicDuration(Duration.ofMinutes(sumOfDuration));
        } else {
            epic.setEpicStartTime(null);
            epic.setEpicEndTime(null);
            epic.setEpicDuration(null);
        }
        }



    @Override
    public List<Tasks> getHistory() {
        return historyManager.getHistory();
    }

        public List<Integer> getIdTask() {
            List<Integer> list = new ArrayList<>();
            list.addAll(tasks.keySet());
            return list;
        }

        public List<Integer> getIdEpic() {
            List<Integer> list = new ArrayList<>();
            list.addAll(epics.keySet());
            return list;
        }

        public List<Integer> getIdSubtask() {
            List<Integer> list = new ArrayList<>();
            list.addAll(subtasks.keySet());
            return list;
        }

    @Override
    public Set<Tasks> getPrioritizedTasks() {
        return setOfTasks;
    }

    @Override
    public void addToTreeSet(Tasks task) {
        setOfTasks.add(task);
    }
    }
