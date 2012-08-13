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
package org.xeneo.db.recommendation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xeneo.core.activity.Activity;
import org.xeneo.core.activity.ActivityRepository;
import org.xeneo.core.task.CaseManager;
import org.xeneo.core.task.CaseType;
import org.xeneo.db.services.URIGenerator;
import org.xeneo.db.testutils.CaseUtil;

/**
 *
 * @author Stefan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/recommendation-test-config.xml")
public class TaskContextGeneratorTest {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskContextGeneratorTest.class);

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Autowired
    private CaseUtil util;

    @Autowired
    private CaseManager manager;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    public String generateActivities(Date date) {
        
        String uri = URIGenerator.getInstance().generateURI();
                
        Activity a = new Activity.Builder()
            .setActivityURI(uri)
            .setActionURI("http://action.com")
            .setCreationDate(date)
            .setSummary("some Summary for ... ")
            .setDescription("some Description for ... ")
            .setActivityProvider("http://provider.com", "Test Provider", "http://someblub.com/DMS")
            .setObject("http://object.com", "object", "http://someobject.com/sometype")
            .setTarget("http://target.com","target","http://someobject.com/sometype")
            .setActor("http://xeneo.org/someActor", "Actor Someone", "http://provider.com")
            .build();
        
        activityRepository.addActivity(a);
        
        return a.getActivityURI();        
        
    }
    
    @Test
    public void generateData() {
    
        CaseType ct = manager.createCaseType("Test Type", "Test Description");
        
        List<String> uris = util.createCasesIntoDatabase(ct.getCaseTypeURI(), 5);
        List<String> tasks = util.createTaskTestData(10);
        
        for (int i = 0; i < 5; i++)       
            util.createTaskContexts(this.generateActivities(Calendar.getInstance().getTime()), tasks, uris);
        
       
    }
}
