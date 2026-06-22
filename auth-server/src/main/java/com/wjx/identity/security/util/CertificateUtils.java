package com.wjx.identity.security.util;

import jakarta.servlet.http.HttpServletRequest;

import java.security.cert.X509Certificate;

public class CertificateUtils {

    private CertificateUtils() {
    }

    public static String getClientCertificateSubject(
            HttpServletRequest request
    ) {
        X509Certificate[] certificates =
                (X509Certificate[]) request.getAttribute(
                        "jakarta.servlet.request.X509Certificate"
                );

        if (certificates == null || certificates.length == 0) {
            return null;
        }

        return certificates[0]
                .getSubjectX500Principal()
                .getName();
    }
}
