package com.shahriar.CSE_Alumni_backend.Controllers;


import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Entities.UserStatus;
import com.shahriar.CSE_Alumni_backend.Services.RegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.DataFormatException;

//@RequestMapping("api/v1")

@RestController
public class RegController {

    @Autowired
    private RegService regService;


    //@PostMapping("/Unused")
    //public ResponseEntity<?> continueWithCUETGmail (@RequestParam("CUETGmail") String gmail)  throws Exception{


    /**
     * 588087058292-m98l2fld9edbpvu1j6e08ieclr7a727n.apps.googleusercontent.com
     * <p>
     * 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
     */



        /*System.out.println("Everything is ok...");

            String otp = generateOTP();

            regService.sendOTP(gmail, otp);

            return new ResponseEntity<>("OTP has been sent to your email for verification.", HttpStatus.OK);*/
    //}

    /*private boolean isUniversityEmail(String email) {

        return email.matches("^u\\d{7}@student\\.cuet\\.ac\\.bd$");
    }


    private String generateOTP() {

        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @GetMapping("/login/oauth2")
    public String handleGoogleOAuthCallback(@RequestParam("code") String authorizationCode) {
        // Process the authorization code, exchange for tokens, and fetch user details
        TokenDto userInfo = regService.getUserInfo(authorizationCode);
        System.out.println("OKKKKKKKKKKKKKKK");
        return "Account is created successfully....";


        // Resource owner Like (Google, linkedin)
        // Client who request for log in
        // Auth server (Google linkedin server)
        // Resource server
    }*/


      /*  @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody RegistrationDTO registrationDTO) {

            System.out.println("Student ID: " + registrationDTO.getStudentId());
            System.out.println("Full Name: " + registrationDTO.getFullName());
            System.out.println("Department ID: " + registrationDTO.getDepartmentId());
            System.out.println("_token: " + registrationDTO.get_token());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration successful");
            return ResponseEntity.ok(response);
        }*/


    //Public API's
    @PostMapping("/public/requestForAccount")
    public ResponseEntity<?> requestForCreatingAccToAdmin(
            @RequestParam("userName") String name,
            @RequestParam(value = "userEmail") String email,
            @RequestParam("passwordOfUser") String password,
            @RequestParam("profilePicOfUser") MultipartFile profilePic,

            @RequestParam(value = "studentId", required = false) String studentId,
            @RequestParam(value = "YearOfGraduation", required = false) String graduationYear,

            @RequestParam(value = "identityPic") MultipartFile identity

    )
            throws IOException {



      /*  boolean studentInfoProvided = studentId != null && studentIdCard != null;
        boolean graduationInfoProvided = graduationYear != null && pvc != null;

        if (!studentInfoProvided && !graduationInfoProvided) {
            return new ResponseEntity<>("Either student ID card and student ID or graduation year and PVC must be provided.", HttpStatus.BAD_REQUEST);
        }*/

        if (!regService.isAccountExistsAlready(email)) {
            String response = regService.requestForAcc(name, email, password, profilePic,
                    studentId, identity,
                    graduationYear,
                    UserStatus.PENDING
            );

            return new ResponseEntity<>(response + " and waiting for approval", HttpStatus.OK);
        }

        return new ResponseEntity<>("Account is already exists with this email...", HttpStatus.OK);
    }


