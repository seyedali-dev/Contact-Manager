package com.springboot.contactmanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int contactId;
    @NotBlank(message = "Please fill this field")
//    @Size(min = 2, max = 20, message = "Must be between 2 and 20 characters")
    private String name;
    @Email
//    @NotBlank(message = "Please fill this field")
    private String email;
//    @NotBlank(message = "Please fill this field")
//    @Size(min = 10, max = 10, message = "must be 10 digits")
    private String phone;
    private String work;
    private String image;
    private String nickname;
    private String description;

    @ManyToOne
    @JsonIgnore
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Contact{" +
               "contactId=" + contactId +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", work='" + work + '\'' +
               ", image='" + image + '\'' +
               ", nickname='" + nickname + '\'' +
               ", description='" + description + '\'' +
               ", user=" + user +
               '}';
    }
}
