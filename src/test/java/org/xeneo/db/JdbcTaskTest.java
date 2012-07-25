/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.db.JdbcTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeneo.core.task.Task;

import javax.sql.DataSource;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Stefan Huber
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcTaskTest {
    
    static final Logger logger = LoggerFactory.getLogger(JdbcTaskTest.class);
    
    @Autowired
    JdbcCaseManager engine;
    
    public JdbcTaskTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    private DataSource dataSource;
    
    @Before
    public void setUp() {

    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testCreateTask() {   
        String title = "My first Task", desc = "My Task Description"; 
        
        Task task = engine.createTask(title, desc);
        
        logger.info("Old Title: " + title + ", old Description: " + desc);
        
        assertEquals(title,task.getTitle());
        assertEquals(desc,task.getDescription());
        
        
        task.update("my blub title","my blub description");        
        
        logger.info("New Title: " + task.getTitle() + ", new Description: " + task.getDescription());
        
        assertFalse(title.equalsIgnoreCase(task.getTitle()));       
        assertFalse(desc.equalsIgnoreCase(task.getDescription()));
    }
}