    @PostMapping("/public/UserLogin")
    public ResponseEntity<?> login(@RequestParam("email") String email,
                                   @RequestParam("password") String password
    ) {

        int authentication = regService.login(email, password);

        //System.out.println(email + " " + password + " " + authentication);

        if (authentication == 3) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404 Not Found for "No account with this email"
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Sorry!!! There is no account with this email");

        } else if (authentication == 0) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403 Forbidden for "Account not approved"
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Your account is not approved...After approval you can login...");
        } else if (authentication == 2) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized for "Incorrect password"
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Password is incorrect...try again");
        } else if (authentication == 1) {

            String token = generateToken(email);

            regService.saveToken(email, token, LocalDateTime.now().plusMinutes(3));

            return ResponseEntity
                    .status(HttpStatus.OK) // 200 OK for "User login successful"
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("User login successful");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 200 OK for "User login successful"
                .contentType(MediaType.APPLICATION_JSON)
                .body("Something wen wrong....");

    }

    public String generateToken(String email) {

        String token = UUID.randomUUID().toString();

        // You can customize the format of the timeout if needed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Concatenate the email and current time to make the token unique
        token += "_" + email + "_" + LocalDateTime.now().plusMinutes(10).format(formatter);

        return token;
    }


    @GetMapping("/public/fetch/{email}")
    public ResponseEntity<?> fetchImage(@PathVariable String email) throws DataFormatException {

        Register fetchedData = regService.fetchRecord(email);

        if (fetchedData.equals(new Register()))
            return new ResponseEntity<>("Sorry...no records exists with this email or you aren't logged in...", HttpStatus.BAD_REQUEST);

        saveImageOfSpecificAcc(fetchedData);

        if (fetchedData.getUserStatus().equals(UserStatus.APPROVED))
            return new ResponseEntity<>(fetchedData, HttpStatus.OK);


        return new ResponseEntity<>("Your request is pending currently", HttpStatus.OK);
    }


    @GetMapping("/public/fetch/allRegisteredAcc")
    public ResponseEntity<?> allRegisteredStudents() {

        List<Register> registeredAcc = regService.getAllRegisteredStudents();

        if (registeredAcc == null)
            return new ResponseEntity<>("No registered accounts are available", HttpStatus.OK);

        saveRegisteredAccounts(registeredAcc);

        return new ResponseEntity<>(registeredAcc, HttpStatus.OK);
    }


    // private API's


//    @GetMapping("/UserLogout")
//    public ResponseEntity<?> logout() {
//
//        regService.logout();
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body("User logged out");
//    }


//    @PostMapping("/updateAcc")
//    public ResponseEntity<?> updateAccount(
//            @RequestParam(value = "Name",required = false) String name,
//            @RequestParam(value = "email",required = false) String email,
//            @RequestParam(value = "password", required = false) String password,
//            @RequestParam(value = "profile",required = false) MultipartFile profilePic,
//
//            @RequestParam(value="studentId" , required = false) String studentId,
//            @RequestParam(value = "graduationYear",required = false) String graduationYear,
//
//            @RequestParam(value = "identity" ,required = false) MultipartFile identity
//    ) {
//
//        String result = regService.updateAccount(name,email, password, profilePic,
//                identity,studentId,
//                graduationYear);
//
//        if (result.equals("No Account found"))
//            return new ResponseEntity<>("No account exists with this email...", HttpStatus.BAD_REQUEST);
//
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
//
//
//    @DeleteMapping("/deleteAcc")
//    public ResponseEntity<?> deleteAccount() {
//
//
//        String result = regService.deleteAccount();
//
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }











    public void saveImageOfSpecificAcc(Register account) {

        String accountFolder = "C:\\Users\\Shahriar\\Desktop\\ImageTemp\\Resumes&Images\\Images\\SpecificAccounts\\";

        byte[] image = account.getProfilePic();

        if (image != null) {

            String filePath = accountFolder + account.getName() + ".jpg";

            try (FileOutputStream fos = new FileOutputStream(filePath)) {

                fos.write(image);
            } catch (IOException e) {
                System.out.println("Error writing image file: " + e.getMessage());
                e.printStackTrace();
            }

        }

        if (account.getIdentity() != null) {
            saveIdentityList(account.getIdentity(), accountFolder, account);
        }
    }

    public void saveIdentityList(byte[] identity, String accountFolder, Register account) {

        String filePath = accountFolder + account.getName() + " identity.jpg";

        try (FileOutputStream fos = new FileOutputStream(filePath)) {

            fos.write(identity);
        } catch (IOException e) {
            System.out.println("Error writing image file: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void saveRegisteredAccounts(List<Register> accounts) {

        String accountFolder = "C:\\Users\\Shahriar\\Desktop\\ImageTemp\\Resumes&Images\\Images\\RegisteredAccounts\\";

        for (Register account : accounts) {

            byte[] image = account.getProfilePic();

            if (image != null) {

                String filePath = accountFolder + account.getName() + " profile.jpg";

                try (FileOutputStream fos = new FileOutputStream(filePath)) {

                    fos.write(image);
                } catch (IOException e) {
                    System.out.println("Error writing image file: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (account.getIdentity() != null) {
                saveIdentity(account.getIdentity(), accountFolder, account);
            }
        }
    }

    public void saveIdentity(byte[] identity, String accountFolder, Register account) {

        String filePath = accountFolder + account.getName() + " identity.jpg";

        try (FileOutputStream fos = new FileOutputStream(filePath)) {

            fos.write(identity);
        } catch (IOException e) {
            System.out.println("Error writing image file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
