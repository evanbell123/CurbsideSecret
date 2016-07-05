package curbsidesecretjava;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Evan
 */
public final class SecretTree {

    private String secretMessage = "";

    public SecretTree() throws IOException, UnsupportedEncodingException, MalformedURLException, ParseException {
        initializeTree();
    }

    public String getSecretMessage() {
        return secretMessage;
    }

    public void initializeTree() throws UnsupportedEncodingException, IOException, MalformedURLException, ParseException {
        JsonArray startId = new JsonArray();
        JsonPrimitive startWord = new JsonPrimitive("start");
        startId.add(startWord);

        LinkedList<Node> root = getNodes(startId);
        Node tree = buildTree(root.getFirst());
    }

    public static String getSessionId() throws MalformedURLException, UnsupportedEncodingException, IOException {
        URL url = new URL("http://challenge.shopcurbside.com/get-session");
        String sessionId = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {

            for (String line; (line = reader.readLine()) != null;) {
                sessionId += line;
            }
        }
        return sessionId;
    }

    public static LinkedList<Node> getNodes(JsonArray ids) throws MalformedURLException, UnsupportedEncodingException, IOException, ParseException {

        Iterator<JsonElement> it = ids.iterator();

        LinkedList<Node> nodes = new LinkedList<>();

        /*
         Token expires after 10 uses, so get a new one
         */
        String sessionId = getSessionId();

        while (it.hasNext()) {
            String id = it.next().getAsString();
            URL url = new URL("http://challenge.shopcurbside.com/" + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("Session", sessionId);

            Node p;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                String line;
                String processedLine = "";

                /*
                 Some property keys have random uppercase letters in them,
                 There we need to convert to lowercase
                 */
                while ((line = in.readLine()) != null) {
                    processedLine += line.toLowerCase();
                }

                /*
                 Parse JSON
                 */
                JsonParser parser = new JsonParser();

                JsonObject nodeJson = parser.parse(processedLine).getAsJsonObject();

                /*
                 Get Children nodes
                 */
                if (nodeJson.get("secret") == null) {
                    /*
                     Not all next values are arrays, so check if array
                     */
                    if (nodeJson.get("next").isJsonArray()) {
                        p = new Node(nodeJson.get("depth").getAsInt(), nodeJson.get("id").getAsString(), null, nodeJson.get("next").getAsJsonArray());
                    } else { //If not array, make an array of one id
                        JsonArray nextArray = new JsonArray();
                        JsonPrimitive nextId = new JsonPrimitive(nodeJson.get("next").getAsString());
                        nextArray.add(nextId);
                        p = new Node(nodeJson.get("depth").getAsInt(), nodeJson.get("id").getAsString(), null, nextArray);
                    }

                } else { // Leaf node with a secret has been found
                    p = new Node(nodeJson.get("depth").getAsInt(), nodeJson.get("id").getAsString(), nodeJson.get("secret").getAsString(), new JsonArray());
                }

                System.out.println(p);

                nodes.add(p);
            }
        }
        return nodes;
    }

    /*
     Traverse n-ary tree using DFS, while building the secret message along the way
     Secret messages are located at the leaves of the tree
     */
    public Node buildTree(Node root) throws UnsupportedEncodingException, IOException, MalformedURLException, ParseException {

        JsonArray ids = root.getNextIds();

        LinkedList<Node> nodes = getNodes(ids);

        root.setChildren(nodes);
        if (root.getSecret() == null) {
            ListIterator<Node> it = root.getChildren().listIterator();
            while (it.hasNext()) {
                Node child = it.next();

                if (child.getSecret() != null) {
                    this.secretMessage += child.getSecret();
                }
                buildTree(child);
            }
        }
        return root;
    }
}
