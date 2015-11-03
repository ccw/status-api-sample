package twitter.api.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.context.ContextConfiguration
import spock.lang.FailsWith
import spock.lang.Specification
import twitter.api.App
import twitter.api.domain.User
import twitter.api.repository.UserRepository

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = App)
class DefaultUserDetailServiceSpec extends Specification {

    @Autowired
    DefaultUserDetailService defaultUserDetailService

    def "user detail should match with user store"() {
        when:
        defaultUserDetailService.userRepository = Stub(UserRepository)
        defaultUserDetailService.userRepository.findByUserName(_) >> { String uName ->
            Optional.of(uName == userName ? new User(userName, password, salt, roles) : null)
        }

        then:
        assert userDetail == defaultUserDetailService.loadUserByUsername(userName)

        where:
        userName | password    | salt | roles        | userDetail
        'user1'  | 'password1' | 1    | 'ADMIN,USER' | new AuthUser(1, 'user1', 'password1', 1, AuthorityUtils.createAuthorityList('ROLE_ADMIN', 'ROLE_USER'))
        'user2'  | 'password2' | 2    | 'ADMIN'      | new AuthUser(2, 'user2', 'password2', 2, AuthorityUtils.createAuthorityList('ROLE_ADMIN'))
    }

    @FailsWith(UsernameNotFoundException)
    def "nonexistent user should cause exception"() {
        when:
        defaultUserDetailService.userRepository = Stub(UserRepository)
        defaultUserDetailService.userRepository.findByUserName(_) >> Optional.empty()

        then:
        defaultUserDetailService.loadUserByUsername(userName)

        where:
        userName << ['user1', 'user2']
    }

}