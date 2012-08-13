/*
 * Copyright 2012 XENEO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xeneo.db.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.xeneo.core.security.User;
import org.xeneo.core.security.UserManager;

/**
 *
 * @author Stefan Huber
 */
public class JdbcUserManager implements UserManager, UserDetailsService {
        
    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private static final String GET_USER_BY_USERURI = "select * from `User` where UserURI = ?";
    private static final String GET_USER_BY_URI = "select count(*) from `User` where UserURI = ?";
    private static final String UPDATE_USER = "update `User` set FirstName = ?, LastName = ?, Email = ?, Password = ? where UserURI = ?";
    private static final String ADD_USER = "insert into `User` (UserURI,FirstName,LastName,Email,Password) values (?,?,?,?,?)";
    private static final String ADD_USER_TO_CASE = "insert into Participant (UserURI,CaseURI) values (?,?)";
    private static final String GET_USERS_BY_CASE_URI = "select * from `User` u inner join Participant p on u.UserURI = p.UserURI where p.CaseURI = ?";
    private static final String ADD_USERS_TO_CASE = "insert into Participant (UserURI,CaseURI) values %s";
    private static final String ADD_USER_MAPPING = "insert into UserMapping (userURI,actorURI) values (?,?)";

    public JdbcUser loadUserByUsername(String userURI) throws UsernameNotFoundException {
        List<JdbcUser> users = loadUsersByUsername(userURI);
        if (users.size() < 1) {
            throw new UsernameNotFoundException("There is no user with UserURI: " + userURI + " registered.");
        }
        
        return users.get(0);       
    }

    private List<JdbcUser> loadUsersByUsername(String userURI) {
        return jdbcTemplate.query(GET_USER_BY_USERURI, new String[]{userURI}, new RowMapper<JdbcUser>() {
            public JdbcUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                           
                String password = rs.getString("Password");
                String email = rs.getString("Email");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String userURI = rs.getString("UserURI");                              
                
                return new JdbcUser(userURI, firstName, lastName, email, password);
            }
        });
    }
    
    private static class UserRowMapper implements RowMapper {
        public JdbcUser mapRow(ResultSet rs, int i) throws SQLException {
            String userURI = rs.getString("UserURI");
            String firstName = rs.getString("FirstName");
            String lastName = rs.getString("LastName");
            String email = rs.getString("Email");
            String password = rs.getString("Password");
            
            return new JdbcUser(userURI,firstName,lastName,email,password);
        }
    }
    
    // TODO: check for username or userURI
    public boolean isExistingUser(String userURI) {
        if (jdbcTemplate.queryForInt(GET_USER_BY_URI, userURI) > 0) {
            return true;
        }        
        return false;
    }
    
    private void checkUser(User user) {
        Assert.notNull(user.getFirstName(), "User object has no first name.");
        Assert.notNull(user.getLastName(), "User object has no last name.");
        Assert.notNull(user.getEmail(), "User object has no Email adress.");
        Assert.notNull(user.getPassword(), "User object has no Password.");        
    }

    public void addUser(User user) {
        checkUser(user);
        
        // TODO: maybe throw exception if it is different
        if (!isExistingUser(user.getUserURI())) {
            jdbcTemplate.update(ADD_USER, user.getUserURI(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getPassword());
        }
    }
    
    public void updateUser(User user) {
        checkUser(user);
        
        // TODO: maybe throw exception if it is different
        if (isExistingUser(user.getUserURI())) {
            jdbcTemplate.update(UPDATE_USER, user.getFirstName(),user.getLastName(),user.getEmail(),user.getPassword(),user.getUserURI());
        }
    }   

    public List<User> listUsersByCaseURI(String caseURI) {
        return jdbcTemplate.query(GET_USERS_BY_CASE_URI, new UserRowMapper(), caseURI);
    }

    public void addUserToCase(String userURI, String caseURI) {
        jdbcTemplate.update(ADD_USER_TO_CASE,userURI,caseURI);
    }
    
    public void addUsersToCase(Collection<String> userURIs, String caseURI) {
        String values = "";
        for (String uri : userURIs) {
            values += ",('"+ uri +"','"+caseURI+"')";
        }
        // substring 1 to strip the first ","               
        jdbcTemplate.update(String.format(ADD_USERS_TO_CASE, values.substring(1)));
    }
    
    public void addUserMapping(String userURI, String actorURI) {
        jdbcTemplate.update(ADD_USER_MAPPING,userURI,actorURI);
    }
    
    
}
