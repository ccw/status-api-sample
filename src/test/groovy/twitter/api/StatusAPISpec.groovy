package twitter.api

import groovy.json.JsonSlurper
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate
import twitter.api.domain.Status
import spock.lang.FailsWith
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@WebIntegrationTest
@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = App)
@Stepwise
class StatusAPISpec extends Specification {

    @Shared
    String url = 'http://localhost:8080/status'

    @Shared
    RestTemplate restTemplate

    @Shared
    HttpHeaders httpHeaders

    def setupSpec() {
        httpHeaders = new HttpHeaders()
        def encoded = new String(Base64.encoder.encode('user:user'.getBytes()))
        httpHeaders.set('Authorization', "Basic ${encoded}")

        restTemplate = new RestTemplate()
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            void handleError(ClientHttpResponse response) throws IOException {
                // to swallow the exception in order to verify status code
            }
        })
    }

    @FailsWith(Exception)
    def "unauthenticated behavior should be rejected"() {
        expect:
        restTemplate.postForEntity(url, new Status('test'), Status, Collections.emptyMap())
    }

    def "post should create new entry"() {
        expect:
        final HttpEntity request = new HttpEntity<Status>(new Status(content), httpHeaders)
        def respEntity = restTemplate.exchange(url, HttpMethod.POST, request, Status)
        assert respEntity.statusCode == status

        where:
        content         | status
        'I am Jotaro'   | HttpStatus.CREATED
        'I am Mohammed' | HttpStatus.CREATED
        null            | HttpStatus.CONFLICT
    }

    def "put should update existent entity"() {
        when:
        final HttpEntity request = new HttpEntity<Status>(new Status(content), httpHeaders)
        def respEntity = restTemplate.exchange(url + "/${id}", HttpMethod.PUT, request, Status)
        assert respEntity.statusCode == status

        and:
        respEntity = restTemplate.exchange(url + "/${id}", HttpMethod.GET,
                                           new HttpEntity<Status>(httpHeaders),
                                           Status, Collections.emptyMap())

        then:
        assert respEntity.body.content == content

        where:
        content       | id | status
        'I am Joseph' | 1  | HttpStatus.OK
    }

    def "delete should remove existent entity"() {
        expect:
        assert restTemplate.exchange(url + "/${id}", HttpMethod.DELETE,
                                     new HttpEntity<Status>(httpHeaders),
                                     Status, Collections.emptyMap())?.statusCode == status

        assert restTemplate.exchange(url + "/${id}", HttpMethod.GET,
                                     new HttpEntity<Status>(httpHeaders),
                                     Status, Collections.emptyMap())?.statusCode == HttpStatus.NOT_FOUND

        where:
        id | status
        2  | HttpStatus.NO_CONTENT
    }

    def "list should have multiple returned"() {
        setup:
        10.times { idx ->
            def request = new HttpEntity<Status>(new Status("content_${idx}"), httpHeaders)
            def respEntity = restTemplate.exchange(url, HttpMethod.POST, request, Status)
            assert respEntity.statusCode == HttpStatus.CREATED
        }

        expect:
        def response = restTemplate.exchange(url + "/?page=0&size=10", HttpMethod.GET,
                                             new HttpEntity<String>(httpHeaders),
                                             String, Collections.emptyMap())

        assert response.statusCode == HttpStatus.OK
        assert new JsonSlurper().parseText(response.body).page.size == 10
    }

}