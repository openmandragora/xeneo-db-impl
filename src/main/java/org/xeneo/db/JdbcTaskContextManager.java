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
package org.xeneo.db;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xeneo.core.task.TaskContextManager;

/**
 *
 * @author Stefan Huber
 */
public class JdbcTaskContextManager implements TaskContextManager {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTaskContextManager.class);
           
    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private static String ADD_TASK_CONTEXT = "insert into `TaskContext` (ActivityURI,TaskURI,CaseURI) values (?,?,?)";
    private static String REMOVE_TASK_CONTEXT = "delete from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
    private static String TASK_CONTEXT_COUNT_QUERY = "select count(*) from `TaskContext` where ActivityURI = ? and TaskURI = ? and CaseURI = ?";
    private static String TASK_CONTEXT_BY_ACTIVITY = "select * from `TaskContext` where ActivityURI = ?";

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
        Iterator<String> it1 = caseTaskURIs.keySet().iterator();
        String caseURI, taskURI;
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
}
