package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.response.UserResponseDto;
import controlm.qrcodegenerator.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto userToDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setRole(userResponseDto.getRole());
        return userResponseDto;
    }
}
