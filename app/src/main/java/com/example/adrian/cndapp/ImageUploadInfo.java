package com.example.adrian.cndapp;

public class ImageUploadInfo {
    String title;
    String description;
    String address;
    String contactPerson;
    String phone;
    String fax;
    String email;
    String website;
    String facebook;
    String image;

    public ImageUploadInfo() {
    }

    public ImageUploadInfo(String title, String description, String address,
                           String contactPerson, String phone, String fax, String email,
                           String website, String facebook, String image) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.website = website;
        this.facebook = facebook;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getImage() {
        return image;
    }
}
