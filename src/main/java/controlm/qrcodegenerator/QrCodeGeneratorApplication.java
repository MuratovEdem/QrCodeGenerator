package controlm.qrcodegenerator;

import controlm.qrcodegenerator.dto.request.ClientCreateRequestDto;
import controlm.qrcodegenerator.dto.request.RegistrationUserDto;
import controlm.qrcodegenerator.enums.RoleEnum;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.ProtocolService;
import controlm.qrcodegenerator.service.RoleService;
import controlm.qrcodegenerator.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class QrCodeGeneratorApplication {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(QrCodeGeneratorApplication.class, args);

        RoleService roleService = context.getBean(RoleService.class);

        roleService.create(RoleEnum.ADMIN.getName());

        UserService userService = context.getBean(UserService.class);

        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("admin");
        registrationUserDto.setPassword("admin");

        userService.create(registrationUserDto);

        ClientService clientService = context.getBean(ClientService.class);

        ClientCreateRequestDto clientCreateRequestDto = new ClientCreateRequestDto();
        clientCreateRequestDto.setName("ООО Строим Как Себе");
        Client client = clientService.createClient(clientCreateRequestDto);

        ClientCreateRequestDto clientCreateRequestDto1 = new ClientCreateRequestDto();
        clientCreateRequestDto1.setName("ООО Техно-стар");
        Client client1 = clientService.createClient(clientCreateRequestDto1);

        ClientCreateRequestDto clientCreateRequestDto2 = new ClientCreateRequestDto();
        clientCreateRequestDto2.setName("ИП Демченко");
        Client client2 = clientService.createClient(clientCreateRequestDto2);

        for (int i = 0; i < 20; i++) {
            ClientCreateRequestDto dto = new ClientCreateRequestDto();
            dto.setName("Client" + i);
            clientService.createClient(dto);
        }

        ProtocolService protocolService = context.getBean(ProtocolService.class);

//        ProtocolRequestDto protocolRequestDto = new ProtocolRequestDto();
//        protocolRequestDto.setCipher("НК");
//        protocolRequestDto.setUniqueNumber("100а");
//        protocolRequestDto.setSequentialNumber("15");
//        protocolRequestDto.setClientId(client.getId());
//        protocolRequestDto.setIssueDate(LocalDate.of(2020, 1, 1));
//
//        ProtocolRequestDto protocolRequestDto1 = new ProtocolRequestDto();
//        protocolRequestDto1.setCipher("НК");
//        protocolRequestDto1.setUniqueNumber("100а");
//        protocolRequestDto1.setSequentialNumber("12");
//        protocolRequestDto1.setClientId(client.getId());
//        protocolRequestDto1.setIssueDate(LocalDate.of(2020, 1, 2));
//
//        protocolService.createProtocol(protocolRequestDto);
//        protocolService.createProtocol(protocolRequestDto1);
//
//        ProtocolRequestDto protocolRequestDto2 = new ProtocolRequestDto();
//        protocolRequestDto2.setCipher("КБ");
//        protocolRequestDto2.setUniqueNumber("17");
//        protocolRequestDto2.setSequentialNumber("16");
//        protocolRequestDto2.setClientId(client2.getId());
//        protocolRequestDto2.setIssueDate(LocalDate.of(2020, 1, 3));
//        ProtocolRequestDto protocolRequestDto3 = new ProtocolRequestDto();
//        protocolRequestDto3.setCipher("НК");
//        protocolRequestDto3.setUniqueNumber("17");
//        protocolRequestDto3.setSequentialNumber("17");
//        protocolRequestDto3.setClientId(client2.getId());
//        protocolRequestDto3.setIssueDate(LocalDate.of(2020, 1, 4));
//
//        ProtocolRequestDto protocolRequestDto4 = new ProtocolRequestDto();
//        protocolRequestDto4.setCipher("НК");
//        protocolRequestDto4.setUniqueNumber("17");
//        protocolRequestDto4.setSequentialNumber("18");
//        protocolRequestDto4.setClientId(client2.getId());
//        protocolRequestDto4.setIssueDate(LocalDate.of(2020, 1, 5));
//        ProtocolRequestDto protocolRequestDto5 = new ProtocolRequestDto();
//        protocolRequestDto5.setCipher("НК");
//        protocolRequestDto5.setUniqueNumber("17");
//        protocolRequestDto5.setSequentialNumber("19");
//        protocolRequestDto5.setClientId(client2.getId());
//        protocolRequestDto5.setIssueDate(LocalDate.of(2020, 1, 6));
//
//        protocolService.createProtocol(protocolRequestDto4);
//        protocolService.createProtocol(protocolRequestDto5);
//        protocolService.createProtocol(protocolRequestDto2);
//        protocolService.createProtocol(protocolRequestDto3);

    }

}
