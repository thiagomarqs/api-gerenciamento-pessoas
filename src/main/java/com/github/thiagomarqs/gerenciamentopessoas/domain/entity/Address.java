package com.github.thiagomarqs.gerenciamentopessoas.domain.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;
    private String cep;
    private String number;
    private String city;
    private String state;

    @ManyToOne
    private Person person;

    private boolean active = true;

    private Boolean isMain = false;

    public Address(boolean isMain) {
        this.isMain = isMain;
    }

    public Address(Long id, String address, String cep, String number, String city, String state, Person person, boolean active, boolean isMain) {
        this.id = id;
        this.address = address;
        this.cep = cep;
        this.number = number;
        this.city = city;
        this.state = state;
        this.person = person;
        this.active = active;
        this.isMain = isMain;
    }

    public Address() {

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setId(Long addressId) {
        this.id = addressId;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean main) {
        isMain = (main == null) ? false : main;
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

        public Builder isMain(boolean isMain) {
            this.address.isMain = isMain;
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
