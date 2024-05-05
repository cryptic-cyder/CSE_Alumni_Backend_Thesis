package com.shahriar.CSE_Alumni_backend.Controllers;


import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Services.RegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class Admin {

    @Autowired
    private RegService regService;


    @PostMapping("/adminLogin")
    public ResponseEntity<?> adminLogin(@RequestParam("email") String emailOfAdmin,
                                        @RequestParam("password") String passwordOfAdmin) {

        System.out.println(emailOfAdmin+" "+passwordOfAdmin);

        if (regService.adminLogin(emailOfAdmin, passwordOfAdmin)) {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Admin login successful...");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Password incorrect...Access denied...Please try again");

    }

    @GetMapping("/pendingRequests")
    public ResponseEntity<?> getPendingRegistrations() {

        if (regService.returnAdminStatus() == 1) {
            List<Register> pendingUsers = regService.getPendingUsers();

            if (pendingUsers == null) {
                return new ResponseEntity<>("No requests pending", HttpStatus.OK);
            }

            saveImagesOfPendingRequest(pendingUsers);

            return new ResponseEntity<>(pendingUsers, HttpStatus.OK);
        }

        return new ResponseEntity<>("Access denied...login as admin first", HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/approveAcc/{email}")
    public ResponseEntity<String> approveAccount(@PathVariable String email) {

        if (regService.returnAdminStatus() == 1) {
            Register register = regService.approveRegistration(email);

            if (register.equals(new Register()))
                return new ResponseEntity<>("Sorry no such account exists", HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>("Registration approved", HttpStatus.OK);
        }

        return new ResponseEntity<>("Access denied...login as admin first", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/rejectAcc/{email}")
    public ResponseEntity<String> rejectAccount(@PathVariable String email) {

        if (regService.returnAdminStatus() == 1) {

            Register register = regService.rejectRegistration(email);

            if (register.equals(new Register()))
                return new ResponseEntity<>("Sorry no such account exists", HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>("Sorry!! You request has been rejected", HttpStatus.OK);
        }

        return new ResponseEntity<>("Access denied...login as admin first", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/adminLogout")
    public ResponseEntity<String> adminLogout(@RequestParam("email") String email) {

        if (regService.returnAdminStatus() == 1) {

            regService.adminLogout(email);
            return new ResponseEntity<>("Successfully log out as admin", HttpStatus.OK);

        }

        return new ResponseEntity<>("You are trying to logout without login...login as admin first", HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/alumnusList")
    public ResponseEntity<?> alumnusList(@RequestParam("email") String email) {


        if (regService.trackFindByEmail(email).getStatus() == 1)
            return ResponseEntity.ok("Alumnus");
        else if (regService.trackFindByEmail(email).getStatus() == 2)
            return ResponseEntity.ok("Password is incorrect...try properly...");

        return ResponseEntity.status(401).body("Sorry..Access denied...plz log in first");
    }

    public void saveImagesOfPendingRequest(List<Register> accounts) {

        String accountFolder = "C:\\Users\\Shahriar\\Desktop\\ImageTemp\\Resumes&Images\\Images\\PendingAccounts\\";

        for (Register account : accounts) {

            byte[] image = account.getProfilePic();

            if (image != null) {

                String filePath = accountFolder + account.getName() + " profile.jpg";

                try (FileOutputStream fos = new FileOutputStream(filePath)) {

                    fos.write(image);
                }
                catch (IOException e) {
                    System.out.println("Error writing image file: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if(account.getStudentIdCardPic()!=null){
               saveIdentity(account.getStudentIdCardPic(), accountFolder, account);
            }
            else if(account.getPVCPic()!=null){
                saveIdentity(account.getPVCPic(), accountFolder, account);
            }
        }
    }
    public void saveIdentity(byte[] identity, String accountFolder, Register account){

        String filePath = accountFolder + account.getName() + " identity.jpg";

        try (FileOutputStream fos = new FileOutputStream(filePath)) {

            fos.write(identity);
        }
        catch (IOException e) {
            System.out.println("Error writing image file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


