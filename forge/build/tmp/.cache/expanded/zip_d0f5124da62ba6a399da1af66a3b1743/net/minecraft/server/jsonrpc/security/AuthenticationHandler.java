package net.minecraft.server.jsonrpc.security;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;

@Sharable
public class AuthenticationHandler extends ChannelInboundHandlerAdapter {
    private final Logger LOGGER = LogUtils.getLogger();
    private static final AttributeKey<Boolean> AUTHENTICATED_KEY = AttributeKey.valueOf("authenticated");
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final SecurityConfig securityConfig;

    public AuthenticationHandler(SecurityConfig p_426248_) {
        this.securityConfig = p_426248_;
    }

    @Override
    public void channelRead(ChannelHandlerContext p_430373_, Object p_423931_) throws Exception {
        String s = this.getClientIp(p_430373_);
        if (p_423931_ instanceof HttpRequest httprequest) {
            AuthenticationHandler.SecurityCheckResult authenticationhandler$securitycheckresult = this.performSecurityChecks(httprequest);
            if (!authenticationhandler$securitycheckresult.isAllowed()) {
                this.LOGGER.debug("Authentication rejected for connection with ip {}: {}", s, authenticationhandler$securitycheckresult.getReason());
                p_430373_.channel().attr(AUTHENTICATED_KEY).set(false);
                this.sendUnauthorizedResponse(p_430373_, authenticationhandler$securitycheckresult.getReason());
                return;
            }

            p_430373_.channel().attr(AUTHENTICATED_KEY).set(true);
        }

        Boolean obool = p_430373_.channel().attr(AUTHENTICATED_KEY).get();
        if (Boolean.TRUE.equals(obool)) {
            super.channelRead(p_430373_, p_423931_);
        } else {
            this.LOGGER.debug("Dropping unauthenticated connection with ip {}", s);
            p_430373_.close();
        }
    }

    private AuthenticationHandler.SecurityCheckResult performSecurityChecks(HttpRequest p_427664_) {
        return !this.validateAuthentication(p_427664_)
            ? AuthenticationHandler.SecurityCheckResult.denied("Invalid or missing API key")
            : AuthenticationHandler.SecurityCheckResult.allowed();
    }

    private boolean validateAuthentication(HttpRequest p_427808_) {
        String s = p_427808_.headers().get("Authorization");
        if (s == null || s.trim().isEmpty()) {
            return false;
        } else if (s.startsWith("Bearer ")) {
            String s1 = s.substring("Bearer ".length()).trim();
            return this.isValidApiKey(s1);
        } else {
            return false;
        }
    }

    public boolean isValidApiKey(String p_425797_) {
        if (p_425797_ != null && !p_425797_.isEmpty()) {
            byte[] abyte = p_425797_.getBytes(StandardCharsets.UTF_8);
            byte[] abyte1 = this.securityConfig.secretKey().getBytes(StandardCharsets.UTF_8);
            return MessageDigest.isEqual(abyte, abyte1);
        } else {
            return false;
        }
    }

    private String getClientIp(ChannelHandlerContext p_428847_) {
        InetSocketAddress inetsocketaddress = (InetSocketAddress)p_428847_.channel().remoteAddress();
        return inetsocketaddress.getAddress().getHostAddress();
    }

    private void sendUnauthorizedResponse(ChannelHandlerContext p_428321_, String p_427324_) {
        String s = "{\"error\":\"Unauthorized\",\"message\":\"" + p_427324_ + "\"}";
        byte[] abyte = s.getBytes(StandardCharsets.UTF_8);
        DefaultFullHttpResponse defaultfullhttpresponse = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.wrappedBuffer(abyte)
        );
        defaultfullhttpresponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        defaultfullhttpresponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, abyte.length);
        defaultfullhttpresponse.headers().set(HttpHeaderNames.CONNECTION, "close");
        p_428321_.writeAndFlush(defaultfullhttpresponse).addListener(p_431580_ -> p_428321_.close());
    }

    static class SecurityCheckResult {
        private final boolean allowed;
        private final String reason;

        private SecurityCheckResult(boolean p_425304_, String p_424364_) {
            this.allowed = p_425304_;
            this.reason = p_424364_;
        }

        public static AuthenticationHandler.SecurityCheckResult allowed() {
            return new AuthenticationHandler.SecurityCheckResult(true, null);
        }

        public static AuthenticationHandler.SecurityCheckResult denied(String p_424213_) {
            return new AuthenticationHandler.SecurityCheckResult(false, p_424213_);
        }

        public boolean isAllowed() {
            return this.allowed;
        }

        public String getReason() {
            return this.reason;
        }
    }
}