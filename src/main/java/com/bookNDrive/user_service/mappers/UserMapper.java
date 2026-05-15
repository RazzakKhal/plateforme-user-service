package com.bookNDrive.user_service.mappers;

import com.bookNDrive.user_service.dtos.received.AddressDto;
import com.bookNDrive.user_service.dtos.received.SubscriptionDto;
import com.bookNDrive.user_service.dtos.sended.UserDto;
import com.bookNDrive.user_service.entities.Address;
import com.bookNDrive.user_service.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User subscriptionDtoToUser(SubscriptionDto subscriptionDto);

    @Mapping(target = "id", ignore = true)
    Address toAddress(AddressDto dto);

    UserDto userToUserDto(User user);
}
