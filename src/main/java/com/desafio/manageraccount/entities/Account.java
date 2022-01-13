package com.desafio.manageraccount.entities;

import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAccount typeAccount;

    @Column(nullable = false)
    private String agency;

    @Column(nullable = false)
    private String numberAccount;

    @Column(nullable = false)
    private String verifyingDigit;

    @Column(nullable = false)
    private Double balanceAccount;

    @Column(nullable = false)
    private Double valueWithdrawLimit;

    @Column(nullable = false)
    private Integer limitWithdrawals;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonIgnore
    @OneToMany(mappedBy = "operations")
    private List<BankingOperations> operationsList = new ArrayList<>();

    public Account(Long id, String agency, String numberAccount, TypeAccount typeAccount, String verifyingDigit, Double balanceAccount, Double valueWithdrawLimit, Integer limitWithdrawals) {
        this.id = id;
        this.agency = agency;
        this.numberAccount = numberAccount;
        this.typeAccount = typeAccount;
        this.verifyingDigit = verifyingDigit;
        this.balanceAccount = balanceAccount;
        this.valueWithdrawLimit = valueWithdrawLimit;
        this.limitWithdrawals = limitWithdrawals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return typeAccount == account.typeAccount && Objects.equals(agency, account.agency) && Objects.equals(numberAccount, account.numberAccount) && Objects.equals(verifyingDigit, account.verifyingDigit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeAccount, agency, numberAccount, verifyingDigit);
    }
}
