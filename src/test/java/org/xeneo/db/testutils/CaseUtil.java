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
package org.xeneo.db.testutils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.xeneo.core.task.Case;
import org.xeneo.core.task.CaseManager;
import org.xeneo.core.task.Task;
import org.xeneo.core.task.TaskContextManager;

/**
 *
 * @author Stefan
 */
public class CaseUtil {
    
    @Autowired
    private CaseManager caseManager;
    
    @Autowired
    private TaskContextManager contextManager;
    
    public List<String> createCasesIntoDatabase(String caseTypeURI,int n) {
        String title = "Test Case Title " + Calendar.getInstance().getTimeInMillis();
        String description = "Test Case Description " + Calendar.getInstance().getTimeInMillis();
        
        List<String> caseURIs = new ArrayList<String>();
        
        for (int i = 0; i < n; i++) {            
            Case c = caseManager.createCase(caseTypeURI, title + " " + i, description + " " + i);
            caseURIs.add(c.getCaseURI());
        }    
        
        return caseURIs;
    }
    
    public List<String> createTaskTestData(int n) {
        String title = "Test Task Title " + Calendar.getInstance().getTimeInMillis();
        String description = "Test Task Description " + Calendar.getInstance().getTimeInMillis();
        List<String> taskURIs = new ArrayList<String>();
        
        for (int i = 0; i < n; i++) {            
            Task t = caseManager.createTask(title + " " + i, description + " " + i);
            taskURIs.add(t.getTaskURI());
        }    
        
        return taskURIs;        
    }
    
    public void createTaskContexts(String activityURI, List<String> tasks, List<String> cases) {
              
        for (String caseURI : cases) {
            
            contextManager.addTaskContexts(activityURI, caseURI, tasks);
        
        }
    }

    
}
