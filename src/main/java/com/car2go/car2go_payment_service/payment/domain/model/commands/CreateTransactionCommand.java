package com.car2go.car2go_payment_service.payment.domain.model.commands;

public record CreateTransactionCommand(Long vehicleId) {
    public CreateTransactionCommand {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId cannot be null");
        }
    }
}
