/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.core.activity.OldActivity;
import org.xeneo.db.services.URIGenerator;
import java.util.*;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Stefan Huber
 */
public class JdbcActivity extends JdbcDaoSupport {

    private static Logger logger = Logger.getLogger(JdbcActivity.class);
    
    private static String ADD_TASK_CONTEXT = "insert into `TaskContext` (ActivityURI,TaskURI,CaseURI) values (?,?,?)";
    private static String REMOVE_TASK_CONTEXT = "delete from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
    private static String TASK_CONTEXT_COUNT_QUERY = "select count(*) from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
    private static String TASK_CONTEXT_BY_ACTIVITY_AND_CASE = "select * from `TaskContext` where ActivityURI = ? and CaseURI = ?";
    private static String TASK_CONTEXT_BY_ACTIVITY = "select * from `TaskContext` where ActivityURI = ?";
    
    private String activityURI;
    private String userURI;
    private String actionURI;
    private String objectURI;
    private String objectName;
    private String objectTypeURI;
    private String targetURI;
    private String targetName;
    private String targetTypeURI;
    private String providerURI;
    private Date creationDate;
    
    public String getActivityURI() {
		return activityURI;
	}

	public void setActivityURI(String activityURI) {
		this.activityURI = activityURI;
	}

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}

	public String getActionURI() {
		return actionURI;
	}

	public void setActionURI(String actionURI) {
		this.actionURI = actionURI;
	}

	public String getObjectURI() {
		return objectURI;
	}

	public void setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getTargetURI() {
		return targetURI;
	}

	public void setTargetURI(String targetURI) {
		this.targetURI = targetURI;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getProviderURI() {
		return providerURI;
	}

	public void setProviderURI(String providerURI) {
		this.providerURI = providerURI;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
