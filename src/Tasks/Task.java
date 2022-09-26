package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task extends Tasks {

    public Task(String name, String description, StatusOfTasks status, TypeOfTasks type, int duration) {
        super(name, description, status, type, duration);
    }
    public Task(String name, String description, StatusOfTasks status, TypeOfTasks type) {
        super(name, description,status, type);
        setDurationAndStarTime(0, "01.01.2100, 00:00");
    }
    public Task(Integer Id) {
        super(Id);
    }
    public Task(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description, LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        super(Id, type, name, status, description, startTime, endTime, duration);
    }

    public Task(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description, int duration) {
        super(Id, type, name, status, description, duration);
    }

    public Task(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description) {
        super(Id, type, name, status, description);
    }

    @Override
    public String toString() {
        return "Task{" +
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
