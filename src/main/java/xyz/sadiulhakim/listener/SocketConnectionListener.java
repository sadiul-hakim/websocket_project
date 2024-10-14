package xyz.sadiulhakim.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Optional;


@Slf4j
@Component
public class SocketConnectionListener {

    @EventListener({SessionConnectEvent.class})
    public void connectionListener(SessionConnectEvent event) {
        Optional.ofNullable(readUser(event)).ifPresent(user -> log(event, user));
    }

    @EventListener({SessionDisconnectEvent.class})
    public void disconnectionListener(SessionDisconnectEvent event) {
        Optional.ofNullable(event.getUser()).ifPresent(user ->
                log.info("USer {} disconnected from session id {}", user.getName(), event.getSessionId())
        );
    }

    private void log(SessionConnectEvent event, Principal user) {
        String sessionId = readSessionId(event);
        log.info("User {} connected to session id {}", user.getName(), sessionId);
    }

    String readSessionId(SessionConnectEvent event) {
        return SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders());
    }

    Principal readUser(SessionConnectEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        return SimpMessageHeaderAccessor.getUser(headers);
    }
}
