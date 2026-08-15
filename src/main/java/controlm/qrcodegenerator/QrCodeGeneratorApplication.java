package controlm.qrcodegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class QrCodeGeneratorApplication {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(QrCodeGeneratorApplication.class, args);

//        RoleService roleService = context.getBean(RoleService.class);
//
//        roleService.create(RoleEnum.ADMIN.getName());
//        roleService.create(RoleEnum.USER.getName());
//
//        UserService userService = context.getBean(UserService.class);
//
//        RegistrationUserRequestDto registrationUserRequestDto = new RegistrationUserRequestDto();
//        registrationUserRequestDto.setName("admin");
//        registrationUserRequestDto.setSurname("admin");
//        registrationUserRequestDto.setRole(RoleEnum.ADMIN.getName());
//        userService.registerUser(registrationUserRequestDto);
//
//        RegistrationUserRequestDto registrationUserRequestDto1 = new RegistrationUserRequestDto();
//        registrationUserRequestDto1.setName("user");
//        registrationUserRequestDto1.setSurname("user");
//        registrationUserRequestDto1.setRole(RoleEnum.USER.getName());
//
//        userService.registerUser(registrationUserRequestDto1);
//
//        ClientService clientService = context.getBean(ClientService.class);
//
//        ClientCreateRequestDto clientCreateRequestDto = new ClientCreateRequestDto();
//        clientCreateRequestDto.setName("ООО Строим Как Себе");
//        Client client = clientService.createClient(clientCreateRequestDto);
//
//        ClientCreateRequestDto clientCreateRequestDto1 = new ClientCreateRequestDto();
//        clientCreateRequestDto1.setName("ООО Техно-стар");
//        Client client1 = clientService.createClient(clientCreateRequestDto1);
//
//        ClientCreateRequestDto clientCreateRequestDto2 = new ClientCreateRequestDto();
//        clientCreateRequestDto2.setName("ИП Демченко");
//        Client client2 = clientService.createClient(clientCreateRequestDto2);
//
//        for (int i = 0; i < 20; i++) {
//            ClientCreateRequestDto dto = new ClientCreateRequestDto();
//            dto.setName("Client" + i);
//            clientService.createClient(dto);
//        }
//
//        ProtocolService protocolService = context.getBean(ProtocolService.class);
    }

}
