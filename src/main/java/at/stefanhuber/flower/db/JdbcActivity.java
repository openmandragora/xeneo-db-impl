/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stefanhuber.flower.db;

import at.stefanhuber.flower.core.Activity;
import at.stefanhuber.flower.db.util.URIGenerator;
import java.util.*;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Stefan Huber
 */
public class JdbcActivity extends JdbcDaoSupport implements Activity {

    private static Logger logger = Logger.getLogger(JdbcActivity.class);
    
    private static String ACTIVITY_ATTRIBUTES_QUERY = "select * from `Activity` where ActivityURI = ?";
    private static String CREATE_NEW_ACTIVITY = "insert into `Activity` (ActivityURI,Title,CreationDate,UserURI) values (?,?,?,?)";
    private static String ADD_TASK_CONTEXT = "insert into `TaskContext` (ActivityURI,TaskURI,CaseURI) values (?,?,?)";
    private static String REMOVE_TASK_CONTEXT = "delete from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
    private static String TASK_CONTEXT_COUNT_QUERY = "select count(*) from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
    private static String TASK_CONTEXT_BY_ACTIVITY_AND_CASE = "select * from `TaskContext` where ActivityURI = ? and CaseURI = ?";
    private static String TASK_CONTEXT_BY_ACTIVITY = "select * from `TaskContext` where ActivityURI = ?";
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
            activityURI = URIGenerator.getInstance().generateURI("activity");
            getJdbcTemplate().update(CREATE_NEW_ACTIVITY, activityURI, title, creationDate, userURI);
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
        Iterator<String> it = taskURIs.iterator();
        String taskURI;
        while (it.hasNext()) {
            taskURI = it.next();
            if (getJdbcTemplate().queryForInt(TASK_CONTEXT_COUNT_QUERY, activityURI, taskURI, caseURI) < 1) {
                logger.info("Add following Task Context: " + activityURI + ", " + taskURI + ", " + caseURI);
                getJdbcTemplate().update(ADD_TASK_CONTEXT, activityURI, taskURI, caseURI);
            }
        }
    }

    public void addTaskContexts(Map<String, Collection<String>> caseTaskURIs) {
        Iterator<String> it1 = caseTaskURIs.keySet().iterator(); String caseURI, taskURI;
        while (it1.hasNext()) {
            caseURI = it1.next();
            Iterator<String> it2 = caseTaskURIs.get(caseURI).iterator();
            while (it2.hasNext()) {
                taskURI = it2.next();
                if (getJdbcTemplate().queryForInt(TASK_CONTEXT_COUNT_QUERY, activityURI, taskURI, caseURI) < 1) {
                    logger.info("Add following Task Context: " + activityURI + ", " + taskURI + ", " + caseURI);
                    getJdbcTemplate().update(ADD_TASK_CONTEXT, activityURI, taskURI, caseURI);
                }
            }
        }
    }

    public void removeTaskContexts(String caseURI, Collection<String> taskURIs) {
        Iterator<String> it = taskURIs.iterator();
        while (it.hasNext()) {
            getJdbcTemplate().update(REMOVE_TASK_CONTEXT, activityURI, it.next(), caseURI);
        }
    }

    public void removeTaskContexts(Map<String, Collection<String>> caseTaskURIs) {
        Iterator<String> it1 = caseTaskURIs.keySet().iterator();
        while (it1.hasNext()) {
            String caseURI = it1.next();
            Iterator<String> it2 = caseTaskURIs.get(caseURI).iterator();

            while (it2.hasNext()) {
                getJdbcTemplate().update(REMOVE_TASK_CONTEXT, activityURI, it2.next(), caseURI);
            }
        }
    }

    public Collection<String> getTaskContextsByCaseURI(String caseURI) {
        Collection<String> tasks = new ArrayList<String>();
        List<Map<String, Object>> result = getJdbcTemplate().queryForList(TASK_CONTEXT_BY_ACTIVITY_AND_CASE, activityURI, caseURI);
        if (!result.isEmpty()) {
            Iterator<Map<String, Object>> it = result.iterator();
            Map<String, Object> map;
            while (it.hasNext()) {
                map = it.next();
                if (map.containsKey("TaskURI")) {
                    tasks.add((String) map.get("TaskURI"));
                }
            }
        }

        return tasks;
    }

    public Map<String, Collection<String>> getTaskContexts() {
        Map<String, Collection<String>> response = new HashMap<String, Collection<String>>();

        List<Map<String, Object>> result = getJdbcTemplate().queryForList(TASK_CONTEXT_BY_ACTIVITY, activityURI);
        if (!result.isEmpty()) {
            Iterator<Map<String, Object>> it = result.iterator();
            Map<String, Object> map;
            String caseURI, taskURI;
            Collection<String> tasks;
            while (it.hasNext()) {
                map = it.next();
                if (map.containsKey("CaseURI") && map.containsKey("TaskURI")) {
                    caseURI = (String) map.get("CaseURI");
                    taskURI = (String) map.get("TaskURI");
                    if (response.containsKey(caseURI)) {
                        tasks = response.get(caseURI);
                        if (!tasks.contains(taskURI)) {
                            tasks.add(taskURI);
                            response.put(caseURI, tasks);
                        }
                    } else {
                        tasks = new ArrayList<String>();
                        tasks.add(taskURI);
                        response.put(caseURI, tasks);
                    }
                }
            }
        }

        return response;
    }
}
