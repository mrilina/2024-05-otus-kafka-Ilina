package org.carservice.payment;

public enum PaymentRequestType {
    REQUEST, CANCEL;

    public boolean isRequest() {
        return REQUEST == this;
    }
}
