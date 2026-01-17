package controlm.qrcodegenerator;

import controlm.qrcodegenerator.dto.request.ClientRequestDto;
import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.ProtocolService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class QrCodeGeneratorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(QrCodeGeneratorApplication.class, args);

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
        protocolRequestDto.setName("НК-100-11");

        ProtocolRequestDto protocolRequestDto1 = new ProtocolRequestDto();
        protocolRequestDto1.setName("НК-100-12");

        protocolService.createProtocolByClientId(protocolRequestDto, client.getId());
        protocolService.createProtocolByClientId(protocolRequestDto1, client.getId());

        ProtocolRequestDto protocolRequestDto2 = new ProtocolRequestDto();
        protocolRequestDto2.setName("НК-55а-11");
        ProtocolRequestDto protocolRequestDto3 = new ProtocolRequestDto();
        protocolRequestDto3.setName("НК-55-11");

        protocolService.createProtocolByClientId(protocolRequestDto2, client1.getId());
        protocolService.createProtocolByClientId(protocolRequestDto3, client1.getId());

        ProtocolRequestDto protocolRequestDto4 = new ProtocolRequestDto();
        protocolRequestDto4.setName("НК-14-15");
        ProtocolRequestDto protocolRequestDto5 = new ProtocolRequestDto();
        protocolRequestDto5.setName("НК-14г-13");

        protocolService.createProtocolByClientId(protocolRequestDto4, client2.getId());
        protocolService.createProtocolByClientId(protocolRequestDto5, client2.getId());

    }

}
