/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.core.security.User;
import org.xeneo.core.task.Task;
import org.xeneo.db.services.URIGenerator;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Stefan Huber
 */
public class JdbcTask implements Task {

    private static String TASK_ATTRIBUTES_UPDATE = "update `Task` set Title = ?, Description = ? where TaskURI = ?";
    private static String TASK_ATTRIBUTES_QUERY = "select * from `Task` where TaskURI = ?";
    private static String CREATE_NEW_TASK = "insert into `Task` (TaskURI,Title,Description) values (?,?,?)";
    
    private String taskURI;
    private String title;
    private String description;
    private boolean updateMe = true;
    
    private JdbcTemplate jdbcTemplate;

    public JdbcTask(JdbcTemplate jdbcTemplate, String taskURI) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskURI = taskURI;
        this.updateMe = true;
    }

    public JdbcTask(JdbcTemplate jdbcTemplate, String title, String description) {
        this.jdbcTemplate = jdbcTemplate;
        this.title = title;
        this.description = description;

        createTask();

        updateMe = false;
    }

    private void createTask() {
        if (taskURI == null) {           
            taskURI = URIGenerator.getInstance().generateURI("task");
            jdbcTemplate.update(CREATE_NEW_TASK, taskURI, title, description);
        }
    }

    private void updateMe() {
        Map<String, Object> map = jdbcTemplate.queryForMap(TASK_ATTRIBUTES_QUERY, this.taskURI);
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

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
        jdbcTemplate.update(TASK_ATTRIBUTES_UPDATE, title, description, taskURI);  
        updateMe = false;
    }
}
