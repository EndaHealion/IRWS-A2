package com.apple.sauce;

import com.apple.sauce.models.Topic;
import com.apple.sauce.parsers.TopicsParser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        for (Topic topic : TopicsParser.parseTopics("src/main/resources/topics")) {
            System.out.println(topic);
        }
    }
}