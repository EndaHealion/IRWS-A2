package apple_sauce;

import apple_sauce.models.Topic;
import apple_sauce.parsers.LATimesParser;
import apple_sauce.parsers.SGMLNode;
import apple_sauce.parsers.SGMLParser;
import apple_sauce.parsers.TopicsParser;

public class Main {
    public static void main(String[] args) throws Exception {
        // for (Topic topic : TopicsParser.parseTopics("resources/topics")) {
        // System.out.println(topic);
        // }
        // LATimesParser.getDocInformation();
        SGMLNode root = SGMLParser.parseSGML("resources/dataset/latimes/la011890");
        root.print();
    }
}
