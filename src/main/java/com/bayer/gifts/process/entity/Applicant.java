package com.bayer.gifts.process.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class Applicant implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String applicantName;

    private String email;

    private String phoneNumber;

    public Applicant() {

    }

    public Applicant(String applicantName, String email, String phoneNumber) {
        this.applicantName = applicantName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}