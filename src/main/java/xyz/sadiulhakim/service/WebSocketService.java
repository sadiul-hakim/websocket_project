package xyz.sadiulhakim.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(final String userId, final String message) {
        this.send(userId, message);
    }

    @SneakyThrows
    private void send(String userId, String message) {
        String json = (new ObjectMapper()).writeValueAsString(new WebSocketResponseMessage(message));

        // Do not broadcast on the topic, send to a specific user. Only that user would see the message.
        messagingTemplate.convertAndSendToUser(userId, "/topic/messages", json, m -> {

            // Create a new header accessor to modify the headers
            MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(m, MessageHeaderAccessor.class);
            if (accessor != null) {
                accessor.setHeader("JMSExpiration", System.currentTimeMillis() + 60000L); // Expire after 60 seconds
            }
            // Return the modified message
            return m;
        });
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WebSocketResponseMessage {

        private String content;

    }
}
