package org.xeneo.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xeneo.core.activity.Activity;
import org.xeneo.core.activity.ActivityManager;
import org.xeneo.core.activity.Object;

public class JdbcActivityManager implements ActivityManager {
	
	private Logger logger = Logger.getLogger(JdbcActivityManager.class);

	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private static String ADD_ACTIVITY = "insert into Activity (ActivityURI,CreationDate,UserURI,ActionURI,ObjectURI,TargetURI,ObjectName,TargetName,ObjectTypeURI,TargetTypeURI) values (?,?,?,?,?,?,?,?,?,?)";
	
	private static String ADD_TASK_CONTEXT = "insert into `TaskContext` (ActivityURI,TaskURI,CaseURI) values (?,?,?)";
	private static String REMOVE_TASK_CONTEXT = "delete from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
	private static String TASK_CONTEXT_COUNT_QUERY = "select count(*) from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
	private static String TASK_CONTEXT_BY_ACTIVITY_AND_CASE = "select * from `TaskContext` where ActivityURI = ? and CaseURI = ?";
	private static String TASK_CONTEXT_BY_ACTIVITY = "select * from `TaskContext` where ActivityURI = ?";

	public void addActivity(Activity a) {
		Object obj = a.getObject();
		Object tar = a.getTarget();
		
		jdbcTemplate.update(ADD_ACTIVITY, a.getActivityURI(),a.getCreationDate(),a.getActorURI(),a.getActionURI(),obj.getObjectURI(),tar.getObjectURI(),obj.getObjectName(),tar.getObjectName(),obj.getObjectTypeURI(),tar.getObjectTypeURI());

	}

	public void addActivity(Activity activity, Map<String, Collection<String>> caseTaskURIs) {
		String activityURI = activity.getActivityURI();		
		this.addTaskContexts(activityURI, caseTaskURIs);
	}
	
	public void addActivity(Activity activity, String caseURI, Collection<String> taskURIs) {
		String activityURI = activity.getActivityURI();		
		this.addTaskContexts(activityURI, caseURI, taskURIs);		
	}

	public void addTaskContexts(String activityURI, String caseURI, Collection<String> taskURIs) {
        Iterator<String> it = taskURIs.iterator();
        String taskURI;
        while (it.hasNext()) {
            taskURI = it.next();
            if (jdbcTemplate.queryForInt(TASK_CONTEXT_COUNT_QUERY, activityURI, taskURI, caseURI) < 1) {
                logger.info("Add following Task Context: " + activityURI + ", " + taskURI + ", " + caseURI);
                jdbcTemplate.update(ADD_TASK_CONTEXT, activityURI, taskURI, caseURI);
            }
        }
	}

	public void addTaskContexts(String activityURI, Map<String, Collection<String>> caseTaskURIs) {
        Iterator<String> it1 = caseTaskURIs.keySet().iterator(); String caseURI, taskURI;
        while (it1.hasNext()) {
            caseURI = it1.next();
            Iterator<String> it2 = caseTaskURIs.get(caseURI).iterator();
            while (it2.hasNext()) {
                taskURI = it2.next();
                if (jdbcTemplate.queryForInt(TASK_CONTEXT_COUNT_QUERY, activityURI, taskURI, caseURI) < 1) {
                    logger.info("Add following Task Context: " + activityURI + ", " + taskURI + ", " + caseURI);
                    jdbcTemplate.update(ADD_TASK_CONTEXT, activityURI, taskURI, caseURI);
                }
            }
        }
	}

	public void removeTaskContexts(String activityURI, String caseURI, Collection<String> taskURIs) {
        Iterator<String> it = taskURIs.iterator();
        while (it.hasNext()) {
        	jdbcTemplate.update(REMOVE_TASK_CONTEXT, activityURI, it.next(), caseURI);
        }
	}

	public void removeTaskContexts(String activityURI, Map<String, Collection<String>> caseTaskURIs) {
        Iterator<String> it1 = caseTaskURIs.keySet().iterator();
        while (it1.hasNext()) {
            String caseURI = it1.next();
            Iterator<String> it2 = caseTaskURIs.get(caseURI).iterator();

            while (it2.hasNext()) {
                jdbcTemplate.update(REMOVE_TASK_CONTEXT, activityURI, it2.next(), caseURI);
            }
        }
	}

	public Map<String, Collection<String>> getTaskContexts(String activityURI) {
        Map<String, Collection<String>> response = new HashMap<String, Collection<String>>();

        List<Map<String, java.lang.Object>> result = jdbcTemplate.queryForList(TASK_CONTEXT_BY_ACTIVITY, activityURI);
        if (!result.isEmpty()) {
            Iterator<Map<String, java.lang.Object>> it = result.iterator();
            Map<String, java.lang.Object> map;
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
	
	/*
	 * Maybe this could be used in the future...
	 * 
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
    */
}
