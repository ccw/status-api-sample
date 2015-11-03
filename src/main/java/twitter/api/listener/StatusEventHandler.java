package twitter.api.listener;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import twitter.api.domain.Status;
import twitter.api.security.AuthUser;
import twitter.api.security.SecurityHelper;

/**
 * Event handler to automatically update owner info to the Status domain object
 *
 * @author ccw
 */
@Component
@RepositoryEventHandler(Status.class)
public class StatusEventHandler {

    @HandleBeforeSave
    @HandleBeforeCreate
    public void handleStatusSave(final Status status) {
        if (status.getOwnerId() == null) {
            final AuthUser user = SecurityHelper.getAuthUser();
            status.setUserId(user.getUserId());
        }
    }

}
