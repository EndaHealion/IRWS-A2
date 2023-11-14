package apple_sauce.parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Stack;

class SGMLParserState {
    Stack<SGMLNode> nodeStack;
    String content;
    int i;

    public SGMLParserState() {
        this.content = "";
        this.i = 0;
        this.nodeStack = new Stack<SGMLNode>();
    }

    public SGMLParserState(String content) {
        this.content = content;
        this.i = 0;
        this.nodeStack = new Stack<SGMLNode>();
    }
}

public class SGMLParser {
    public static String readEntireFile(String filename) throws Exception {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        while (line != null) {
            content.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        return content.toString();
    }

    private static SGMLParserState skipWhitespace(SGMLParserState state) {
        while (state.i < state.content.length() && Character.isWhitespace(state.content.charAt(state.i))) {
            ++state.i;
        }
        return state;
    }

    private static SGMLParserState parseOpen(SGMLParserState state) {
        state = skipWhitespace(state);
        int closeIndex = state.content.indexOf('>', state.i);
        String openTagWithAttribs = state.content.substring(state.i + 1, closeIndex);
        String openTags[] = openTagWithAttribs.split("\\s+");
        String openTag = openTags[0];
        state.i += 2 + openTagWithAttribs.length();
        SGMLNode root = state.nodeStack.pop();
        SGMLNode node = new SGMLNode(openTag, "");
        root.children.add(node);
        root.value_is_children = true;
        state.nodeStack.push(root);
        state.nodeStack.push(node);
        return state;
    }

    private static SGMLParserState parseValue(SGMLParserState state) {
        state = skipWhitespace(state);
        String openTag = state.nodeStack.peek().tag;
        String closeTag = "</" + openTag + ">";

        // Keep parsing values until we find the close tag.
        while (!state.content.startsWith(closeTag, state.i)) {
            if (state.content.charAt(state.i) == '<') {
                state = parseOpen(state);
                state = parseValue(state);
                state = parseClose(state);
            } else {
                // Parse until start of text tag.
                int closeIndex = state.content.indexOf("<", state.i);
                String value = state.content.substring(state.i, closeIndex);
                SGMLNode node = state.nodeStack.pop();
                if (node.value_is_children) {
                    SGMLNode valueNode = new SGMLNode("", value);
                    node.children.add(valueNode);
                } else {
                    node.value = value;
                }
                state.nodeStack.push(node);
                state.i += value.length();
            }
            state = skipWhitespace(state);
        }
        return state;
    }

    private static SGMLParserState parseClose(SGMLParserState state) {
        state = skipWhitespace(state);
        String openTag = state.nodeStack.peek().tag;
        String closeTag = "</" + openTag + ">";
        int closeIndex = state.content.indexOf(closeTag, state.i);
        state.i = closeIndex + closeTag.length();
        state.nodeStack.pop();
        return state;
    }

    public static SGMLNode seekTag(ArrayList<SGMLNode> nodes, String tag) {
        for (SGMLNode n : nodes) {
            if (n.tag.contentEquals(tag)) {
                return n;
            }
        }
        return null;
    }

    public static SGMLNode parseSGML(String content) throws Exception {
        SGMLNode root = new SGMLNode("root", "");
        root.value_is_children = true;

        SGMLParserState state = new SGMLParserState(content);
        state.nodeStack.push(root);
        while (state.i < state.content.length() && !state.nodeStack.empty()) {
            state = skipWhitespace(state);
            if (state.i >= state.content.length()) {
                break; // Stop at EOF.
            }
            state = parseOpen(state);
            state = parseValue(state);
            state = parseClose(state);
        }

        return root;
    }

}
