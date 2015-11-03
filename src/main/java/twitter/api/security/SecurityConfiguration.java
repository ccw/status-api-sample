package twitter.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;

/**
 * Security related configurations.
 *
 * @author ccw
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DefaultUserDetailService defaultUserDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new ShaPasswordEncoder(256));
        final ReflectionSaltSource saltSource = new ReflectionSaltSource();
        saltSource.setUserPropertyToUse("salt");
        provider.setSaltSource(saltSource);
        provider.setUserDetailsService(defaultUserDetailService);
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and().authorizeRequests()
                        .antMatchers("/user", "/status").hasRole("USER").and()
                        .csrf().disable();
        http.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
    }

}
