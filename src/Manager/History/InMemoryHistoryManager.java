package Manager.History;

import Manager.History.HistoryManager;
import Tasks.Tasks;

import java.util.*;

public class InMemoryHistoryManager<T extends Tasks> implements HistoryManager<T> {

    private final CustomLinkedList<T> historyTasks = new CustomLinkedList<>();

    private final Map<Integer, Node> nodeMap = new HashMap<>();

    public static class Node <T extends Tasks> {
        private T task;
        private Node prev;
        private Node next;

        public Node(T task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task +
                    ", prev=" + prev +
                    ", next=" + next +
                    '}';
        }

        public Tasks getTask() {
            return task;
        }
    }

    public static class CustomLinkedList<T extends Tasks> {

        private Node<T> first;
        private Node<T> last;

        //Добавляет в конец новую ноду
        public Node linkLast(T task) {
            final Node<T> newNode = new Node(task, last, null);
            if(first == null) {
                first = newNode;
            } else {
                last.next = newNode;
            }
            last = newNode;
            return newNode;
        }

        //возвращает список
        public List<T> getTasks() {
            List<T> tasks = new ArrayList<>();
            Node<T> node = first;
            while(node != null){
                tasks.add(node.task);
                node = node.next;
            }
            return tasks;
        }

        //удаляет ноду
        public void removeNode(Node node) {
            if(node.next == null && node.prev == null) {
                first = null;
                last = null;
            } else if(node.prev == null) {
                Node next = node.next;
                next.prev = null;
                first = next;
            } else if(node.next == null) {
                Node prev = node.prev;
                prev.next = null;
                last = prev;
            } else {
                Node prev = node.prev;
                Node next = node.next;
                prev.next = next;
                next.prev = prev;
            }
        }
    }

    // добавляет просмотренную задачу
    @Override
    public void addTask(T task) {
        if(task != null) {
            Integer id = task.getId();
            if (nodeMap.containsKey(id)) {
                historyTasks.removeNode(nodeMap.get(id));
            }
            nodeMap.put(id, historyTasks.linkLast(task));
        }
    }

    //получает историю просмотра задач
    @Override
    public List<T> getHistory() {
        return historyTasks.getTasks();
    }

    //удаляет задачу
    @Override
    public void remove(int id) {
        if(nodeMap.containsKey(id)) {
            Node node = nodeMap.get(id);
            historyTasks.removeNode(node);
            nodeMap.remove(id);
        }
    }

}
