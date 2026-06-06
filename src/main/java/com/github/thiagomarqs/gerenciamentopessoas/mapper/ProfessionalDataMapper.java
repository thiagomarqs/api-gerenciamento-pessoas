package com.github.thiagomarqs.gerenciamentopessoas.mapper;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.ProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.UpdateProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.response.ProfessionalDataResponse;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ProfessionalData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.format.DateTimeFormatter;

@Mapper
public interface ProfessionalDataMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    ProfessionalData requestToEntity(ProfessionalDataRequest request);

    @Mapping(target = "contractType", expression = "java(professionalData.getContractType() != null ? professionalData.getContractType().name() : null)")
    @Mapping(target = "employmentStartDate", expression = "java(formatDate(professionalData.getEmploymentStartDate()))")
    ProfessionalDataResponse entityToResponse(ProfessionalData professionalData);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    void updateEntityFromRequest(UpdateProfessionalDataRequest request, @MappingTarget ProfessionalData entity);

    default String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
