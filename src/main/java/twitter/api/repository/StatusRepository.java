package twitter.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import twitter.api.domain.Status;

/**
 * Basic CRUD manipulation of Status domain object.
 * All the methods have been exposed as RESTful resource endpoints and restrict to 'ROLE_USER' ACL.
 *
 * @author ccw
 */
@PreAuthorize("hasRole('ROLE_USER')")
@RepositoryRestResource(collectionResourceRel = "status", path = "status")
public interface StatusRepository extends PagingAndSortingRepository<Status, Long> {

}