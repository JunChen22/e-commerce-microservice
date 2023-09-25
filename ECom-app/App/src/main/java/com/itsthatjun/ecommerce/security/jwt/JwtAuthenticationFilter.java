package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.HEADER_STRING}")
    private String header;
    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(header);
            if(authHeader != null && authHeader.startsWith(tokenPrefix)) {
                String jwt = getJWTFromRequest(request);
                System.out.println(jwt);
                if (jwt != null && jwtTokenUtil.validateToken(jwt)) {
                    // after validate token
                    String name = jwtTokenUtil.getNameFromToken(jwt);
                    int userId = jwtTokenUtil.getUserIdFromToken(jwt);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(name, jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    UserContext userContext = new UserContext(name, userId);

                    request.setAttribute("userContext", userContext);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(header);
        if(!bearerToken.isEmpty() && bearerToken.startsWith(tokenPrefix)){
            return bearerToken.substring(tokenPrefix.length() + 1); // "bearer " length is 7
        }
        return null;
    }
}
