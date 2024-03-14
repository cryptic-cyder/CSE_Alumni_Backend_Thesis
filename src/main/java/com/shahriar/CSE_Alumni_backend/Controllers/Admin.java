package com.shahriar.CSE_Alumni_backend.Controllers;

import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Services.RegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Admin {

    @Autowired
    private RegService regService;


    @PostMapping("/adminLogin")
    public ResponseEntity<?> adminLogin(@RequestParam("email") String email,
                                        @RequestParam("password") String password){

        if(regService.adminLogin(email, password)){

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Admin login successful...");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Password incorrect...Access denied...Please try again");

    }

    @GetMapping("/pendingRequests")
    public ResponseEntity<?> getPendingRegistrations() {

        if(regService.returnAdminStatus()==1){
            List<Register> pendingUsers = regService.getPendingUsers();

            if(pendingUsers==null){
                return new ResponseEntity<>("No requests pending", HttpStatus.OK);
            }

            return new ResponseEntity<>(pendingUsers, HttpStatus.OK);
        }

        return new ResponseEntity<>("Access denied...login as admin first", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/approveAcc/{email}")
    public ResponseEntity<String> approveAccount(@PathVariable String email) {

        if(regService.returnAdminStatus()==1){
            Register register = regService.approveRegistration(email);

            if(register.equals(new Register()))
                return new ResponseEntity<>("Sorry no such account exists", HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>("Registration approved", HttpStatus.OK);
        }

        return new ResponseEntity<>("Access denied...login as admin first", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/rejectAcc/{email}")
    public ResponseEntity<String> rejectAccount(@PathVariable String email) {

        if(regService.returnAdminStatus()==1){

            Register register = regService.rejectRegistration(email);

            if(register.equals(new Register()))
                return new ResponseEntity<>("Sorry no such account exists", HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>("Sorry!! You request has been rejected", HttpStatus.OK);
        }

        return new ResponseEntity<>("Access denied...login as admin first", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/adminLogout")
    public ResponseEntity<String> adminLogout(@RequestParam("email") String email) {

        if(regService.returnAdminStatus()==1){

            regService.adminLogout(email);
            return new ResponseEntity<>("Successfully log out as admin", HttpStatus.OK);

        }

        return new ResponseEntity<>("You are trying to logout without login...login as admin first", HttpStatus.BAD_REQUEST);
    }

}
