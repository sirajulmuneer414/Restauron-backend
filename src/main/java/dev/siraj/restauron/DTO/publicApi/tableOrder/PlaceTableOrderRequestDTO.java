package dev.siraj.restauron.DTO.publicApi.tableOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Request DTO for placing an order from a table
 */
public record PlaceTableOrderRequestDTO(
        @NotBlank(message = "Customer name is required") String customerName,

        @NotBlank(message = "Customer phone number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits") String customerPhone,

        String customerRemarks,

        @NotEmpty(message = "Order must contain at least one item") List<OrderItemDTO> items) {
    public record OrderItemDTO(
            @NotBlank(message = "Menu item ID is required") String encryptedMenuItemId,

            @Positive(message = "Quantity must be positive") Integer quantity) {
    }
}
