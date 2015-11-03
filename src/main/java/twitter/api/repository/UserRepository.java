package twitter.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import twitter.api.domain.User;

import java.util.Optional;

/**
 * Basic CURD of User domain object. For internal usage only currently.
 *
 * @author ccw
 */
@RepositoryRestResource(exported = false)
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUserName(@Param("userName") final String userName);

    Optional<User> findByUserNameAndPassword(@Param("userName") final String userName, @Param("password") final String password);

}
