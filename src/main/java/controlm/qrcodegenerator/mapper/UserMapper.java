package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.response.UserResponseDto;
import controlm.qrcodegenerator.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto userToDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        return userResponseDto;
    }
}
