/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stefanhuber.flower.db.security;

import at.stefanhuber.flower.core.security.User;
import at.stefanhuber.flower.core.security.UserServices;
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
    
}
