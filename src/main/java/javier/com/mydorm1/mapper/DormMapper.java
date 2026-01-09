package javier.com.mydorm1.mapper;

import javier.com.mydorm1.dto.DormResponseDto;
import javier.com.mydorm1.model.Dormitory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DormMapper {

    @Mappings(
            {
                    @Mapping(target = "ownerId", source = "owner.id"),
                    @Mapping(target = "ownerFirstName", source = "owner.firstName"),
                    @Mapping(target = "ownerLastName", source = "owner.lastName"),
                    @Mapping(target = "ownerMiddleName", source = "owner.middleName")
            }
    )
    DormResponseDto toDormResponseDto(Dormitory dormitory);
}
