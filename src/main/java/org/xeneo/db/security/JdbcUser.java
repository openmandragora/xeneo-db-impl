/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db.security;

import org.xeneo.core.security.User;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Stefan Huber
 */
public class JdbcUser extends User implements UserDetails {

    JdbcUser(String userURI, String firstName, String lastName, String email, String password) {
        super(userURI,firstName,lastName,email,password);
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();

        auths.add(new GrantedAuthorityImpl("ROLE_USER"));

        return auths;
    }
}
