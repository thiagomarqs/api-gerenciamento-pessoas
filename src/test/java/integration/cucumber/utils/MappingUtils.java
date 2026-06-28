package integration.cucumber.utils;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request.EditPersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.UpdateProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;

public class MappingUtils {

    public static EditPersonRequest convertPersonToEditPersonRequest(Person person) {
        UpdateProfessionalDataRequest professionalDataRequest = null;
        
        if (person.getProfessionalData() != null) {
            professionalDataRequest = new UpdateProfessionalDataRequest(
                    person.getProfessionalData().getCompanyName(),
                    person.getProfessionalData().getContractType(),
                    person.getProfessionalData().getEmploymentStartDate()
            );
        }

        return new EditPersonRequest(
                person.getFullName(),
                person.getBirthDate(),
                person.isActive(),
                professionalDataRequest
        );
    }
}
