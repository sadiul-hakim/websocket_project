package xyz.sadiulhakim.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper;

    @JmsListener(destination = "/chat-socket")
    private void send(Map<String, Object> msg) throws JsonProcessingException {

        // Do not broadcast on the topic, send to a specific user. Only that user would see the message.
        messagingTemplate.convertAndSendToUser((String) msg.get("user"), "/topic/messages", mapper.writeValueAsString(msg.get("message")), m -> {

            // Create a new header accessor to modify the headers
            MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(m, MessageHeaderAccessor.class);
            if (accessor != null) {
                accessor.setHeader("JMSExpiration", System.currentTimeMillis() + 60000L); // Expire after 60 seconds
            }
            // Return the modified message
            return m;
        });
    }
}
