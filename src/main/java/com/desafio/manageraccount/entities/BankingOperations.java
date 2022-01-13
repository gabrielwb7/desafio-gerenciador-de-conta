package com.desafio.manageraccount.entities;

import com.desafio.manageraccount.entities.enums.TypeOperations;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class BankingOperations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOperation;

    @Column(nullable = false)
    private Date dateOperation = new Date();

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOperations typeOperations;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private Long idDestinyAccount;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


    public BankingOperations(Long idOperation, Date dateOperation, Double amount, TypeOperations typeOperations, Double rate, Long idDestinyAccount, Account account) {
        this.idOperation = idOperation;
        this.dateOperation = dateOperation;
        this.amount = amount;
        this.typeOperations = typeOperations;
        this.rate = rate;
        this.idDestinyAccount = idDestinyAccount;
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankingOperations that = (BankingOperations) o;
        return Objects.equals(idOperation, that.idOperation) && Objects.equals(dateOperation, that.dateOperation) && Objects.equals(amount, that.amount) && typeOperations == that.typeOperations && Objects.equals(rate, that.rate) && Objects.equals(idDestinyAccount, that.idDestinyAccount) && Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOperation, dateOperation, amount, typeOperations, rate, idDestinyAccount, account);
    }
}
