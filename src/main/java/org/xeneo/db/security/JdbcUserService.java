
package org.xeneo.db.security;

import org.xeneo.core.security.UserService;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Stefan Huber
 */
public class JdbcUserService implements UserService {

    protected JdbcUser getCurrentJdbcUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JdbcUser user = null;
        if (principal instanceof JdbcUser) {
            user = (JdbcUser) principal;
        }
        return user;
    }

    public JdbcUser getCurrentUser() {
        return getCurrentJdbcUser();
    }

    public String getCurrentUserURI() {
        return getCurrentJdbcUser().getUserURI();
    }
    
    
}
