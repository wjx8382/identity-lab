package com.wjx.identity.common.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CodeGenerator {

    public String generateCode() {

        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}
