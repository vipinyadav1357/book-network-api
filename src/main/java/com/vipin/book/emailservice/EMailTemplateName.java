package com.vipin.book.emailservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EMailTemplateName {
    ACTIVATE_ACCOUNT("activate_account");

    private final String name;
}
