package com.shahriar.CSE_Alumni_backend.Controllers;


import com.shahriar.CSE_Alumni_backend.Entities.AdminRequest;
import com.shahriar.CSE_Alumni_backend.Entities.LoginResponse;
import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Entities.TokenDto;
import com.shahriar.CSE_Alumni_backend.Services.RegService;
import com.shahriar.CSE_Alumni_backend.Services.TokenValidation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@RestController
public class Admin {

    @Autowired
    private RegService regService;




    @PostMapping("/public/adminLogin")
    public ResponseEntity<?> adminLogin(@RequestParam("email") String adminEmail,
                                        @RequestParam("password") String adminPassword) {
        System.out.println("Hit api");
        //System.out.println("Trying to push github");

//        String adminEmail = adminRequest.getAdminEmail();
//        String adminPassword = adminRequest.getAdminPassword();

        if (regService.adminLogin(adminEmail, adminPassword)) {

            String token = new RegController().generateToken(adminEmail);

            regService.saveToken(adminEmail, token, LocalDateTime.now().plusMinutes(40));

            LoginResponse response = new LoginResponse();
            response.setMessage("Admin login successful");
            response.setToken(token);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        LoginResponse errorResponse = new LoginResponse();
        errorResponse.setMessage("Email or Password is incorrect. Access denied. Please try again.");

       // System.out.println(errorResponse);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }


    // Handler for OPTIONS requests
//    @RequestMapping(value = "/public/adminLogin", method = RequestMethod.OPTIONS)
//    public ResponseEntity<?> handleOptionsRequest() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Access-Control-Allow-Origin", "http://localhost:3000");
//        headers.add("Access-Control-Allow-Methods", "POST");
//        headers.add("Access-Control-Allow-Headers", "Content-Type");
//        headers.add("Access-Control-Max-Age", "3600");
//        return ResponseEntity.ok().headers(headers).build();
//    }


    @PostMapping("/pendingRequests")
    public ResponseEntity<?> getPendingRegistrations(@RequestBody TokenDto authorizationHeader) {


       // System.out.println("Token is : "+authorizationHeader.getToken());

        if(new TokenValidation().isTokenValid(authorizationHeader.getToken())){
            //System.out.println("Token is validated...");
            List<Register> pendingUsers = regService.getPendingUsers();
                //System.out.println(pendingUsers.size());
                if (pendingUsers == null) {
                    return new ResponseEntity<>("No requests pending", HttpStatus.OK);
                }

                saveImagesOfPendingRequest(pendingUsers);

                return new ResponseEntity<>(pendingUsers, HttpStatus.OK);
        }

        return new ResponseEntity<>("Access denied...You are not logged in or token expired", HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/approveAcc/{email}")
    public ResponseEntity<String> approveAccount(@PathVariable String email, @RequestBody TokenDto auth) {

        if(new TokenValidation().isTokenValid(auth.getToken())){

            if (regService.returnAdminStatus() == 1) {
                Register register = regService.approveRegistration(email);

            }

            return new ResponseEntity<>("Registration approved", HttpStatus.OK);
        }

        return new ResponseEntity<>("Access denied...login as admin first", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/rejectAcc/{email}")
    public ResponseEntity<String> rejectAccount(@PathVariable String email, @RequestBody TokenDto auth) {

        if(new TokenValidation().isTokenValid(auth.getToken())) {

            if (regService.returnAdminStatus() == 1) {

                Register register = regService.rejectRegistration(email);

                return new ResponseEntity<>("Sorry!! You request has been rejected", HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Access denied...login as admin first", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/adminLogout")
    public ResponseEntity<String> adminLogout(@RequestBody TokenDto auth) {

        if(new TokenValidation().isTokenValid(auth.getToken())){

            if (regService.returnAdminStatus() == 1) {

                regService.adminLogout();
                return new ResponseEntity<>("Successfully log out as admin", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("You are trying to logout without login...login as admin first", HttpStatus.BAD_REQUEST);
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

            if(account.getIdentity()!=null){
               saveIdentity(account.getIdentity(), accountFolder, account);
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


