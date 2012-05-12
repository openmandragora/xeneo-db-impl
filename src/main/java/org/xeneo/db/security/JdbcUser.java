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
public class JdbcUser implements UserDetails, User {

    private String FirstName, LastName, UserURI, Username, Password;

    public JdbcUser(String Username, String Password, String FirstName, String LastName, String UserURI) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.UserURI = UserURI;
        this.Username = Username;
        this.Password = Password;
    }

    public String getPassword() {
        return this.Password;
    }

    public String getUsername() {
        return this.Username;
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

    public String getUserURI() {
        return this.UserURI;
    }

    public String getFirstName() {
        return this.FirstName;
    }

    public String getLastName() {
        return this.LastName;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();

        auths.add(new GrantedAuthorityImpl("ROLE_SOMEONE"));

        return auths;
    }
}
