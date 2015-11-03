package twitter.api.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import twitter.api.App

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = App)
class UserRepositorySpec extends Specification {

    @Shared
    ShaPasswordEncoder encoder = new ShaPasswordEncoder(256)

    @Autowired
    UserRepository userRepository

    def "existent customer should be found"() {
        expect:
        def encoded = encoder.encodePassword(password, salt)
        assert userRepository.findByUserNameAndPassword(user, encoded).orElse(null)

        where:
        user     | password | salt
        'user'   | 'user'   | 7237291199533735258
        'admin'  | 'user'  | 7237291199533735258
    }

}