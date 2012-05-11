/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.db.JdbcCaseType;
import org.springframework.security.authentication.AuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeneo.core.task.CaseType;

import javax.sql.DataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xeneo.core.task.CaseEngine;

/**
 *
 * @author Stefan Huber
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcCaseTypeTest {
        
    @Autowired
    private AuthenticationManager am;
    
    @Autowired
    private JdbcCaseEngine engine;
    
    static final Logger logger = LoggerFactory.getLogger(JdbcTaskTest.class);
    
    public JdbcCaseTypeTest() {        
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

    @Test
    public void testCreateCaseType() {
        
        String title = "My Case Type"; String description = "My Case Type Description";
        
        CaseType ct = engine.createCaseType(title, description);
        
        logger.info("CaseType created with URI: " + ct.getCaseTypeURI() + ", Title: " + ct.getTitle() + " and Description: " + ct.getDescription());
        
        assertTrue(!ct.getCaseTypeURI().isEmpty() && !ct.getTitle().isEmpty() && !ct.getDescription().isEmpty());
    }
    
    @Test
    public void testUpdateFields() {
        
        String title = "My Second Case Type"; String description = "My Second Case Type Description";
        
        CaseType ct = engine.createCaseType(title, description);
        
        logger.info("CaseType created with URI: " + ct.getCaseTypeURI() + ", Title: " + ct.getTitle() + " and Description: " + ct.getDescription());
        
        assertTrue(!ct.getCaseTypeURI().isEmpty() && !ct.getTitle().isEmpty() && !ct.getDescription().isEmpty());
        
        ct.updateTitle("new CaseType Title");
        ct.updateDescription("new CaseType description");
        
        assertFalse(title.equalsIgnoreCase(ct.getTitle()) || description.equalsIgnoreCase(ct.getDescription()));
        
    }
}
