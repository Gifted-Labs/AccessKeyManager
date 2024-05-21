package com.juls.accesskeymanager.data.models;

public record AuthenticationRequest(String email, String password, Role role) {

}
