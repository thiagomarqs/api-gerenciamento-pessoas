package com.github.thiagomarqs.gerenciamentopessoas.domain.entity;

import jakarta.persistence.*;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private LocalDate birthDate;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Address mainAddress;

    private boolean active = true;

    public Person() {}

    public Person(String fullName, LocalDate birthDate, List<Address> addresses, Address mainAddress) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.addresses = addresses;
        this.mainAddress = mainAddress;
    }

    @ConstructorProperties({"id", "fullName", "birthDate", "addresses", "mainAddress", "active"})
    public Person(Long id, String fullName, LocalDate birthDate, List<Address> addresses, Address mainAddress, boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.addresses = addresses;
        this.mainAddress = mainAddress;
        this.active = active;
    }

    public boolean hasMainAddress() {
        return addresses.stream().anyMatch(a -> a.getIsMain() != null && a.getIsMain());
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void addAddress(Address address) {

        address.setPerson(this);

        this.addresses.add(address);
    }

    public void removeAddress(Long id) {
        this.addresses.removeIf(address -> address.getId().equals(id));
    }

    public void removeAddress(Address address) {
        this.addresses.remove(address);
    }

    public Address getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(Address newMainAddress) {
        newMainAddress.setIsMain(true);
        this.mainAddress = newMainAddress;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static class Builder {

        private Person person = new Person();

        public Builder id(Long personId) {
            person.id = personId;
            return this;
        }

        public Builder fullName(String fullName) {
            person.fullName = fullName;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            person.birthDate = birthDate;
            return this;
        }

        public Builder address(Address address) {
            person.addAddress(address);
            return this;
        }

        public Builder addresses(List<Address> addresses) {
            person.addresses = addresses;
            return this;
        }

        public Builder mainAddress(Address mainAddress) {
            person.mainAddress = mainAddress;
            return this;
        }

        public Builder active(boolean active) {
            person.active = active;
            return this;
        }

        public Person build() {
            return person;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", birthDate=" + birthDate +
                ", addresses=" + addresses +
                ", mainAddress=" + mainAddress +
                ", active=" + active +
                '}';
    }
}
