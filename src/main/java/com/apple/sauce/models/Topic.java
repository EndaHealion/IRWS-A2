package com.apple.sauce.models;

public record Topic(
        int number,
        String title,
        String description,
        String narrative
) {
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
