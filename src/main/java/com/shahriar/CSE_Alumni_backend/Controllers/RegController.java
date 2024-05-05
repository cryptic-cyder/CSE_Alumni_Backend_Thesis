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
import java.util.*;
import java.util.zip.DataFormatException;

//@RequestMapping("api/v1")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RegController {

    @Autowired
    private RegService regService;

    //@PostMapping("/Unused")
    //public ResponseEntity<?> continueWithCUETGmail (@RequestParam("CUETGmail") String gmail)  throws Exception{


        /**

         588087058292-m98l2fld9edbpvu1j6e08ieclr7a727n.apps.googleusercontent.com

         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

         * */



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
        UserDTO userInfo = regService.getUserInfo(authorizationCode);
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


    @PostMapping("/public/requestForAccount")
    public ResponseEntity<?> requestForCreatingAccToAdmin(
                                        @RequestParam("userName") String name,
                                        @RequestParam("userEmail") String email,
                                        @RequestParam("passwordOfUser") String password,
                                        @RequestParam("roleOfUser") String role,
                                        @RequestParam("profilePicOfUser") MultipartFile profilePic,

                                        @RequestParam(value = "studentIdCard", required = false) String studentId,
                                        @RequestParam(value = "IdCardPic", required = false) MultipartFile studentIdCard,

                                        @RequestParam(value = "YearOfGraduation", required = false) String graduationYear,
                                        @RequestParam(value = "pvc", required = false) MultipartFile pvc
                                        )
                      throws IOException {



      /*  boolean studentInfoProvided = studentId != null && studentIdCard != null;
        boolean graduationInfoProvided = graduationYear != null && pvc != null;

        if (!studentInfoProvided && !graduationInfoProvided) {
            return new ResponseEntity<>("Either student ID card and student ID or graduation year and PVC must be provided.", HttpStatus.BAD_REQUEST);
        }*/

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

    @PostMapping("/public/UserLogin")
    public ResponseEntity<?> login( @RequestParam("email") String email,
                                    @RequestParam("password") String password
    ) {

        int authentication = regService.login(email, password);

        System.out.println(email + " " + password + " " + authentication);

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
        } else if(authentication==1){
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



    @GetMapping("/public/fetch/{email}")
    public ResponseEntity<?> fetchImage(@PathVariable String email) throws DataFormatException {

        Register fetchedData = regService.fetchRecord(email);

        if (fetchedData.equals(new Register()))
            return new ResponseEntity<>("Sorry...no records exists with this email or you aren't logged in...", HttpStatus.BAD_REQUEST);

        saveImageOfSpecificAcc(fetchedData);

        if(fetchedData.getUserStatus().equals(UserStatus.APPROVED))
            return new ResponseEntity<>(fetchedData, HttpStatus.OK);


        return new ResponseEntity<>("Your request is pending currently", HttpStatus.OK);
    }



    @GetMapping("/public/fetch/allRegisteredAcc")
    public ResponseEntity<?> allRegisteredStudents(){

        List<Register> registeredAcc = regService.getAllRegisteredStudents();

        if(registeredAcc==null)
            return new ResponseEntity<>("No registered accounts are available", HttpStatus.OK);

        saveRegisteredAccounts(registeredAcc);

        return new ResponseEntity<>(registeredAcc, HttpStatus.OK);
    }




    @GetMapping("/UserLogout")
    public ResponseEntity<?> logout(@RequestParam("email") String email){

        regService.logout(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User logged out");
    }


    public void saveImageOfSpecificAcc(Register account) {

        String accountFolder = "C:\\Users\\Shahriar\\Desktop\\ImageTemp\\Resumes&Images\\Images\\SpecificAccounts\\";

            byte[] image = account.getProfilePic();

            if (image != null) {

                String filePath = accountFolder + account.getName() + ".jpg";

                try (FileOutputStream fos = new FileOutputStream(filePath)) {

                    fos.write(image);
                }
                catch (IOException e) {
                    System.out.println("Error writing image file: " + e.getMessage());
                    e.printStackTrace();
                }

            }

        if(account.getStudentIdCardPic()!=null){
            saveIdentityList(account.getStudentIdCardPic(), accountFolder, account);
        }
        else if(account.getPVCPic()!=null){
            saveIdentityList(account.getPVCPic(), accountFolder, account);
        }
    }

    public void saveIdentityList(byte[] identity, String accountFolder, Register account){

        String filePath = accountFolder + account.getName() + " identity.jpg";

        try (FileOutputStream fos = new FileOutputStream(filePath)) {

            fos.write(identity);
        }
        catch (IOException e) {
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
