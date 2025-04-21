package tn.esprit.examen.Smartmeet.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),  // For account activation emails
    RESET_PASSWORD("reset_password");      // For password reset emails

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}