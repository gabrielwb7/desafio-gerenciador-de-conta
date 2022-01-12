package com.desafio.manageraccount.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String agency;

    @Column(nullable = false)
    private String numberAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAccount typeAccount;

    @Column
    private String verifyingDigit;

    @Column
    private Double balanceAccount;

    @Column
    private Double valueWithdrawLimit;

    @Column
    private Integer limitWithdrawals;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Account(Long id, String agency, String numberAccount, TypeAccount typeAccount, String verifyingDigit, Double balanceAccount, Double valueWithdrawLimit, Integer limitWithdrawals, Client client) {
        this.id = id;
        this.agency = agency;
        this.numberAccount = numberAccount;
        this.typeAccount = typeAccount;
        this.verifyingDigit = verifyingDigit;
        this.balanceAccount = balanceAccount;
        this.valueWithdrawLimit = valueWithdrawLimit;
        this.limitWithdrawals = limitWithdrawals;
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(agency, account.agency) && Objects.equals(numberAccount, account.numberAccount) && Objects.equals(verifyingDigit, account.verifyingDigit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, agency, numberAccount, verifyingDigit);
    }
}
