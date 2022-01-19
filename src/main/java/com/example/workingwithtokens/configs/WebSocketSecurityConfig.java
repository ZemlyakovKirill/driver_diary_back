package com.example.workingwithtokens.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpSubscribeDestMatchers("/topic/user/**").authenticated()
                .simpSubscribeDestMatchers("/topic/editor/**").hasRole("EDITOR")
                .simpSubscribeDestMatchers("/topic/admin/**").hasRole("ADMIN")
                .simpSubscribeDestMatchers("/topic/greetings").hasRole("ADMIN")
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
