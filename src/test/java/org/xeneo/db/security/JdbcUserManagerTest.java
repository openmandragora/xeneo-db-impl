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
package org.xeneo.db.security;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xeneo.core.security.User;
import org.xeneo.core.task.Case;
import org.xeneo.core.task.CaseType;
import org.xeneo.db.JdbcCaseManager;

/**
 *
 * @author Stefan
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcUserManagerTest {
    
    @Autowired
    private JdbcUserManager um;
    
    @Autowired
    private JdbcCaseManager ce;
    
    public JdbcUserManagerTest() {
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
    
    public void checkIfUsersEqual(User a, User b) {
        
        // check if both point on a different objects in the heap
        assertFalse(a==b);
        
        assertEquals(a.getUserURI(), b.getUserURI());
        assertEquals(a.getFirstName(), b.getFirstName());
        assertEquals(a.getLastName(), b.getLastName());
        assertEquals(a.getEmail(), b.getEmail());
        
    }
    
    public List<User> createRandomExampleUsers(int n) {
        List<User> users = new ArrayList<User>();
        
        String userURI = "http://test.xeneo.org/user/" + Calendar.getInstance().getTimeInMillis() + "#";
        
        for (int i = 0; i < n; i++) {            
            User in = new User(userURI + "testuser" + i,"Firstname","Lastname","test@xeneo.org","password");
            um.addUser(in);
            users.add(in);
        }
        
        return users;
    }
    
    @Test
    public void testAddingAUserAndRetrievingItAgain() {
    
        String userURI = "http://xeneo.org/users#testuser";
        
        User in = new User(userURI,"Firstname","Lastname","test@xeneo.org","password");
    
        um.addUser(in);
        
        User out = um.loadUserByUsername(userURI);
        
        checkIfUsersEqual(in, out);
    }
    
    @Test
    public void testAddingAUserToACaseAndRetrievingItAgainFromTheCase() {     
        int n = 1;
        
        CaseType ct = ce.createCaseType("My Case Type", "My Test Case Type Description");
        Case c = ce.createCase(ct.getCaseTypeURI(), "My Test Case Title", "My Test Case Description");
            
        List<User> users = this.createRandomExampleUsers(n);
        
        for (User u : users) {
            um.addUserToCase(u.getUserURI(), c.getCaseURI());
        }
        
        List<User> caseUsers = um.listUsersByCaseURI(c.getCaseURI());
        
        assertEquals(users.size(),caseUsers.size());
        assertEquals(caseUsers.size(),n);
                
        checkIfUsersEqual(users.get(n-1),caseUsers.get(n-1));
                
    }
    
    @Test
    public void testAddingUsersToACaseAndRetrievingThemAgainFromTheCase() {     
        int n = 10;
        
        CaseType ct = ce.createCaseType("My Case Type", "My Test Case Type Description");
        Case c = ce.createCase(ct.getCaseTypeURI(), "My Test Case Title", "My Test Case Description");
            
        List<User> users = this.createRandomExampleUsers(n);
        List<String> userURIs = new ArrayList<String>();
        for (User u : users) {
            userURIs.add(u.getUserURI());
        }
        
        assertEquals(users.size(), userURIs.size());
        
        um.addUsersToCase(userURIs, c.getCaseURI());
        
        List<User> caseUsers = um.listUsersByCaseURI(c.getCaseURI());
        
        assertEquals(users.size(),caseUsers.size());
        
        for (int i = 0; i < n; i++) {
            checkIfUsersEqual(users.get(i),caseUsers.get(i));
        }        
    }
    
    @Test
    public void createUserAndUpdateItsAttributes() {
        
        String userURI = "http://test.xeneo.org/users#somebody";        
        User user = new User(userURI,"Firstname","Lastname","test@xeneo.org","password");
        
        um.addUser(user);
        
        assertTrue(um.isExistingUser(userURI));
        
        user.setFirstName("New FirstName");
        user.setLastName("New LastName");
        user.setEmail("Email");
        user.setPassword("New Password");
        
        um.updateUser(user);
        
        User b = um.loadUserByUsername(userURI);
        
        this.checkIfUsersEqual(user, b);
    }
}
