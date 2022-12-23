package com.filipmajewski.sieci.container;

import com.filipmajewski.sieci.entity.Device;
import com.filipmajewski.sieci.entity.User;

public class OfferContainer {

    private User user;

    private Device device;

    public OfferContainer(User user, Device device) {
        this.user = user;
        this.device = device;
    }

    public User getUser() {
        return user;
    }

    public Device getDevice() {
        return device;
    }
}
