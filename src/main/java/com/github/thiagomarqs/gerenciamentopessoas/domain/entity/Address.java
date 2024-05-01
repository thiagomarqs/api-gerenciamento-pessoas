package com.github.thiagomarqs.gerenciamentopessoas.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 10, max = 255)
    private String address;

    @NotBlank
    @Pattern(regexp = "\\d{8}")
    private String cep;

    @NotBlank
    @Size(min = 1, max = 5)
    private String number;

    @NotBlank
    @Size(min = 3, max = 50)
    private String city;

    @NotBlank
    @Size(min = 3, max = 50)
    private String state;

    @ManyToOne
    private Person person;

    @NotNull
    private boolean active = true;

    public Address() {}
    public Address(Long id, String address, String cep, String number, String city, String state) {
        this.id = id;
        this.address = address;
        this.cep = cep;
        this.number = number;
        this.city = city;
        this.state = state;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static class Builder {

        public Address address = new Address();

        public Builder id(Long id) {
            this.address.id = id;
            return this;
        }

        public Builder address(String address) {
            this.address.address = address;
            return this;
        }

        public Builder cep(String cep) {
            this.address.cep = cep;
            return this;
        }

        public Builder number(String number) {
            this.address.number = number;
            return this;
        }

        public Builder city(String city) {
            this.address.setCity(city);
            return this;
        }

        public Builder state(String state) {
            this.address.state = state;
            return this;
        }

        public Builder active(boolean active) {
            this.address.active = active;
            return this;
        }

        public Builder person(Person person) {
            this.address.person = person;
            return this;
        }

        public Address build() {
            return address;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", cep='" + cep + '\'' +
                ", number='" + number + '\'' +
                ", city=" + city +
                ", active=" + active +
                '}';
    }
}
