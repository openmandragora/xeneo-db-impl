/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stefanhuber.flower.db;

import at.stefanhuber.flower.core.Activity;
import at.stefanhuber.flower.core.Task;
import at.stefanhuber.flower.core.security.User;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Stefan Huber
 */
public class JdbcTask extends JdbcDaoSupport implements Task {

    private static String TASK_ATTRIBUTES_UPDATE = "update `Task` set Title = ?, Description = ? where TaskURI = ?";
    private static String TASK_ATTRIBUTES_QUERY = "select * from `Task` where TaskURI = ?";
    private static String CREATE_NEW_TASK = "insert into `Task` (TaskURI,Title,Description) values (?,?,?)";
    
    private String taskURI;
    private String title;
    private String description;
    private boolean updateMe = true;

    public JdbcTask(DataSource dataSource, String taskURI) {
        this.setDataSource(dataSource);
        this.taskURI = taskURI;
        this.updateMe = true;
    }

    public JdbcTask(DataSource dataSource, String title, String description) {
        this.setDataSource(dataSource);
        this.title = title;
        this.description = description;

        createTask();

        updateMe = false;
    }

    private void createTask() {
        if (taskURI == null) {

            // TODO: make it better
            taskURI = "http://stefanhuber.at/test/" + Calendar.getInstance().getTimeInMillis() + "/task/" + title.toLowerCase().trim().replace(' ', '-');

            getJdbcTemplate().update(CREATE_NEW_TASK, taskURI, title, description);
        }
    }

    private void updateMe() {
        Map<String, Object> map = getJdbcTemplate().queryForMap(TASK_ATTRIBUTES_QUERY, this.taskURI);
        if (!map.isEmpty()) {
            this.title = map.containsKey("Title") ? (String) map.get("Title") : "";
            this.description = map.containsKey("Description") ? (String) map.get("Description") : "";
        }

        updateMe = false;
    }

    public String getTaskURI() {
        return taskURI;
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

    public List<User> getParticipants() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Activity> getRecentActivities(int number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateTitle(String title) {
        this.title = title;
        getJdbcTemplate().update(TASK_ATTRIBUTES_UPDATE, title, description, taskURI);        
    }

    public void updateDescription(String description) {
        this.description = description;
        getJdbcTemplate().update(TASK_ATTRIBUTES_UPDATE, title, description, taskURI);        
    }
}
