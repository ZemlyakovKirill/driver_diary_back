package com.example.workingwithtokens.configs;

import com.example.workingwithtokens.details.MyUserDetails;
import com.example.workingwithtokens.details.MyUserDetailsService;
import com.example.workingwithtokens.providers.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.*;

import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
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
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                List<String> tokenList = accessor.getNativeHeader("Authorization");
                String token = null;
                if(tokenList == null || tokenList.size() < 1) {
                    return message;
                } else {
                    token = tokenList.get(0);
                    if(token == null) {
                        return message;
                    }
                }
                if(tokenList == null || tokenList.size() < 1) {
                    return message;
                } else {
                    token = tokenList.get(0);
                    if(token == null) {
                        return message;
                    }
                }
                System.out.println(tokenList);
                    if (token != null && jwtProvider.validateToken(token)) {
                        System.out.println(token);
                        String userLogin = jwtProvider.getLoginFromToken(token);
                        MyUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(userLogin);
                        if (customUserDetails != null) {
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                            if (customUserDetails.isActive()) {
                                if (accessor.getMessageType() == SimpMessageType.CONNECT) {
                                    userRegistry.onApplicationEvent(new SessionConnectedEvent(this, (Message<byte[]>) message, auth));
                                } else if (accessor.getMessageType() == SimpMessageType.SUBSCRIBE) {
                                    userRegistry.onApplicationEvent(new SessionSubscribeEvent(this, (Message<byte[]>) message, auth));
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
}
