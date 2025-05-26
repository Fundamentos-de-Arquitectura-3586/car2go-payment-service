package com.car2go.car2go_payment_service.payment.interfaces.rest.resources;

import com.car2go.car2go_payment_service.payment.domain.model.valueobjects.PaymentStatus;
import com.car2go.car2go_payment_service.profiles.domain.model.aggregates.Profile;
import com.car2go.car2go_payment_service.vehicle.domain.model.aggregates.Vehicle;

import java.util.Date;

public record TransactionResource(Long transactionId, Profile buyer, Profile seller, Vehicle vehicle, Double amount, Date createdAt, Date updatedAt, PaymentStatus paymentStatus) {}
