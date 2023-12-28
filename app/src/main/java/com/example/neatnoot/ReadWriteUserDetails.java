package com.example.neatnoot;

public class ReadWriteUserDetails {
    public String username, dateOfBirth, gender, mobile;

    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textUsername, String textDateOfBirth, String textGender, String textMobile){
        this.username = textUsername;
        this.dateOfBirth = textDateOfBirth;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
