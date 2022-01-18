package com.desafio.manageraccount.entities;

import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
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
    @Column(name = "tipoOperação", nullable = false)
    private TypeOperations typeOperations;

    @Column
    private Double rate = 0.0;

    @Column(nullable = false)
    private Long idDestinyAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeStatus typeStatus = TypeStatus.NOSTARTER;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


    public BankingOperations(Long idOperation, Double amount, TypeOperations typeOperations, Long idDestinyAccount) {
        this.idOperation = idOperation;
        this.amount = amount;
        this.typeOperations = typeOperations;
        this.idDestinyAccount = idDestinyAccount;
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
