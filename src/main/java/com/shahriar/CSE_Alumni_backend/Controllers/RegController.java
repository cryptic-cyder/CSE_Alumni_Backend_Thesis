package com.shahriar.CSE_Alumni_backend.Controllers;

import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Entities.UserDTO;
import com.shahriar.CSE_Alumni_backend.Entities.UserStatus;
import com.shahriar.CSE_Alumni_backend.Services.RegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.zip.DataFormatException;

@RequestMapping("api/v1")
@RestController
public class RegController {

    @Autowired
    private RegService regService;

    @PostMapping("/requestForAccount")
    public ResponseEntity<?> requestForCreatingAccToAdmin(
                                        @RequestParam("name") String name,
                                        @RequestParam("email") String email,
                                        @RequestParam("password") String password,
                                        @RequestParam("role") String role,
                                        @RequestParam("profilePic") MultipartFile profilePic,

                                        @RequestParam(value = "studentId", required = false) String studentId,
                                        @RequestParam(value = "studentIdCard", required = false) MultipartFile studentIdCard,

                                        @RequestParam(value = "graduationYear", required = false) String graduationYear,
                                        @RequestParam(value = "pvc", required = false) MultipartFile pvc
                                        )
                      throws IOException {

        if(!regService.isAccountExistsAlready(email)){
            String response = regService.requestForAcc( name, email, password, role, profilePic,
                    studentId, studentIdCard,
                    graduationYear, pvc,
                    UserStatus.PENDING
            );

            return new ResponseEntity<>(response+" and waiting for approval", HttpStatus.OK);
        }

        return new ResponseEntity<>("Account is already exists with this email...", HttpStatus.OK);
    }


    @PostMapping("/updateAcc/{email}")
    public ResponseEntity<?> updateAccount(@PathVariable String email,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String password,
                                                @RequestParam(required = false) String role,
                                                @RequestParam(required = false) MultipartFile profilePic,
                                                @RequestParam(required = false) String studentId,
                                                @RequestParam(required = false) MultipartFile studentIdCard,
                                                @RequestParam(required = false) String graduationYear,
                                                @RequestParam(required = false) MultipartFile pvc
                                                ) {
        if(regService.returnUserStatus(email)!=1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);

        String result = regService.updateAccount(email, name, password, role, profilePic,
                studentId, studentIdCard, graduationYear, pvc);

        if(result.equals("No Account found"))
            return new ResponseEntity<>("No account exists with this email...", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @DeleteMapping("/deleteAcc/{email}")
    public ResponseEntity<?> updateAccount(@PathVariable String email) {

        if(regService.returnUserStatus(email)!=1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);

        String result = regService.deleteAccount(email);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    @GetMapping("/fetch/{email}")
    public ResponseEntity<?> fetchImage(@PathVariable String email) throws DataFormatException {

        Register fetchedData = regService.fetchRecord(email);

        if (fetchedData.equals(new Register()))
            return new ResponseEntity<>("Sorry...no records exists with this email or you aren't logged in...", HttpStatus.BAD_REQUEST);

        if(fetchedData.getUserStatus().equals(UserStatus.APPROVED))
            return new ResponseEntity<>(fetchedData, HttpStatus.OK);
        else
            return new ResponseEntity<>("Your request is pending currently", HttpStatus.OK);

    }

        // Retrieve the image bytes directly
        /*Register fetchedData = regService.fetchRecord(studentID);
        byte[] imageBytes = fetchedData.getImageData();

        // Set the appropriate headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Adjust based on your image format

        // Return the image bytes with headers
        return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);*/


    @GetMapping("/fetch/allRegisteredBasic")
    public ResponseEntity<?> allRegisteredBasic(){

        List<UserDTO> allRegisteredBasic = regService.getAllAcceptedAccBasic();

        if(allRegisteredBasic==null)
            return new ResponseEntity<>("No registered account", HttpStatus.OK);

        return new ResponseEntity<>(allRegisteredBasic, HttpStatus.OK);
    }


    @GetMapping("/fetch/allRegisteredAcc")
    public ResponseEntity<?> allRegisteredStudents(){

        List<Register> registeredAcc = regService.getAllRegisteredStudents();

        if(registeredAcc==null)
            return new ResponseEntity<>("No registered accounts are available", HttpStatus.OK);

        return new ResponseEntity<>(registeredAcc, HttpStatus.OK);
    }


    @PostMapping("/UserLogin")
    public ResponseEntity login( @RequestParam("email") String email,
                       @RequestParam("password") String password
                     ){

        int authentication = regService.login(email, password);

        if(authentication==0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Sorry!!! There is no account with this email");

        else if(authentication==1)
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("User login successful");

        else if(authentication==3)
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Your account is not approved...After approval you can login...");

        return ResponseEntity.status(401).body("Password is incorrect ...try again");
    }


    @GetMapping("/UserLogout")
    public ResponseEntity<?> logout(@RequestParam("email") String email){

        regService.logout(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User logged out");
    }

}
