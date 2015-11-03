package twitter.api.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ContextConfiguration
import twitter.api.App
import twitter.api.domain.Status
import twitter.api.security.AuthUser
import spock.lang.FailsWith
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = App)
@Stepwise
class StatusRepositorySpec extends Specification {

    @Autowired
    StatusRepository repository

    @Shared
    List handledIDs = []

    def auth(final AuthUser user, final String password) {
        final UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user, password, user.authorities)
        SecurityContextHolder.getContext().setAuthentication(token)
        true
    }

    @FailsWith(AuthenticationCredentialsNotFoundException)
    def "unauthenticated behavior should fail"() {
        setup:
        SecurityContextHolder.clearContext();

        expect:
        repository.save(new Status(content))

        where:
        content = 'I am Jotaro'
    }

    def "create customer should work"() {
        setup:
        def user = new AuthUser(2, 'user', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER'))

        when:
        auth(user, 'user')
        final Status customer = new Status(content)
        customer.userId = user.userId
        final Status saved = repository.save(customer)

        then:
        repository.findOne(customer.getId()) == saved

        cleanup:
        handledIDs << saved.getId()

        where:
        content << ['I am Jotaro', 'I am Joseph']
    }

    def "non authorized user can't view"() {
        setup:
        def user = new AuthUser(3, 'tester', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER'))

        when:
        auth(user, 'user')

        then:
        repository.findOne(dataId) == null

        where:
        dataId << handledIDs
    }

    def "super user can view"() {
        setup:
        def user = new AuthUser(1, 'admin', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER', 'ROLE_ADMIN'))

        when:
        auth(user, 'user')

        then:
        repository.findOne(dataId) != null

        where:
        dataId << handledIDs
    }

    def "update customer should work"() {
        setup:
        def user = new AuthUser(2, 'user', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER'))

        when:
        auth(user, 'user')
        final Status status = new Status("test")
        status.id = dataId
        status.userId = user.userId

        then:
        repository.save(status)?.content == "test"

        where:
        dataId << handledIDs
    }

    def "non authorized user can't update"() {
        setup:
        def user = new AuthUser(3, 'tester', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER'))

        when:
        auth(user, 'user')
        final Status status = new Status("test")
        status.id = dataId
        status.userId = 2

        then:
        repository.save(status)?.content == null

        where:
        dataId << handledIDs
    }

    def "super user can update"() {
        setup:
        def user = new AuthUser(1, 'admin', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER', 'ROLE_ADMIN'))

        when:
        auth(user, 'user')
        final Status status = new Status("tested")
        status.id = dataId
        status.userId = 2

        then:
        repository.save(status)?.content == "tested"

        where:
        dataId << handledIDs
    }

    def "delete customer should work"() {
        setup:
        def user = new AuthUser(2, 'user', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER'))

        when:
        auth(user, 'user')
        repository.delete(dataId)

        then:
        repository.findOne(dataId) == null

        where:
        dataId = handledIDs.pop()
    }

    def "non authorized user can't delete"() {
        when:
        auth(new AuthUser(3, 'tester', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER')), 'user')
        repository.delete(dataId)

        then:
        auth(new AuthUser(2, 'tester', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER')), 'user')
        repository.findOne(dataId) != null

        where:
        dataId << handledIDs
    }

    def "super user can delete"() {
        setup:
        def user = new AuthUser(1, 'admin', 'user', 1, AuthorityUtils.createAuthorityList('ROLE_USER', 'ROLE_ADMIN'))

        when:
        auth(user, 'user')
        repository.delete(dataId)

        then:
        repository.findOne(dataId) == null

        where:
        dataId << handledIDs
    }
}