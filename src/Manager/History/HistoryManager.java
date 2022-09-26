package Manager.History;

import Tasks.Tasks;

import java.util.List;

public interface HistoryManager<T extends Tasks> {

    List<T> getHistory();

    void addTask(T task);

    void remove(int id);
}
