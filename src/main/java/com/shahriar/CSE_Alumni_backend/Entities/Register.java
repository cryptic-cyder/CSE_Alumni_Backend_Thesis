package com.shahriar.CSE_Alumni_backend.Entities;

import jakarta.persistence.*;
import lombok.*;

//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Register {


    public void setEmail(String email) {
        //if(isValidEmail(email))
            this.email = email;
    }

//    public static boolean isValidEmail(String email) {
//        boolean isValid = true;
//        try {
//            InternetAddress internetAddress = new InternetAddress(email);
//            internetAddress.validate();
//        } catch (AddressException e) {
//            isValid = false;
//        }
//        return isValid;
//    }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private String email;

    private String password;

    @Lob
    @Column(length = 10000000)
    private byte[] profilePic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus = UserStatus.PENDING;

    @Lob
    @Column(length = 10000000)
    private byte[] identity;

    private String profDetails;


    //Student specific info
    private String studentId;


    //Alumnus specific info
    private String graduationYear;

}
