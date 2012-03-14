/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stefanhuber.flower.db;

import at.stefanhuber.flower.core.Activity;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Stefan Huber
 */
public class JdbcActivity extends JdbcDaoSupport implements Activity {
    
    private static Logger logger = Logger.getLogger(JdbcActivity.class);

    //private static String ACTIVITY_ATTRIBUTES_UPDATE = "update `Activity` set Title = ? where ActivityURI = ?";
    private static String ACTIVITY_ATTRIBUTES_QUERY = "select * from `Activity` where ActivityURI = ?";
    private static String CREATE_NEW_ACTIVITY = "insert into `Activity` (ActivityURI,Title,CreationDate,UserURI) values (?,?,?,?)";
    private static String ADD_TASK_CONTEXT = "insert into `TaskContext` (ActivityURI,TaskURI,CaseURI) values (?,?,?)";
    private static String REMOVE_TASK_CONTEXT = "delete from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
    
    private String activityURI;
    
    private String userURI;
    private String title;
    private Date creationDate;
    
    private boolean updateMe = true;
    
    private void updateMe() {
        Map<String, Object> map = getJdbcTemplate().queryForMap(ACTIVITY_ATTRIBUTES_QUERY, activityURI);
        if (!map.isEmpty()) {
            this.title = map.containsKey("Title") ? (String) map.get("Title") : "";
            this.userURI = map.containsKey("UserURI") ? (String) map.get("UserURI") : "";
            this.creationDate = (Date) (map.containsKey("CreationDate") ? map.get("CreationDate") : null);
        }

        updateMe = false;
    }
    
    public JdbcActivity(DataSource dataSource, String title, String userURI, Date creationDate) {
        this.setDataSource(dataSource);
        this.title = title;
        this.userURI = userURI;
        this.creationDate = creationDate;
        
        createActivity();
        
        updateMe = false;        
    }
    
    public JdbcActivity(DataSource dataSource, String activityURI) {
        this.setDataSource(dataSource);
        this.activityURI = activityURI;        
        updateMe = true;
    }
    
    private void createActivity() {      
        if (activityURI == null) {
            // TODO: change and make better
            activityURI = "http://stefanhuber.at/test/" + "/activities/" + creationDate.getTime();
            getJdbcTemplate().update(CREATE_NEW_ACTIVITY,activityURI,title,creationDate,userURI);
        }
    }
    
    public String getActivityURI() {
        return activityURI;
    }

    public String getUserURI() {
        if (updateMe) {
            updateMe();
        }
        return userURI;
    }

    public Date getCreationDate() {
        if (updateMe) {
            updateMe();
        }
        return creationDate;
    }

    public String getTitle() {
        if (updateMe) {
            updateMe();
        }
        return title;
    }

    public void addTaskContexts(String caseURI, Collection<String> taskURIs) {
        Iterator<String> it = taskURIs.iterator(); String taskURI;
        while (it.hasNext()) {
            taskURI = it.next();
            logger.info("Add following Task Context: " + activityURI + ", " + taskURI + ", " + caseURI);
            getJdbcTemplate().update(ADD_TASK_CONTEXT,activityURI,taskURI,caseURI);
        }       
    }

    public void addTaskContexts(Map<String, Collection<String>> caseTaskURIs) {
        Iterator<String> it1 = caseTaskURIs.keySet().iterator();
        while (it1.hasNext()) {
            String caseURI = it1.next();
            Iterator<String> it2 = caseTaskURIs.get(caseURI).iterator();
            
            while (it2.hasNext()) {
                getJdbcTemplate().update(ADD_TASK_CONTEXT,activityURI,it2.next(),caseURI);
            } 
        }
    }

    public void removeTaskContexts(String caseURI, Collection<String> taskURIs) {
        Iterator<String> it = taskURIs.iterator();
        while (it.hasNext()) {
            getJdbcTemplate().update(REMOVE_TASK_CONTEXT, activityURI,it.next(),caseURI);
        }       
    }

    public void removeTaskContexts(Map<String, Collection<String>> caseTaskURIs) {
        Iterator<String> it1 = caseTaskURIs.keySet().iterator();
        while (it1.hasNext()) {
            String caseURI = it1.next();
            Iterator<String> it2 = caseTaskURIs.get(caseURI).iterator();
            
            while (it2.hasNext()) {
                getJdbcTemplate().update(REMOVE_TASK_CONTEXT,activityURI,it2.next(),caseURI);
            } 
        }
    }

    public List<String> getTaskURIs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getCaseURIs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
