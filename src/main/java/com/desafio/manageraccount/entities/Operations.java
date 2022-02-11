package com.desafio.manageraccount.entities;

import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@javax.persistence.Entity(name = "operations")
public class Operations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOperation;

    @Column
    @Enumerated(EnumType.STRING)
    private TypeOperations typeOperations;

    @Column(nullable = false)
    private String dateOperation = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column
    private TypeStatus typeStatus = TypeStatus.CANCELED;

    @Column
    private Double tax = 0.0;

    @Column
    @Size(min = 5, max = 5)
    private String accountDestiny;

    @Column
    @Size(min = 4, max = 4)
    private String agencyDestiny;

    @Column
    @Size(min = 1, max = 1)
    private String destinyVerifyDigit;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operations that = (Operations) o;
        return Objects.equals(dateOperation, that.dateOperation) && Objects.equals(amount, that.amount) && typeOperations == that.typeOperations && Objects.equals(accountDestiny, that.accountDestiny) && Objects.equals(agencyDestiny, that.agencyDestiny) && Objects.equals(destinyVerifyDigit, that.destinyVerifyDigit) && typeStatus == that.typeStatus && Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateOperation, amount, typeOperations, accountDestiny, agencyDestiny, destinyVerifyDigit, typeStatus, account);
    }
}
