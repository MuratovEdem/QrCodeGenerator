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

        ClientService bean = context.getBean(ClientService.class);
        ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.setName("ООО Строим Как Себе");
        Client client = bean.createClient(clientRequestDto);

        ProtocolService protocolService = context.getBean(ProtocolService.class);

        ProtocolRequestDto protocolRequestDto = new ProtocolRequestDto();
        protocolRequestDto.setName("НК-100-11");

        ProtocolRequestDto protocolRequestDto1 = new ProtocolRequestDto();
        protocolRequestDto1.setName("НК-100-12");

        protocolService.createProtocolByClientId(protocolRequestDto, client.getId());
        protocolService.createProtocolByClientId(protocolRequestDto1, client.getId());
    }

}
