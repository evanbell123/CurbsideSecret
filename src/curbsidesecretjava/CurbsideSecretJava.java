package curbsidesecretjava;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;

/**
 *
 * @author Evan
 */
public class CurbsideSecretJava {

    /**
     * @param args the command line arguments
     * @throws java.io.UnsupportedEncodingException
     * @throws java.net.MalformedURLException
     * @throws java.text.ParseException
     */
    public static void main(String[] args) throws IOException, UnsupportedEncodingException, MalformedURLException, ParseException {

        SecretTree secretTree = new SecretTree();

        String secretMessage = secretTree.getSecretMessage();

        System.out.println(secretMessage);
    }

}
