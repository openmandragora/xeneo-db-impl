/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db.security;

import org.xeneo.core.security.User;
import org.xeneo.core.services.UserServices;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Stefan Huber
 */
public class JdbcUserServices extends JdbcDaoSupport implements UserServices {

    protected JdbcUser getCurrentJdbcUser() {                
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        JdbcUser user = null;
        if (principal instanceof JdbcUser) {
            user = (JdbcUser) principal;
        }
        
        return user;
    }
    
    public User getCurrentUser() {
        return getCurrentJdbcUser();
    }

    public String getCurrentUserURI() {
        return getCurrentJdbcUser().getUserURI();
    }

    public String getCurrentUsername() {
        return getCurrentJdbcUser().getUsername();
    }

    public org.xeneo.core.security.User getUserMapping(String externalUsername, String pluginURI) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
