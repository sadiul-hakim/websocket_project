package xyz.sadiulhakim.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final JmsTemplate jmsTemplate;

    @MessageMapping("/message/{toUser}")
    public Boolean sendMessage(
            Principal principal,
            @Header String authKey,
            @DestinationVariable String toUser,
            @RequestBody WebSocketRequestMessage message) {
        log.info("Send message from user {} to user {}. Auth key {}", principal.getName(), toUser, authKey);

        Map<String, Object> msg = new HashMap<>();
        msg.put("user", toUser);
        msg.put("message", message);

//        String json = mapper.writeValueAsString(msg);

        // When receive a message publish in activemq topic
        jmsTemplate.convertAndSend("/chat-socket", msg);
        return Boolean.TRUE;
    }


    @Getter
    @Setter
    public static class WebSocketRequestMessage {
        private String messageContent;
    }
}
