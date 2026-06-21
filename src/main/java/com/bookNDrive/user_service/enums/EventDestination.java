package com.bookNDrive.user_service.enums;

public enum EventDestination {

    SEND_FORGOT_PASSWORD_LINK("send-forgot-password-link-out-0"),
    SEND_FORMULA_SAVED("send-formula-saved-out-0");

    private final String destination;

    EventDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }
}
