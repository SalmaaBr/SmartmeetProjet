package tn.esprit.examen.Smartmeet.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.listener.ExceptionListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Slf4j
@org.springframework.context.annotation.Configuration
public class WebSocketConfig {

    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    @Bean
    @Primary
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        // Enable CORS for all origins
        config.setOrigin("*");
        
        // Configure socket options
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        config.setSocketConfig(socketConfig);
        
        // Use only WebSocket transport to avoid polling issues
        config.setTransports(Transport.WEBSOCKET);
        
        // Set ping interval (milliseconds)
        config.setPingInterval(25000);
        config.setPingTimeout(60000);
        
        // Set up a custom exception handler
        config.setExceptionListener(new ExceptionListener() {
            @Override
            public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
                if (e instanceof NullPointerException) {
                    log.warn("Suppressed NullPointerException in Socket.IO: {}", e.getMessage());
                    return true; // Suppress the NPE
                }
                log.error("Socket.IO exception: {}", e.getMessage());
                return false; // Let other exceptions propagate
            }
            
            @Override
            public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
                log.error("Event exception: {}", e.getMessage());
            }
            
            @Override
            public void onDisconnectException(Exception e, SocketIOClient client) {
                log.error("Disconnect exception: {}", e.getMessage());
            }
            
            @Override
            public void onConnectException(Exception e, SocketIOClient client) {
                log.error("Connect exception: {}", e.getMessage());
            }
            
            public void onAuthException(Exception e, SocketIOClient client) {
                log.error("Auth exception: {}", e.getMessage());
            }
            
            @Override
            public void onPingException(Exception e, SocketIOClient client) {
                log.error("Ping exception: {}", e.getMessage());
            }
            
            @Override
            public void onPongException(Exception e, SocketIOClient client) {
                log.error("Pong exception: {}", e.getMessage());
            }
        });

        // Allow using the same connection for multiple requests
        config.setAllowCustomRequests(true);
        
        // Increase message buffer size for larger payloads
        config.setMaxFramePayloadLength(64 * 1024);
        config.setMaxHttpContentLength(64 * 1024);
        
        // Add verbose debug logging
        config.setRandomSession(true);
        
        // Create the server with our custom configuration
        SocketIOServer server = new SocketIOServer(config);
        
        log.info("Configured Socket.IO server on {}:{} using WebSocket transport", host, port);
        
        return server;
    }
} 