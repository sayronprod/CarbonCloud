package com.example.myapplication;

public class AuthMsg {
    private String access_token;

    private String expires;

    private String token_type;

    private String userName;

    private String expires_in;

    private String issued;

    public String getAccess_token ()
    {
        return access_token;
    }

    public void setAccess_token (String access_token)
    {
        this.access_token = access_token;
    }

    public String getexpires ()
    {
        return expires;
    }

    public void setexpires (String expires)
    {
        this.expires = expires;
    }

    public String getToken_type ()
    {
        return token_type;
    }

    public void setToken_type (String token_type)
    {
        this.token_type = token_type;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    public String getExpires_in ()
    {
        return expires_in;
    }

    public void setExpires_in (String expires_in)
    {
        this.expires_in = expires_in;
    }

    public String getissued ()
    {
        return issued;
    }

    public void setissued (String issued)
    {
        this.issued = issued;
    }

    @Override
    public String toString()
    {
        return "AuthMsg [access_token = "+access_token+", .expires = "+expires+", token_type = "+token_type+", userName = "+userName+", expires_in = "+expires_in+", .issued = "+issued+"]";
    }

}
