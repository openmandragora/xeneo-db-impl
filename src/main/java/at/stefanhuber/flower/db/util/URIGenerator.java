/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stefanhuber.flower.db.util;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;

/**
 *
 * This URI Generator is based on a mapping of a long number to a string
 * representation Thus, a long value of <bold>10</bold> maps to a string of
 * <bold>9</bold>
 *
 * The state of the long value has to be keept in persitence in order to prevent
 * collisions.
 *
 * @author Stefan Huber
 */
public class URIGenerator {

    private static Logger logger = Logger.getLogger(URIGenerator.class);
    private static String symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static int length; // 62 in standard case
    /*
     * This variable represents the amount of URIs already created and maps to a
     * string representation.
     */
    private static long n;
    private URI baseURI;

    private URIGenerator() {
        length = symbols.length();
        n = 0;
    }
    
    private static URIGenerator instance = null;
    
    public static URIGenerator getInstance() {
        if (instance == null) {
            instance = new URIGenerator();            
        }
        
        return instance;
    }

    public void setBaseURI(String uri) {
        try {
            baseURI = new URI(uri);
            logger.info(baseURI.toASCIIString() + " is set as base URI.");
            
        } catch (URISyntaxException ex) {
            logger.error("The given URI is not valid: " + uri + " Error message: " + ex.getMessage());
        }
    }

    public String getStringRepresentation(long n) {
        
        // handle 0 as an exception
        if (n == 0)
            return "0";
               
        String output = "";
        int y = 0;
        long x = n;
        
        while (n >= (Math.pow(length,y))) {
            output = symbols.charAt((int)x%length) + output;
            x /= length;
            y++;
        }       
        
        return output;
    }

    public String generateURI(String path) { 
        URI out = null; String u;
        try {
            if (!path.isEmpty())
                u = baseURI.getScheme() + "://" + baseURI.getAuthority() + "/" + path + "#" + this.getStringRepresentation(n++);
            else
                u = baseURI.getScheme() + "://" + baseURI.getAuthority() + "#" + this.getStringRepresentation(n++);
            
            out = new URI(u);            
        } catch (URISyntaxException ex) {
            logger.error("The URI couldn't be constructed due to the following error: " + ex.getMessage());
        }        
        return out.toASCIIString();
    }
    
    public String generateURI() {
        return generateURI("");
    }
}
