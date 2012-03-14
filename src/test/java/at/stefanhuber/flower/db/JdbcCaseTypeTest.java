/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stefanhuber.flower.db;

import org.apache.log4j.Logger;
import at.stefanhuber.flower.core.CaseType;
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
public class JdbcCaseTypeTest {
    
    private DataSource dataSource;
    static final Logger logger = Logger.getLogger(JdbcTaskTest.class);
    
    public JdbcCaseTypeTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"test-config.xml"});
        dataSource = context.getBean("dataSource", DataSource.class);
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
        
        CaseType ct = new JdbcCaseType(dataSource,title,description);
        
        logger.info("CaseType created with URI: " + ct.getCaseTypeURI() + ", Title: " + ct.getTitle() + " and Description: " + ct.getDescription());
        
        assertTrue(!ct.getCaseTypeURI().isEmpty() && !ct.getTitle().isEmpty() && !ct.getDescription().isEmpty());
    }
    
    @Test
    public void testUpdateFields() {
        
        String title = "My Second Case Type"; String description = "My Second Case Type Description";
        
        CaseType ct = new JdbcCaseType(dataSource,title,description);
        
        logger.info("CaseType created with URI: " + ct.getCaseTypeURI() + ", Title: " + ct.getTitle() + " and Description: " + ct.getDescription());
        
        assertTrue(!ct.getCaseTypeURI().isEmpty() && !ct.getTitle().isEmpty() && !ct.getDescription().isEmpty());
        
        ct.updateTitle("new CaseType Title");
        ct.updateDescription("new CaseType description");
        
        assertFalse(title.equalsIgnoreCase(ct.getTitle()) || description.equalsIgnoreCase(ct.getDescription()));
        
    }
}
