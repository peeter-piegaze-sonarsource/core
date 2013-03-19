package org.meveo.security;

import java.io.Serializable;

import org.picketlink.idm.api.User;

public class MeveoUser implements User, Serializable {

    private static final long serialVersionUID = 4333140556503076034L;

    private org.meveo.model.admin.User user;

    public MeveoUser(org.meveo.model.admin.User user) {
        this.user = user;
    }

    @Override
    public String getKey() {
        return getId();
    }

    @Override
    public String getId() {
        return user.getUsername();
    }

    public org.meveo.model.admin.User getUser() {
        return this.user;
    }
}