package com.itextpdf.dito.manager.dto.option;

public class OptionsDTO {
    private boolean emailDeliveryEnabled;

    public OptionsDTO(boolean emailDeliveryEnabled) {
        this.emailDeliveryEnabled = emailDeliveryEnabled;
    }

    public boolean getEmailDeliveryEnabled() {
        return emailDeliveryEnabled;
    }

    public void setEmailDeliveryEnabled(boolean emailDeliveryEnabled) {
        this.emailDeliveryEnabled = emailDeliveryEnabled;
    }
}
