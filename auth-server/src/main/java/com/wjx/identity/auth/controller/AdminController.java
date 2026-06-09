package com.wjx.identity.auth.controller;

import com.wjx.identity.auth.dto.CurrentUserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    public String test() {
        return "admin success";
    }

    @GetMapping("/current")
    public CurrentUserResponse current() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        List<String> roles =
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        return new CurrentUserResponse(
                authentication.getName(),
                roles
        );
    }
}
