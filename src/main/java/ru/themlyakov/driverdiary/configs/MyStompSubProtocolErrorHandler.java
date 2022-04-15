package ru.themlyakov.driverdiary.configs;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MyStompSubProtocolErrorHandler extends StompSubProtocolErrorHandler {
    private static final Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public MyStompSubProtocolErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable exception=ex.getCause();
        if (exception instanceof JwtException) {
            return handleTokenExceptions(ex);
        }
        if (exception instanceof AccessDeniedException) {
            return handleAccessDeniedException();
        }


        if (ex instanceof Exception) {
            return handleOtherExceptions(ex);
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> handleOtherExceptions(Throwable throwable) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        String messageJson = prepareErrorJSON(throwable.getLocalizedMessage());
        accessor.setNativeHeader("status", "400");
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(messageJson.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }

    private Message<byte[]> handleTokenExceptions(Throwable ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        String messageJson;
        if (ex.getCause() instanceof MalformedJwtException) {
            messageJson = prepareErrorJSON("Поврежденный токен");
        } else if (ex.getCause() instanceof ExpiredJwtException) {
            messageJson = prepareErrorJSON("Токен является просроченным");
        } else if (ex.getCause() instanceof UnsupportedJwtException) {
            messageJson = prepareErrorJSON("Токен не поддерживается");
        } else if (ex.getCause() instanceof SignatureException) {
            messageJson = prepareErrorJSON("Неправильная сигнатура токена");
        } else {
            messageJson = prepareErrorJSON("Ошибка токена доступа");
        }
        accessor.setNativeHeader("status", "403");
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(messageJson.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }

    private Message<byte[]> handleAccessDeniedException() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        String messageJson = prepareErrorJSON("Доступ запрещен");
        accessor.setNativeHeader("status", "403");
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(messageJson.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }

    private String prepareErrorJSON(String message) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("response", message);
        return json.toJson(responseMap);
    }

}
