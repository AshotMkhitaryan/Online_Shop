package com.test.dto.user;

public class SignInResponseDto {
  //  private String status;
    private String token;
/*
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }*/

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SignInResponseDto( String token) {

        this.token = token;
    }
}
