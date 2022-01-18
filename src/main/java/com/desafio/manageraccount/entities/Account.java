package com.desafio.manageraccount.entities;

import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 4, max = 4)
    private String agency;

    @Column(nullable = false)
    @Size(min = 5, max = 5)
    private String numberAccount;

    @Column(nullable = false)
    @Size(min = 1, max = 1)
    private String verifyDigit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAccount typeAccount;

    @Column
    private Double balanceAccount = 0.0;

//    @Column(nullable = false)
//    private Integer limitWithdrawals;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<BankingOperations> operationsList = new ArrayList<>();

    public Account(String agency, String numberAccount, TypeAccount typeAccount, String verifyDigit) {
        this.agency = agency;
        this.numberAccount = numberAccount;
        this.typeAccount = typeAccount;
        this.verifyDigit = verifyDigit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return typeAccount == account.typeAccount && Objects.equals(agency, account.agency) && Objects.equals(numberAccount, account.numberAccount) && Objects.equals(verifyDigit, account.verifyDigit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeAccount, agency, numberAccount, verifyDigit);
    }

    @Override
    public String toString() {
        return "O saldo da conta " + numberAccount
                + "-" + verifyDigit
                + " da agencia " + agency
                + " eh: R$ " + String.format("%.2f", balanceAccount);
    }
}
