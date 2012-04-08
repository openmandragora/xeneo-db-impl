/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.core.security.User;
import org.xeneo.core.task.Case;
import org.xeneo.core.task.Task;
import org.xeneo.db.services.URIGenerator;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Stefan Huber
 */
public class JdbcCase extends JdbcDaoSupport implements Case {
    
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
    
    public JdbcCase(DataSource dataSource, String caseURI) {
        this.setDataSource(dataSource);
        this.caseURI = caseURI;        
        this.updateMe = true;        
    }
    
    public JdbcCase(DataSource dataSource, String caseTypeURI, String title, String description) {
        this.setDataSource(dataSource);
        this.caseTypeURI = caseTypeURI;
        this.title = title; 
        this.description = description;
        this.date = Calendar.getInstance().getTime();
        
        createCase();
        this.updateMe = false;
    }
    
    public JdbcCase(DataSource dataSource, String caseTypeURI, String title) {
        this(dataSource,caseTypeURI,title,"");
    }
    
    private void createCase() {
        if (caseURI == null) {            
            
            caseURI = URIGenerator.getInstance().generateURI("case");
            
            getJdbcTemplate().update(CREATE_NEW_CASE,caseURI,title,date,caseTypeURI);
        }
    }
    
    public String getCaseURI() {
        return this.caseURI;
    }
    
    private void updateMe() {
        Map<String,Object> map = getJdbcTemplate().queryForMap(CASE_ATTRIBUTES_QUERY,this.caseURI);
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

    public void updateTitle(String title) {
        this.title = title;
        getJdbcTemplate().update(CASE_ATTRIBUTES_UPDATE,title,this.description,this.caseURI);               
    }

    public void updateDescription(String description) {
        this.description = description;
        getJdbcTemplate().update(CASE_ATTRIBUTES_UPDATE,this.title,description,this.caseURI);         
    }

    public List<User> getParticipants() {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
