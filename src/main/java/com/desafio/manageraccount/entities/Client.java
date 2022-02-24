package com.desafio.manageraccount.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Table(name = "client")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100)
    private String name;

    @Column(unique = true)
    private String clientCPF;

    @Column(unique = true)
    private String clientCNPJ;

    @Column(nullable = false)
    @Size(min = 10, max = 11)
    private String phoneNumber;

    @Column(nullable = false)
    @Size(min = 5, max = 250)
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Account> accountList = new ArrayList<>();

    public Client(String name, String clientCPF, String clientCNPJ , String phoneNumber, String address) {
        this.name = name;
        this.clientCPF = clientCPF;
        this.clientCNPJ = clientCNPJ;
        this.phoneNumber = phoneNumber;
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
