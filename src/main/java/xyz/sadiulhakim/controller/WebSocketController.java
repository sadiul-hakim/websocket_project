package xyz.sadiulhakim.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.sadiulhakim.service.WebSocketService;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final WebSocketService webSocketService;

    @MessageMapping("/message/{toUser}")
    public Boolean sendMessage(
            Principal principal,
            @Header String authKey,
            @DestinationVariable String toUser,
            @RequestBody WebSocketRequestMessage message) {
        log.info("Send message from user {} to user {}. Auth key {}", principal.getName(), toUser, authKey);
        webSocketService.notifyUser(toUser, message.getMessageContent());
        return Boolean.TRUE;
    }


    @Getter
    @Setter
    public static class WebSocketRequestMessage {
        private String messageContent;
    }
}
