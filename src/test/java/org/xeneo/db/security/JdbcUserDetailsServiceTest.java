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

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xeneo.core.security.User;

/**
 *
 * @author Stefan Huber
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/security-test-config.xml")
public class JdbcUserDetailsServiceTest {
    
    @Autowired
    JdbcUserDetailsService uds;

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
    public void testLoadUserByUsername() {
        
        UserDetails u = uds.loadUserByUsername("http://stefanhuber.at/users#stefan");
    
        assertEquals(u.getUsername(),"stefan");
        assertEquals(u.getPassword(),"blub");
        
    }
}
