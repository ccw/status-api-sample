package twitter.api;

import org.h2.server.web.WebServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import twitter.api.repository.SecuredJpaRepositoryImpl;

/**
 * Major entry point of the whole application.
 *
 * @author ccw
 */
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = SecuredJpaRepositoryImpl.class)
public class App {

    /**
     * To expose the h2 web console for debugging
     */
    @Bean
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
        registration.addUrlMappings("/h2/*");
        return registration;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}