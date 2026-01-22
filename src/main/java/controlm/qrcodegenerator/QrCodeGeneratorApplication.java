package controlm.qrcodegenerator;

import controlm.qrcodegenerator.dto.request.ClientRequestDto;
import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
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

@SpringBootApplication
public class QrCodeGeneratorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(QrCodeGeneratorApplication.class, args);

        RoleService roleService = context.getBean(RoleService.class);

        roleService.create(RoleEnum.ADMIN.getName());

        UserService userService = context.getBean(UserService.class);

        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("admin");
        registrationUserDto.setPassword("admin");

        userService.create(registrationUserDto);


        ClientService clientService = context.getBean(ClientService.class);

        ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.setName("ООО Строим Как Себе");
        Client client = clientService.createClient(clientRequestDto);

        ClientRequestDto clientRequestDto1 = new ClientRequestDto();
        clientRequestDto1.setName("ООО Техно-стар");
        Client client1 = clientService.createClient(clientRequestDto1);

        ClientRequestDto clientRequestDto2 = new ClientRequestDto();
        clientRequestDto2.setName("ИП Демченко");
        Client client2 = clientService.createClient(clientRequestDto2);

        ProtocolService protocolService = context.getBean(ProtocolService.class);

        ProtocolRequestDto protocolRequestDto = new ProtocolRequestDto();
        protocolRequestDto.setCipher("НК");
        protocolRequestDto.setUniqueNumber("100а");
        protocolRequestDto.setSequentialNumber("15");
        protocolRequestDto.setClientId(client.getId());

        ProtocolRequestDto protocolRequestDto1 = new ProtocolRequestDto();
        protocolRequestDto1.setCipher("НК");
        protocolRequestDto1.setUniqueNumber("100а");
        protocolRequestDto1.setSequentialNumber("12");
        protocolRequestDto1.setClientId(client.getId());

        protocolService.createProtocols(protocolRequestDto);
        protocolService.createProtocols(protocolRequestDto1);

        ProtocolRequestDto protocolRequestDto2 = new ProtocolRequestDto();
        protocolRequestDto2.setCipher("КБ");
        protocolRequestDto2.setUniqueNumber("17");
        protocolRequestDto2.setSequentialNumber("16");
        protocolRequestDto2.setClientId(client2.getId());
        ProtocolRequestDto protocolRequestDto3 = new ProtocolRequestDto();
        protocolRequestDto3.setCipher("НК");
        protocolRequestDto3.setUniqueNumber("17");
        protocolRequestDto3.setSequentialNumber("17");
        protocolRequestDto3.setClientId(client2.getId());

        ProtocolRequestDto protocolRequestDto4 = new ProtocolRequestDto();
        protocolRequestDto4.setCipher("НК");
        protocolRequestDto4.setUniqueNumber("17");
        protocolRequestDto4.setSequentialNumber("18");
        protocolRequestDto4.setClientId(client2.getId());
        ProtocolRequestDto protocolRequestDto5 = new ProtocolRequestDto();
        protocolRequestDto5.setCipher("НК");
        protocolRequestDto5.setUniqueNumber("17");
        protocolRequestDto5.setSequentialNumber("19");
        protocolRequestDto5.setClientId(client2.getId());

        protocolService.createProtocols(protocolRequestDto4);
        protocolService.createProtocols(protocolRequestDto5);
        protocolService.createProtocols(protocolRequestDto2);
        protocolService.createProtocols(protocolRequestDto3);


    }

}
