package com.bookNDrive.user_service.mappers;

import com.bookNDrive.user_service.dtos.received.AddressDto;
import com.bookNDrive.user_service.dtos.received.SubscriptionDto;
import com.bookNDrive.user_service.dtos.sended.UserDto;
import com.bookNDrive.user_service.models.Adress;
import com.bookNDrive.user_service.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    @Mapping(source = "address", target = "adress")
    User subscriptionDtoToUser(SubscriptionDto subscriptionDto);

    Adress toAdress(AddressDto dto);

    @Mapping(source = "adress", target = "address")
    @Mapping(target = "id", ignore = false) // facultatif aussi
    UserDto userToUserDto(User user);

}
