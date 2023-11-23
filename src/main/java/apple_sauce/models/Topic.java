package apple_sauce.models;

public class Topic {
    int number;
    String title;
    String description;
    String narrative;

    public String getDescription() {
        return description;
    }

    public Topic() {
    }

    public String getTitle() {
        return title;
    }

    public Topic(int number, String title, String description, String narrative) {
        this.number = number;
        this.title = title;
        this.description = description.replaceAll("^Description:\\s*", "").trim();
        this.narrative = narrative.replaceAll("^Narrative:\\s*", "").trim();
    }

    @Override
    public String toString() {
        return "Topic{" +
                "number=" + number +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", narrative='" + narrative + '\'' +
                '}';
    }
}