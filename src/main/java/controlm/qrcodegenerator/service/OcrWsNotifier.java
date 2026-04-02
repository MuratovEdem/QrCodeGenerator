package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.OcrJobEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrWsNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(String username, OcrJobEvent event) {
        log.info("=== WEBSOCKET SEND ===");
        log.info("To user: {}", username);
        log.info("Destination: /user/{}/topic/ocr", username);
        log.info("Payload: {}", event);
        log.info("======================");
        messagingTemplate.convertAndSendToUser(username, "/topic/ocr", event);
    }
}
