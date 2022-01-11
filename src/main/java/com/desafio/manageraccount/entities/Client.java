package com.desafio.manageraccount.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String clientCPF;
    private String foneNumber;
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Account> accountList = new ArrayList<>();

    public Client() {}

    public Client(Long id,String name, String clientCPF, String foneNumber, String address) {
        this.id = id;
        this.name = name;
        this.clientCPF = clientCPF;
        this.foneNumber = foneNumber;
        this.address = address;
    }

    public void addAccount(Account account) {
        accountList.add(account);
    }

    public void removeAccount(Account account) {
        accountList.remove(account);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getClientCPF() {
        return clientCPF;
    }

    public String getFoneNumber() {
        return foneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFoneNumber(String foneNumber) {
        this.foneNumber = foneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id) && Objects.equals(clientCPF, client.clientCPF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientCPF);
    }
}
