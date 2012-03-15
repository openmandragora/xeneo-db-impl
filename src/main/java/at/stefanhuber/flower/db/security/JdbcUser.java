/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stefanhuber.flower.db.security;

import at.stefanhuber.flower.core.security.User;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        auths.add(new SimpleGrantedAuthority("ROLE_SOMEONE"));
        
        return auths;
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
    
}
