Status API Example
===================

Technical Spec
----------------
This service is mainly implemented with [Spring Data REST](http://projects.spring.io/spring-data-rest/) and 
[Spring Security](http://projects.spring.io/spring-security/). 
And an embedded [H2 Database](http://www.h2database.com/html/main.html) is adopted as the major data store of the application. In addition, [Flyway](http://flywaydb.org/) is as well used for the DB initiation and migration.

For testing, [Spock Framework](https://github.com/spockframework/spock) is used.



Build and Run
----------------
[Gradle](http://http://www.gradle.org) and Java 8 are required to build this project.

Please run `gradle clean build` under the project root (or with the assist of IDEs).
After the project being built, simply run `java -jar build/libs/status-api-0.1.0.jar` will launch the application.
Alternatively, run `gradle clean bootRun` can do the same thing.

After launching, the service will be available on port `8080`. And `/status` will be the endpoint to access the `status` resource.
Basic HTTP authentication is required for the mentioned endpoint (sample user credentials - `user:user` and `tester:user`; sample admin user credentials - `admin:user`.)  


Test
----------------

Please be noted that the `content-type` header of the client requests must be `application/json`; and the request body for `POST` and `PUT` methods must be formated as JSON document accordingly. Such like -

    {"content": "Hello World"}
    

