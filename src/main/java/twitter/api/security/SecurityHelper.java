package twitter.api.security;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper of retrieving authenticated user info
 *
 * @author ccw
 */
public class SecurityHelper {

    public static final AuthUser getAuthUser() {
        return (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static final boolean isSuperUser(final AuthUser user) {
        return user.getAuthorities().stream().filter(ga -> ga.getAuthority().equals("ROLE_ADMIN")).toArray().length > 0;
    }

}
