/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db.services;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

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
public class URIGenerator extends JdbcDaoSupport {

    private static String GET_NUMBER_BY_URI_QUERY = "select Number from URIGenerator where BaseURI = ?";
    private static String INSERT_NEW_URI = "insert into `URIGenerator` (BaseURI,Number) values (?,?)";
    private static String GET_BY_URI_QUERY = "select count(*) from URIGenerator where BaseURI = ?";
    private static String UPDATE_URI_NUMBER = "update `URIGenerator` set Number = ? where BaseURI = ?";
    
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
            uri.toLowerCase().trim();
            
            baseURI = new URI(uri);
            
            if (getJdbcTemplate().queryForInt(GET_BY_URI_QUERY,uri) > 0) {
                n = getJdbcTemplate().queryForLong(GET_NUMBER_BY_URI_QUERY,uri);
            } else {
                n = 0;
                getJdbcTemplate().update(INSERT_NEW_URI, uri, n);                
            }        
            
            logger.info(baseURI.toASCIIString() + " is set as base URI with already " + n + " URIs generated.");            
        } catch (URISyntaxException ex) {
            logger.error("The given URI is not valid: " + uri + " Error message: " + ex.getMessage());
        }
    }

    public String getStringRepresentation(long n) {
        
        // handle 0 and smaller values as exceptions
        if (n <= 0)
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
            
            getJdbcTemplate().update(UPDATE_URI_NUMBER, n, baseURI.toASCIIString());                  
        } catch (URISyntaxException ex) {
            logger.error("The URI couldn't be constructed due to the following error: " + ex.getMessage());
        }        
        return out.toASCIIString();
    }
    
    public String generateURI() {
        return generateURI("");
    }
}
