package twitter.api.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

/**
 * User entity, only used for service authentication
 *
 * @author ccw
 */
@Entity
@Table(
    name = "user",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_name"})
)
@Data
@RequiredArgsConstructor
public class User implements Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_name")
    private final String userName;

    @Column(name = "password")
    private final String password;

    @Column(name = "salt")
    private final Long salt;

    @Column(name = "roles")
    private final String roles;

    User() {
        this.userName = null;
        this.password = null;
        this.salt = null;
        this.roles = null;
    }

    @Override
    public Long getOwnerId() {
        return id;
    }
}
