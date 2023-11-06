package apple_sauce.models;

public class Topic {
    int number;
    String title;
    String description;
    String narrative;

    public Topic() {
    }

    public Topic(int number, String title, String description, String narrative) {
        this.number = number;
        this.title = title;
        this.description = description;
        this.narrative = narrative;
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
