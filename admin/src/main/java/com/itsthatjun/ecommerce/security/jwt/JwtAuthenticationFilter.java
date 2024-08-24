package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.HEADER_STRING}")
    private String header;
    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    private final AdminServiceImpl adminService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtAuthenticationFilter(AdminServiceImpl adminService, JwtTokenUtil jwtTokenUtil) {
        this.adminService = adminService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /*

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(header);
            if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
                String jwt = getJWTFromRequest(request);
                UserDetails userDetails = adminService.loadUserByUsername(jwtTokenUtil.getUsernameFromToken(jwt));
                if (jwt != null && jwtTokenUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }
     */

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(header);
        if (!bearerToken.isEmpty() && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length()).trim(); // "bearer " length is 7
        }
        return null;
    }
}
