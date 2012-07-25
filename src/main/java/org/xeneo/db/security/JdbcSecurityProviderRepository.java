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

import org.springframework.jdbc.core.JdbcTemplate;
import org.xeneo.core.security.SecurityProvider;
import org.xeneo.core.security.SecurityProviderRepository;

/**
 *
 * @author Stefan Huber
 */
public class JdbcSecurityProviderRepository implements SecurityProviderRepository {
    
    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate(JdbcTemplate jt) {
        this.jdbcTemplate = jt;
    }
    
    private final static String ADD_SECURITY_PROVIDER = "INSERT INTO SecurityProvider (Identifier,) values ()";
    
    public void addSecurityProvider(SecurityProvider sp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
}
