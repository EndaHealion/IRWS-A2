package apple_sauce.parsers;

import java.util.ArrayList;

public class SGMLNode {
    String tag;
    String value;
    ArrayList<SGMLNode> children;
    boolean value_is_children;

    public SGMLNode() {
        this.tag = "";
        this.value = "";
        this.children = new ArrayList<SGMLNode>();
        this.value_is_children = false;
    }

    public SGMLNode(String tag, String value) {
        this.tag = tag;
        this.value = value;
        this.children = new ArrayList<SGMLNode>();
        this.value_is_children = false;
    }

    private void printInternal(int indent) {
        for (int i = 0; i < indent; ++i) {
            System.out.print("  ");
        }
        System.out.println(this.tag);

        if (!this.value_is_children) {
            for (int i = 0; i < indent + 1; ++i) {
                System.out.print("  ");
            }
            System.out.println(this.value);
        } else {
            for (SGMLNode n : this.children) {
                n.printInternal(indent + 1);
            }
        }
    }

    public void print() {
        int indent = 0;
        this.printInternal(indent);
    }
}
