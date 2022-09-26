package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

   private Integer EpicId;
    public Subtask(String name, String description, Integer Id, StatusOfTasks status, TypeOfTasks type, int duration) {
        super(name, description,status, type, duration);
        this.EpicId = Id;
    }
    public Subtask(Integer Id) {
        super(Id);
    }

    public Subtask(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description, Integer EpicId, LocalDateTime startTime, LocalDateTime endTime, Duration duration ) {
        super(Id, type, name, status, description, startTime, endTime, duration);
        this.EpicId = EpicId;
    }

    public Subtask(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description, Integer EpicId) {
        super(Id, type, name, status, description);
        this.EpicId = EpicId;
    }

    public Integer getEpicId() {
        return EpicId;
    }

    public void setEpicId(Integer epicId) {
        EpicId = epicId;
    }

    // переопределила метод toString()
    @Override
    public String toString() {
        return  "Subtask{" +
                "name='" + getName() + '\'' +
                ", Id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", description='" + getDescription() + '\'' +
                '}';
    }

}
