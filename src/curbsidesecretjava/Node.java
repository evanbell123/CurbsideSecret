package curbsidesecretjava;

import com.google.gson.JsonArray;
import java.util.LinkedList;

/**
 *
 * @author Evan
 */
public class Node {

    private final int depth;
    private final String id;
    private final JsonArray next;
    private LinkedList<Node> children;
    private final String secret;

    public Node(int depth, String id, String secret, JsonArray next) {
        this.depth = depth;
        this.id = id;
        this.secret = secret;
        this.next = next;
        children = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "Node{" + "depth=" + depth + ", id=" + id + ", next=" + next + ", children=" + children + ", secret=" + secret + '}';
    }

    public String getSecret() {
        return secret;
    }

    public LinkedList<Node> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<Node> children) {
        this.children = children;
    }

    public JsonArray getNextIds() {
        return this.next;
    }
}
