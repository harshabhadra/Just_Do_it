package com.technoidtintin.justdoit.Model;

public class UserDetails {

    private String userName;
    private String userEmail;
    private String accountCreated;

    public UserDetails( String userName, String userEmail,String accountCreated) {

        this.userName = userName;
        this.userEmail = userEmail;
        this.accountCreated = accountCreated;
    }

    public String getAccountCreated() {
        return accountCreated;
    }

    public void setAccountCreated(String accountCreated) {
        this.accountCreated = accountCreated;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
