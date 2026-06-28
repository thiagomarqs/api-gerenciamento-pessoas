package integration.cucumber.fixture;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ProfessionalData;

import java.time.LocalDate;

public class PersonFixture {

    public static String createPersonRequest = """
                {
                  "fullName": "string",
                  "birthDate": "2026-06-28",
                  "addresses": [
                    {
                      "address": "string",
                      "cep": "21572-065",
                      "number": "string",
                      "city": "string",
                      "state": "string",
                      "isMain": true
                    }
                  ],
                  "professionalData": {
                    "companyName": "string",
                    "contractType": "CLT",
                    "employmentStartDate": "2026-06-28"
                  }
                }
                """;

    public static Person activePerson() {
        Address activeAddress = Address.builder()
                .address("Rua Exemplo")
                .cep("21572-065")
                .number("123")
                .city("Rio de Janeiro")
                .state("RJ")
                .active(true)
                .isMain(true)
                .build();

        ProfessionalData professionalData = ProfessionalData.builder()
                .companyName("Empresa Exemplo")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.of(2024, 1, 1))
                .build();

        return Person.builder()
                .fullName("Pessoa Ativa")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(activeAddress)
                .mainAddress(activeAddress)
                .professionalData(professionalData)
                .active(true)
                .build();
    }
}
