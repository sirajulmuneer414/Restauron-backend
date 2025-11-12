package dev.siraj.restauron.DTO.authentication;

public class JwtAuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String newRefreshToken;

    public JwtAuthResponse(String token,String refreshToken){
        this.token = token;
        this.newRefreshToken = refreshToken;
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

    public String getNewRefreshToken() {
        return newRefreshToken;
    }

    public void setNewRefreshToken(String newRefreshToken) {
        this.newRefreshToken = newRefreshToken;
    }
}
