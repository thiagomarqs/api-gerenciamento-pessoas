package com.github.thiagomarqs.gerenciamentopessoas.domain.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class ProfessionalData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    private LocalDate employmentStartDate;

    @OneToOne(mappedBy = "professionalData")
    private Person person;

    public ProfessionalData() {}

    public ProfessionalData(String companyName, ContractType contractType, LocalDate employmentStartDate, Person person) {
        this.companyName = companyName;
        this.contractType = contractType;
        this.employmentStartDate = employmentStartDate;
        this.person = person;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public LocalDate getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(LocalDate employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public static class Builder {

        private ProfessionalData professionalData = new ProfessionalData();

        public Builder id(Long id) {
            professionalData.id = id;
            return this;
        }

        public Builder companyName(String companyName) {
            professionalData.companyName = companyName;
            return this;
        }

        public Builder contractType(ContractType contractType) {
            professionalData.contractType = contractType;
            return this;
        }

        public Builder employmentStartDate(LocalDate employmentStartDate) {
            professionalData.employmentStartDate = employmentStartDate;
            return this;
        }

        public Builder person(Person person) {
            professionalData.person = person;
            return this;
        }

        public ProfessionalData build() {
            return professionalData;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfessionalData that = (ProfessionalData) o;
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProfessionalData{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", contractType=" + contractType +
                ", employmentStartDate=" + employmentStartDate +
                '}';
    }
}
