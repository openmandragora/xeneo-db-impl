/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.xeneo.core.security.User;
import org.xeneo.core.services.UserServices;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.context.SecurityContextHolder;
import org.xeneo.db.security.JdbcUser;

/**
 *
 * @author Stefan Huber
 */
public class JdbcUserServices implements UserServices {

    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private static String GET_JDBCUSER = "select * from 'User' where UserURI = ?";
    
    protected JdbcUser getCurrentJdbcUser() {                
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JdbcUser user = null;
        if (principal instanceof JdbcUser) {
        user = (JdbcUser) principal;
        }
        return user;
        
         }
                
    public JdbcUser getCurrentUser() {
        return getCurrentJdbcUser();
    }

    public String getCurrentUserURI() {
        return getCurrentJdbcUser().getUserURI();
    }

    
}
