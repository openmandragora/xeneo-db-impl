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

import java.util.Calendar;
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
import org.xeneo.core.activity.Filter;
import static org.xeneo.core.activity.Filter.*;
import org.xeneo.db.testutils.ActivityUtil;

/**
 *
 * @author Stefan Huber
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcActivityRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(JdbcActivityRepositoryTest.class);
    @Autowired
    private JdbcActivityRepository ar;

    public JdbcActivityRepositoryTest() {
    }

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

    public void LogActivity(Activity a) {
        logger.info("ActivityURI: " + a.getActivityURI() + " action: " + a.getActionURI() + " actor: " + a.getActionURI());
    }

    public void checkActivity(Activity a) {
        
        assertNotNull(a.getActivityURI());
        assertNotNull(a.getActionURI());
        assertNotNull(a.getCreationDate().before(Calendar.getInstance().getTime()));

        assertNotNull(a.getActor());
        assertNotNull(a.getObject());
        assertNotNull(a.getActivityProvider());

        assertNotNull(a.getObject().getObjectName());
        assertNotNull(a.getObject().getObjectURI());
        assertNotNull(a.getObject().getObjectTypeURI());
        
        assertNotNull(a.getActor().getActorURI());
        assertNotNull(a.getActor().getActorName());
        assertNotNull(a.getActor().getActivityProviderURI());
        
        assertNotNull(a.getActivityProvider().getActivityProviderURI());
        assertNotNull(a.getActivityProvider().getActivityProviderName());
        assertNotNull(a.getActivityProvider().getActivityProviderType());

        if (a.getTarget() != null) {
            assertNotNull(a.getTarget().getObjectName());
            assertNotNull(a.getTarget().getObjectURI());
            assertNotNull(a.getTarget().getObjectTypeURI());
        }
    }

    @Test
    public void testAddingActivities() {

        List<Activity> list = ActivityUtil.createRandomActivities("http://someuri.org/", 50, 3);

        for (Activity a : list) {
            
            checkActivity(a);
            logger.info("Try to add Activity: " + a.toString());

            ar.addActivity(a);

        }

        List<Activity> activities = ar.getActivities(new Filter(EQ(Term.Actor, "http://xeneo.org/someActor")));

        for (Activity a : activities) {            
            checkActivity(a);
        }

        assertTrue(list.size() == activities.size());
    }
    
    @Test
    public void checkExistenceOfActivityAndParts() {
        
        List<Activity> list = ActivityUtil.createRandomActivities("http://someuri2.org/", 1, 2);
        
        for (Activity a : list) {            
            
            checkActivity(a);
            ar.addActivity(a);
            
            assertTrue(ar.isExistingActivity(a.getActivityURI()));
            assertFalse(ar.isExistingActivity("http://somenonexistenuri.org"));
            
            assertTrue(ar.isExistingActor(a.getActor().getActorURI()));
            assertFalse(ar.isExistingActor("http://somenonexistenuri.org"));
            
            assertTrue(ar.isExistingActivityProvider(a.getActivityProvider().getActivityProviderURI()));
            assertFalse(ar.isExistingActivityProvider("http://somenonexistenuri.org"));
            
            assertTrue(ar.isExistingObject(a.getObject().getObjectURI()));
            assertFalse(ar.isExistingObject("http://somenonexistenuri.org"));
            
        }
        
    }
}
