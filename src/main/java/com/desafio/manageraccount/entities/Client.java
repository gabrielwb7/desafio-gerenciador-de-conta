package com.desafio.manageraccount.entities;

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
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String clientCPF;

    @Column(nullable = false)
    private String foneNumber;

    @Column(nullable = false)
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Account> accountList = new ArrayList<>();

    public Client(Long id,String name, String clientCPF, String foneNumber, String address) {
        this.id = id;
        this.name = name;
        this.clientCPF = clientCPF;
        this.foneNumber = foneNumber;
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(clientCPF, client.clientCPF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientCPF);
    }
}
