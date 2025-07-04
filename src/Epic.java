import java.util.*;

public class Epic extends Task {
    private ArrayList<Subtask> subtasksList;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksList = new ArrayList<>();
    }

    public void setSubtasksList(ArrayList<Subtask> subtasksList) {
        this.subtasksList = subtasksList;
    }

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskList=" + subtasksList +
                '}';
    }
}