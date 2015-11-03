package twitter.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import twitter.api.domain.User;
import twitter.api.repository.UserRepository;

import java.util.stream.Stream;

/**
 * Responsible for the retrieval of user detail information for authentication.
 *
 * @author ccw
 */
@Component
public class DefaultUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new AuthUser(user.getId(), user.getUserName(), user.getPassword(), user.getSalt(),
                            AuthorityUtils.createAuthorityList(this.resolveRoles(user.getRoles())));
    }

    protected String[] resolveRoles(final String roles) {
        return Stream.of(roles.split(",")).map(s -> "ROLE_" + s).toArray(String[]::new);
    }

}
