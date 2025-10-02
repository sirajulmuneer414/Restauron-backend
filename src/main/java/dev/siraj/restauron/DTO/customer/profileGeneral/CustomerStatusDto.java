package dev.siraj.restauron.DTO.customer.profileGeneral;

import dev.siraj.restauron.entity.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatusDto {
    private AccountStatus status;
}
