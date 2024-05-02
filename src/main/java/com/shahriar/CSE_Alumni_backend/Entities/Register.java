package com.shahriar.CSE_Alumni_backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Register {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if(isValidEmail(email))
            this.email = email;
    }

    public static boolean isValidEmail(String email) {
        boolean isValid = true;
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        } catch (AddressException e) {
            isValid = false;
        }
        return isValid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public byte[] getStudentIdCardPic() {
        return studentIdCardPic;
    }

    public void setStudentIdCardPic(byte[] studentIdCardPic) {
        this.studentIdCardPic = studentIdCardPic;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public byte[] getPVCPic() {
        return PVCPic;
    }

    public void setPVCPic(byte[] PVCPic) {
        this.PVCPic = PVCPic;
    }





    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private String email;

    private String password;
    private String role;

    @Lob
    @Column(length = 10000000)
    private byte[] profilePic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus = UserStatus.PENDING;



    //Student specific info
    private String studentId;

    @Lob
    @Column(length = 10000000)
    private byte[] studentIdCardPic;


    //Alumnus specific info
    private String graduationYear;
    @Lob
    @Column(length = 10000000)
    private byte[] PVCPic;
}
