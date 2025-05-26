package com.car2go.car2go_payment_service.payment.application.internal.services.commandservices;

import com.car2go.car2go_payment_service.iam.domain.model.aggregates.User;
import com.car2go.car2go_payment_service.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.car2go.car2go_payment_service.payment.domain.model.aggregates.Transaction;
import com.car2go.car2go_payment_service.payment.domain.model.commands.CreateTransactionCommand;
import com.car2go.car2go_payment_service.payment.domain.model.commands.UpdateTransactionCommand;
import com.car2go.car2go_payment_service.payment.domain.services.TransactionCommandService;
import com.car2go.car2go_payment_service.payment.infrastructure.persistence.jpa.TransactionRepository;
import com.car2go.car2go_payment_service.profiles.domain.model.aggregates.Profile;
import com.car2go.car2go_payment_service.profiles.domain.model.queries.GetProfileByIdQuery;
import com.car2go.car2go_payment_service.profiles.domain.services.ProfileQueryService;
import com.car2go.car2go_payment_service.vehicle.domain.model.aggregates.Vehicle;
import com.car2go.car2go_payment_service.vehicle.domain.model.queries.GetVehicleByIdQuery;
import com.car2go.car2go_payment_service.vehicle.domain.services.VehicleQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class TransactionCommandServiceImpl implements TransactionCommandService {

    private final TransactionRepository transactionRepository;
    private final ProfileQueryService profileQueryService;
    private final VehicleQueryService vehicleQueryService;

    public TransactionCommandServiceImpl(TransactionRepository transactionRepository,
                                         ProfileQueryService profileQueryService,
                                         VehicleQueryService vehicleQueryService) {
        this.transactionRepository = transactionRepository;
        this.profileQueryService = profileQueryService;
        this.vehicleQueryService = vehicleQueryService;
    }

    @Override
    public Optional<Transaction> handle(CreateTransactionCommand command) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        boolean hasRequiredRole = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_BUYER"));

        if (hasRequiredRole){
            Long profileId = userDetails.getId();
            Profile profileBuyer = profileQueryService.handle(new GetProfileByIdQuery(profileId))
                    .orElseThrow(() -> new IllegalArgumentException("Buyer Profile not found"));
            Vehicle vehicle = vehicleQueryService.handle(new GetVehicleByIdQuery(Math.toIntExact(command.vehicleId())))
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
            Profile profileSeller = profileQueryService.handle(new GetProfileByIdQuery(vehicle.getProfileId()))
                    .orElseThrow(() -> new IllegalArgumentException("Seller Profile not found"));
            var transaction = new Transaction(profileBuyer, profileSeller, vehicle, vehicle.getPrice());
            transactionRepository.save(transaction);
            return Optional.of(transaction);
        } else{
            throw new SecurityException("Only buyers can create a transaction");
        }

    }

    @Override
    public Optional<Transaction> handle(UpdateTransactionCommand command) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(command.transactionId());
        if (transactionOpt.isEmpty()) {
            throw new IllegalArgumentException("Transaction does not exist for the given id");
        }

        Transaction transaction = transactionOpt.get();
        transaction.setStatus(command.status());
        transactionRepository.save(transaction);
        return Optional.of(transaction);
    }
}
