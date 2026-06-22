package com.wjx.identity.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.cert.X509Certificate;

@RestController
@RequestMapping("/mtls")
public class MtlsTestController {

    @GetMapping("/test")
    public String test(HttpServletRequest request) {

        X509Certificate[] certificates =
                (X509Certificate[]) request.getAttribute(
                        "jakarta.servlet.request.X509Certificate"
                );

        if (certificates == null || certificates.length == 0) {
            return "No client certificate";
        }

        return "MTLS success: " +
                certificates[0].getSubjectX500Principal().getName();
    }
}
