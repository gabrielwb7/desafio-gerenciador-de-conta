package com.desafio.manageraccount.entities;

import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@javax.persistence.Entity(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 4, max = 4)
    private String agency;

    @Column(nullable = false)
    private String numberAccount;

    @Column(nullable = false)
    private String verifyDigit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAccount typeAccount;

    @Column
    private Integer quantityWithdraw;

    @Column
    private Double balanceAccount = 0.0;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Operations> operationsList = new ArrayList<>();

    public Account(String agency,TypeAccount typeAccount) {
        this.agency = agency;
        this.typeAccount = typeAccount;
    }

    public Account(Long id, String agency, String numberAccount, String verifyDigit, TypeAccount typeAccount) {
        this.id = id;
        this.agency = agency;
        this.numberAccount = numberAccount;
        this.verifyDigit = verifyDigit;
        this.typeAccount = typeAccount;
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

}
