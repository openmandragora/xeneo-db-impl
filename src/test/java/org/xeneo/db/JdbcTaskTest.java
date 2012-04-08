/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.db.JdbcTask;
import org.apache.log4j.Logger;
import org.xeneo.core.task.Task;

import javax.sql.DataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stefan
 */
public class JdbcTaskTest {
    
    static final Logger logger = Logger.getLogger(JdbcTaskTest.class);
    
    public JdbcTaskTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"test-config.xml"});
        dataSource = context.getBean("dataSource", DataSource.class);
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
        Task task = new JdbcTask(dataSource,"My first Task","My Task Description");
        
        String title = task.getTitle();
        String desc = task.getDescription();
        
        logger.info("Old Title: " + title + ", old Description: " + desc);
        
        task.updateTitle("my blub title");
        task.updateDescription("my blub description");
        
        logger.info("New Title: " + task.getTitle() + ", new Description: " + task.getDescription());
        
        assertFalse(title.equalsIgnoreCase(task.getTitle()));       
        assertFalse(desc.equalsIgnoreCase(task.getDescription()));
    }
}
