package com.apple.sauce.parsers;

import com.apple.sauce.models.Topic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TopicsParser {
    enum Status {
        PARSE_NUM,
        PARSE_TITLE,
        WAIT_DESCRIPTION, PARSE_DESCRIPTION,
        WAIT_NARRATIVE, PARSE_NARRATIVE,
    }

    public static List<Topic> parseTopics(String queriesFilePath) throws IOException {
        Status status = Status.PARSE_NUM;

        int num = 0;
        String title = "";
        StringBuilder description = new StringBuilder();
        StringBuilder narrative = new StringBuilder();

        List<Topic> topics = new ArrayList<>();

        List<String> lines = Files.readAllLines(new File(queriesFilePath).toPath());
        for (String line : lines) {
            line = line.trim();
            switch (status) {
                case PARSE_NUM:
                    if (line.startsWith("<num>")) {
                        num = Integer.parseInt(line.substring(13).trim());
                        status = Status.PARSE_TITLE;
                    }
                    break;
                case PARSE_TITLE:
                    if (!line.startsWith("<title>")) {
                        throw new RuntimeException("Expected <title>");
                    }
                    title = line.substring(8);
                    status = Status.WAIT_DESCRIPTION;
                    break;
                case WAIT_DESCRIPTION:
                    if (line.startsWith("<desc>")) {
                        status = Status.PARSE_DESCRIPTION;
                    }
                    break;
                case PARSE_DESCRIPTION:
                    if (line.isEmpty()) {
                        status = Status.WAIT_NARRATIVE;
                    } else {
                        description.append(line).append(" ");
                    }
                    break;
                case WAIT_NARRATIVE:
                    if (line.startsWith("<narr>")) {
                        status = Status.PARSE_NARRATIVE;
                    }
                    break;
                case PARSE_NARRATIVE:
                    if (line.isEmpty()) {
                        topics.add(new Topic(num, title, description.toString().trim(), narrative.toString().trim()));

                        num = 0;
                        title = "";
                        description = new StringBuilder();
                        narrative = new StringBuilder();

                        status = Status.PARSE_NUM;
                    } else {
                        narrative.append(line).append(" ");
                    }
                    break;
            }
        }
        return topics;
    }
}