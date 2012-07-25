/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import java.util.*;
import org.xeneo.core.security.User;
import org.xeneo.core.task.Case;
import org.xeneo.core.task.Task;
import org.xeneo.db.services.URIGenerator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xeneo.core.security.UserManager;

/**
 *
 * @author Stefan Huber
 */
public class JdbcCase implements Case {
    
    private JdbcTemplate jdbcTemplate;
    private UserManager userManager;
    
    private static String CASE_ATTRIBUTES_QUERY = "select * from `Case` where CaseURI = ?";
    private static String CASE_ATTRIBUTES_UPDATE = "update `Case` set Title = ?, Description = ? where CaseURI = ?";
    private static String CREATE_NEW_CASE = "insert into `Case` (CaseURI,Title,CreationDate,CaseTypeURI) values (?,?,?,?)";
    
    private String caseURI;
    private String caseTypeURI;
    
    // updateMe flag indicates iff the Case data should be updated!
    private boolean updateMe = true;
    
    private String title;
    private String description;    
    private Date date;
    
    /* retrieve old case */
    public JdbcCase(JdbcTemplate jdbcTemplate,UserManager userManager,String caseURI) {     
        this.jdbcTemplate = jdbcTemplate;
        this.userManager = userManager;
        this.caseURI = caseURI;        
        this.updateMe = true;        
    }
    
    /* create new case, with an additional call to createCase(), TODO: these needs to be refactored completely...*/
    public JdbcCase(JdbcTemplate jdbcTemplate,UserManager userManager, String caseTypeURI, String title, String description) {        
        this.caseTypeURI = caseTypeURI;
        this.title = title; 
        this.description = description;
        this.jdbcTemplate = jdbcTemplate;
        this.userManager = userManager;
        this.date = Calendar.getInstance().getTime();      
        
        createCase();
        this.updateMe = false;       
    }
    
    private void createCase() {
        if (caseURI == null) {            
            
            caseURI = URIGenerator.getInstance().generateURI("case");
            
            jdbcTemplate.update(CREATE_NEW_CASE,caseURI,title,date,caseTypeURI);
        }
    }
    
    public String getCaseURI() {
        return this.caseURI;
    }
    
    private void updateMe() {
        Map<String,Object> map = jdbcTemplate.queryForMap(CASE_ATTRIBUTES_QUERY,this.caseURI);
        if (!map.isEmpty()) {
            this.title = map.containsKey("Title") ? (String) map.get("Title") : "";
            this.description = map.containsKey("Description") ? (String) map.get("Description") : "";
            this.date = (Date) (map.containsKey("CreationDate") ? map.get("CreationDate") : null);
            this.caseTypeURI = (String) (map.containsKey("CaseTypeURI") ? map.get("CaseTypeURI") : null);
        }
        
        updateMe = false;
    }

    public String getTitle() {        
        if (updateMe) {
            updateMe();
        }            
        return title;       
    }

    public String getDescription() {
        if (updateMe) {
            updateMe();
        }        
        return description;
    }

    public Date getCreationDate() {
        if (updateMe) {
            updateMe();
        } 
        return date;
    }
    
    public String getCaseTypeURI() {
        if (updateMe) {
            updateMe();
        }
        return this.caseTypeURI;
    }
    
    public void update(String title, String description) {
        this.title = title;
        this.description = description;
        
        jdbcTemplate.update(CASE_ATTRIBUTES_UPDATE,title,description,caseURI);
    }

    public List<User> getParticipants() {
        return userManager.listUsersByCaseURI(caseURI);
    }

    public List<Task> getPreviousTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Task> getRecommendedTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Task getCurrentTask() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addParticipants(Collection<String> participants) {
        userManager.addUsersToCase(participants, caseURI);
    }
}
