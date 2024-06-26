package ru.themlyakov.driverdiary.configs;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.themlyakov.driverdiary.details.MyUserDetails;
import ru.themlyakov.driverdiary.details.MyUserDetailsService;
import ru.themlyakov.driverdiary.providers.JwtProvider;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.*;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Log
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {
    private DefaultSimpUserRegistry userRegistry = new DefaultSimpUserRegistry();
    private DefaultUserDestinationResolver resolver = new DefaultUserDestinationResolver(userRegistry);

    @Bean
    @Primary
    public SimpUserRegistry userRegistry() {
        return userRegistry;
    }

    @Bean
    @Primary
    public UserDestinationResolver userDestinationResolver() {
        return resolver;
    }


    @Autowired
    JwtProvider jwtProvider;


    @Autowired
    private MyUserDetailsService customUserDetailsService;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setErrorHandler(new MyStompSubProtocolErrorHandler());
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(25000)
                .setDisconnectDelay(5000)
                .setClientLibraryUrl("/webjars/sockjs-client/1.1.2/sockjs.js")
                .setSessionCookieNeeded(false);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
        registry.enableSimpleBroker("/topic", "/user", "/session")
                .setTaskScheduler(handleHeartbeatScheduler());
    }

    @Bean
    public TaskScheduler handleHeartbeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @SneakyThrows
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if(accessor.getMessageType()==SimpMessageType.HEARTBEAT){
                    return MessageBuilder.createMessage(message.getPayload(),message.getHeaders());
                }
                List<String> tokenList = accessor.getNativeHeader("Authorization");
                String token = null;
                if (tokenList == null || tokenList.size() < 1) {
                    return message;
                } else {
                    token = tokenList.get(0);
                    if (token == null) {
                        return message;
                    }
                }
                token = tokenList.get(0);
                if (token == null) {
                    return message;
                }
                if (jwtProvider.validateToken(token)) {
                    String userLogin = jwtProvider.getLoginFromToken(token);
                    MyUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(userLogin);
                    if (customUserDetails != null) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                        if (customUserDetails.isActive()) {
                            if (accessor.getMessageType() == SimpMessageType.CONNECT) {
                                userRegistry.onApplicationEvent(new SessionConnectedEvent(this, (Message<byte[]>) message, auth));
                            }
                            else if (accessor.getMessageType() == SimpMessageType.SUBSCRIBE) {
                                boolean hasPermission = userHasPermissionToSubscribeThisDestination(accessor, auth.getName());
                                userRegistry.onApplicationEvent(new SessionSubscribeEvent(this, (Message<byte[]>) message, auth));
                                if (!hasPermission)
                                    return unsubscribeMessage(accessor, auth);
                            } else if (accessor.getMessageType() == SimpMessageType.UNSUBSCRIBE) {
                                userRegistry.onApplicationEvent(new SessionUnsubscribeEvent(this, (Message<byte[]>) message, auth));
                            } else if (accessor.getMessageType() == SimpMessageType.DISCONNECT) {
                                userRegistry.onApplicationEvent(new SessionDisconnectEvent(this, (Message<byte[]>) message, accessor.getSessionId(), CloseStatus.NORMAL));
                            }
                            accessor.setUser(auth);
                            accessor.setLeaveMutable(true);
                            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                        }
                    }
                }
                return message;
            }
        });
    }

    private boolean userHasPermissionToSubscribeThisDestination(StompHeaderAccessor accessor, String username) {
        String destination = accessor.getDestination();
        if (destination != null) {
            if (destination.matches("^/(editor|user|admin)/" + username + "/.*")) {
                return true;
            }
            if (destination.matches("^/session/.*"))
                if (accessor.getSessionId() == null)
                    return false;
            return destination.matches("^/session/" + accessor.getSessionId() + "/.*");
        }
        return true;
    }


    private Message<?> unsubscribeMessage(StompHeaderAccessor accessor, Authentication authentication) {
        SimpMessageHeaderAccessor accessorSimp = SimpMessageHeaderAccessor.create(SimpMessageType.UNSUBSCRIBE);
        accessorSimp.setSessionId(accessor.getSessionId());
        accessorSimp.setSubscriptionId(accessor.getSessionId());
        accessorSimp.setUser(authentication);
        accessorSimp.setLeaveMutable(true);
        return MessageBuilder.createMessage("", accessorSimp.getMessageHeaders());
    }


}
