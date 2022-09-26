package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class Tasks {
    private String name;
    private Integer Id;
    private StatusOfTasks status;
    private String description;
    private TypeOfTasks type;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    protected Tasks(String name, String description, StatusOfTasks status, TypeOfTasks type, int duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(duration);
        this.endTime = setEndTime();

    }

    protected Tasks(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description, LocalDateTime startTime, LocalDateTime endTime, Duration duration ) {
        this.Id = Id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    protected Tasks(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description, int duration ) {
        this.Id = Id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(duration);
        this.endTime = setEndTime();
    }

    protected Tasks(Integer Id, TypeOfTasks type, String name, StatusOfTasks status, String description ) {
        this.Id = Id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    protected Tasks(String name, String description,StatusOfTasks status, TypeOfTasks type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
    }
    protected Tasks(String name, String description, TypeOfTasks type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    protected Tasks(Integer Id) {
        this.Id = Id;
    }

    public void setDurationAndStarTime(Integer duration, String startTime) {
        this.duration = Duration.ofMinutes(duration);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        this.startTime = LocalDateTime.parse(startTime,formatter);
        setEndTime();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime setEndTime() {
        this.endTime = startTime.plus(duration);
        return endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public StatusOfTasks getStatus() {
        return status;
    }

    public void setStatus(StatusOfTasks status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract String toString();

    public TypeOfTasks getType() {
        return type;
    }

    public void setType(TypeOfTasks type) {
        this.type = type;
    }

    public abstract Integer getEpicId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tasks tasks = (Tasks) o;
        return Objects.equals(name, tasks.name) && Objects.equals(Id, tasks.Id) && status == tasks.status && Objects.equals(description, tasks.description) && type == tasks.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Id, status, description, type);
    }
}
