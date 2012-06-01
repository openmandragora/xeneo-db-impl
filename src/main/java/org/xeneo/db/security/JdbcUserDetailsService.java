/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author Stefan Huber
 */
public class JdbcUserDetailsService extends JdbcDaoSupport implements UserDetailsService {

    private static String GET_USER_BY_USERNAME = "select * from `User` where UserURI = ?";
    

    public UserDetails loadUserByUsername(String userURI) throws UsernameNotFoundException {
        List<JdbcUser> users = loadUsersByUsername(userURI);
        if (users.size() < 1) {
            throw new UsernameNotFoundException("There is no user with Username: " + userURI + " registered.");
        }
        
        return users.get(0);       
    }

    private List<JdbcUser> loadUsersByUsername(String userURI) {
        return getJdbcTemplate().query(GET_USER_BY_USERNAME, new String[]{userURI}, new RowMapper<JdbcUser>() {
            public JdbcUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                String username = rs.getString("UserName");
                String password = rs.getString("Password");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String userURI = rs.getString("UserURI");
                
                return new JdbcUser(username, password, firstName, lastName, userURI);
            }
        });
    }
}
