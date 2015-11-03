package twitter.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Extends the User class from spring security to contain an additional salt attribute.
 *
 * @author ccw
 */
public class AuthUser extends User {

    private Long userId;
    private Long salt;

    public AuthUser(final Long userId, final String username, final String password, final Long salt, final Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.salt = salt;
    }

    public Long getSalt() {
        return this.salt;
    }

    public Long getUserId() { return this.userId; }
}
