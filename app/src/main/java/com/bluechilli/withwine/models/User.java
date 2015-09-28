package com.bluechilli.withwine.models;


/**
 * Created by monishi on 30/12/14.
 */
public class User {


    private boolean empty;
    private String fullPhoneNumber;

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getFullPhoneNumber() {
        return String.format("+%s%s", Constants.DEFAULT_COUNTRY_CODE, phoneNumber);
    }

    public Login getLogin() {
        return new Login() {{ phoneNumber = User.this.phoneNumber;}};
    }

    public enum UserStatus {
        Authenticated,
        LoggedIn,
        LoggedOut,
        LoggedInCompleted,
        Deleted
    }


    private int id;
    private String name;
    private String firstName;
    private String lastName;
    public String userKey;
    public boolean enablePushNotification;
    public String phoneNumber;

    public User() {
        enablePushNotification = true;
        empty = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        setEmpty(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setEmpty(false);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        setEmpty(false);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        setEmpty(false);
    }

    public void updateFromLogin(Login login) {
        this.userKey = login.userKey;
        this.enablePushNotification = login.enablePushNotification;
    }
}
