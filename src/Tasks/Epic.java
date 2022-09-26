package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Tasks {

    public StatusOfTasks STATUS_NEW = StatusOfTasks.New;
    public List<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, String description, TypeOfTasks type) {
        super(name, description, type);
        setStatus(STATUS_NEW);
        setDurationAndStarTime(0, "01.01.2100, 00:00");
    }

    public Epic(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description, LocalDateTime startTime, LocalDateTime endTime, Duration duration ) {
        super(Id, type, name, status, description, startTime, endTime, duration);
    }

    public Epic(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description) {
        super(Id, type, name, status, description);
        setDurationAndStarTime(0, "01.01.2100, 00:00");
    }

    public Epic(Integer Id) {
        super(Id);
    }

    public void addSubtaskId(Integer Id) {
        subtaskId.add(Id);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskId;
    }

    public void removeSubtask(Integer Id) {
        subtaskId.remove(Id);
    }

    public void setEpicStartTime(LocalDateTime time) {
        this.startTime = time;
    }

    public void setEpicDuration(Duration duration) {
        this.duration = duration;
    }

    public void setEpicEndTime(LocalDateTime time) {
        this.endTime = time;
    }

    // реализовала метод isEpic
    public boolean isEpic(Object o, Epic epic) {
        if(epic.equals(o)) {
            return true;
        }
        else {
            return false;
        }
    }

    // переопределила метод toString()
    @Override
    public String toString() {
        return  "Epic{" +
                "name='" + getName() + '\'' +
                ", Id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", description='" + getDescription() + '\'' +
                '}';
    }

    @Override
    public Integer getEpicId() {
        return null;
    }
}

