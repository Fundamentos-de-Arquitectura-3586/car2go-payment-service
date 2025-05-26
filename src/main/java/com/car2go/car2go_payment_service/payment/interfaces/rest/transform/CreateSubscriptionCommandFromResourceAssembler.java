package com.car2go.car2go_payment_service.payment.interfaces.rest.transform;

import com.car2go.car2go_payment_service.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.car2go.car2go_payment_service.payment.domain.model.commands.CreateSubscriptionCommand;
import com.car2go.car2go_payment_service.payment.interfaces.rest.resources.CreateSubscriptionResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CreateSubscriptionCommandFromResourceAssembler {
    public static CreateSubscriptionCommand toCommandFromResource(CreateSubscriptionResource resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long profileId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        return new CreateSubscriptionCommand(
                resource.price(),
                resource.description(),
                profileId
        );
    }
}
