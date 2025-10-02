package dev.siraj.restauron.DTO.authentication;

public class JwtAuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String refreshToken;

    public JwtAuthResponse(String token,String refreshToken){
        this.token = token;
        this.refreshToken = refreshToken;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
