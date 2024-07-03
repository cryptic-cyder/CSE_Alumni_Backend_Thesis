/*  boolean studentInfoProvided = studentId != null && studentIdCard != null;
       boolean graduationInfoProvided = graduationYear != null && pvc != null;

       if (!studentInfoProvided && !graduationInfoProvided) {
           return new ResponseEntity<>("Either student ID card and student ID or graduation year and PVC must be provided.", HttpStatus.BAD_REQUEST);
       }*/


package com.shahriar.CSE_Alumni_backend.Controllers;

import com.shahriar.CSE_Alumni_backend.Entities.*;
import com.shahriar.CSE_Alumni_backend.Repos.RegRepoIF;
import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import com.shahriar.CSE_Alumni_backend.Services.RegService;
import com.shahriar.CSE_Alumni_backend.Services.TokenValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.DataFormatException;

@RestController
public class RegController {

    @Autowired
    private RegService regService;

    @Autowired
    private RegRepoIF regRepoIF;


    @PostMapping("/SearchMembers")
    public ResponseEntity<?> search(@RequestBody Map<String, String> payload
                                    ) throws IOException {


        String query = payload.get("searchContent");
        System.out.println("Searched query is : " + query);

        List<Register> searchResults = regService.performSearch(query);

        //System.out.println(searchResults.size());

        if (searchResults == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }


    @PostMapping("/public/ChangePassword")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String auth,
                                            @RequestParam("userPassword") String password
    ) {

        String token = auth.replace("Bearer", "");

        String[] parts = token.split("_");
        Long id = Long.parseLong(parts[3]);

        //Optional<Token> tokenFromDB = tokenInterface.findById(id);
        String emailFromBrowserToken = new TokenValidation().extractEmailFromToken(token);

        Optional<Register> record = regRepoIF.findByEmail(emailFromBrowserToken);
        Register record1 = record.get();

        record1.setPassword(password);
        regRepoIF.save(record1);

        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
    }

    @PostMapping("/public/forgetPassword")
    public ResponseEntity<?> sendEmail(@RequestParam("userEmail") String email) {

        String token = generateToken(email);

        String resetLink = "http://localhost:3000/Password_Recovery";

        String emailBody = "Dear User,\n\n"
                + "You have requested to reset your password. Please click the following link to reset your password:\n"
                + resetLink + "\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Best regards,\nFrom Application Team";


        regService.sendEmail(email, "Password Reset Request", emailBody);


        Token token1 = regService.saveToken(email, token, LocalDateTime.now().plusMinutes(40));

        Token tokenForId = tokenInterface.findByToken(token);
        String tokenId = tokenForId.getId().toString();

        token1.setToken(token1.getToken() + "_" + tokenId);
        tokenInterface.save(token1);

        LoginResponse response = new LoginResponse();

        response.setMessage("Sent email properly");
        response.setToken(token + "_" + tokenId);


        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/public/tokenValidation")
    public ResponseEntity<?> tokenValidation(@RequestBody TokenDto authorizationHeader) {

        if (new TokenValidation().isTokenValid(authorizationHeader.getToken()))
            return ResponseEntity.status(HttpStatus.OK).body("Token is valid...");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid...");
    }


    //Public API's
    @PostMapping("/public/requestForAccount")
    public ResponseEntity<?> requestForCreatingAccToAdmin(
            @RequestParam("userName") String name,
            @RequestParam(value = "userEmail", required = false) String email,
            @RequestParam("passwordOfUser") String password,

            @RequestParam(value = "identityPic") MultipartFile identity

    )
            throws IOException {


        if (!regService.isAccountExistsAlready(email)) {
            String response = regService.requestForAcc(name, email, password, identity,
                    UserStatus.PENDING
            );

            return new ResponseEntity<>(response + " and waiting for approval", HttpStatus.OK);
        }

        return new ResponseEntity<>("Account is already exists with this email...", HttpStatus.FOUND);
    }

    @Autowired
    private TokenInterface tokenInterface;

    @PostMapping("/public/UserLogin")
    public ResponseEntity<?> login(@RequestParam("userEmail") String email,
                                   @RequestParam("userPassword") String password
    ) {

        int authentication = regService.login(email, password);

        System.out.println(authentication);

        if (authentication == 3) {

            LoginResponse response = new LoginResponse();

            response.setMessage("Account is waiting for approval...");
            response.setToken(null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } else if (authentication == 0) {

            LoginResponse response = new LoginResponse();

            response.setMessage("Sorry...No such account exists");
            response.setToken(null);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } else if (authentication == 2) {
            LoginResponse response = new LoginResponse();

            response.setMessage("Password is incorrect");
            response.setToken(null);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else if (authentication == 1) {

            String token = generateToken(email);

            Token token1 = regService.saveToken(email, token, LocalDateTime.now().plusMinutes(40));

            Token tokenForId = tokenInterface.findByToken(token);
            String tokenId = tokenForId.getId().toString();

            token1.setToken(token1.getToken() + "_" + tokenId);
            tokenInterface.save(token1);

            LoginResponse response = new LoginResponse();

            response.setMessage("User login successful");
            response.setToken(token + "_" + tokenId);


            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        LoginResponse response = new LoginResponse();

        response.setMessage("Bad request.....");
        response.setToken(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public String generateToken(String email) {

        String token = UUID.randomUUID().toString();

        // You can customize the format of the timeout if needed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Concatenate the email and current time to make the token unique
        token += "_" + email + "_" + LocalDateTime.now().plusMinutes(40).format(formatter);

        return token;
    }


    @PostMapping("/fetch")
    public ResponseEntity<?> fetchImage(@RequestBody TokenDto authorizationHeader) throws DataFormatException {

        //System.out.println("Token is : "+authorizationHeader.getToken());

        String token = authorizationHeader.getToken();

        if (new TokenValidation().isTokenValid(token)) {

            String[] parts = token.split("_");
            Long id = Long.parseLong(parts[3]);

            Optional<Token> tokenFromDB = tokenInterface.findById(id);
            String emailFromTokenDB = tokenFromDB.get().getEmail();
            String emailFromBrowserToken = new TokenValidation().extractEmailFromToken(token);


            if (emailFromBrowserToken.equals(emailFromTokenDB)) {

                Register fetchedData = regService.fetchRecord(authorizationHeader.getToken());

                //saveImageOfSpecificAcc(fetchedData);

                return new ResponseEntity<>(fetchedData, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/fetchOthers/{email}")
    public ResponseEntity<?> fetchOther(@PathVariable("email") String email) throws DataFormatException {

        //System.out.println("\n\nPost person's email is : "+email+"\n\n");
        Register fetchedData = regService.fetchOthersRecord(email);

        //System.out.println(fetchedData.getEmail()+" "+fetchedData.getName()+" "+fetchedData.getPassword());

        return new ResponseEntity<>(fetchedData, HttpStatus.OK);
    }


    @GetMapping("/public/fetch/allRegisteredAcc")
    public ResponseEntity<?> allRegisteredStudents() {

        List<Register> registeredAcc = regService.getAllRegisteredStudents();

        if (registeredAcc == null)
            return ResponseEntity.status(HttpStatus.OK).body(null);

        //saveRegisteredAccounts(registeredAcc);

        return ResponseEntity.status(HttpStatus.OK).body(registeredAcc);
    }


    // private API's


    @PostMapping("/UserLogout")
    public ResponseEntity<?> logout(@RequestBody TokenDto auth) {

        String token = auth.getToken();

        if (new TokenValidation().isTokenValid(token)) {

            String[] parts = token.split("_");
            Long id = Long.parseLong(parts[3]);

            Optional<Token> tokenFromDB = tokenInterface.findById(id);
            String emailFromTokenDB = tokenFromDB.get().getEmail();
            String emailFromBrowserToken = new TokenValidation().extractEmailFromToken(token);


            if (emailFromBrowserToken.equals(emailFromTokenDB)) {
                regService.logout(auth.getToken());

                LoginResponse response = new LoginResponse();
                response.setMessage("User logged out...");
                response.setToken(null);

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }

        LoginResponse response = new LoginResponse();
        response.setMessage("Token is expired...");
        response.setToken(null);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @PostMapping("/updateAcc")
    public ResponseEntity<?> updateAccount(
            @RequestParam(value = "userName", required = false) String name,
            @RequestParam(value = "userEmail", required = false) String email,
            @RequestParam(value = "passwordOfUser", required = false) String password,
            @RequestParam(value = "profStatus", required = false) String profStatus,

            @RequestParam(value = "profilePicOfUser", required = false) MultipartFile profilePic,
            @RequestParam(value = "identityPic", required = false) MultipartFile identity,

            @RequestParam(value = "studentId", required = false) String studentId,
            @RequestParam(value = "YearOfGraduation", required = false) String graduationYear,

            @RequestHeader("Authorization") String auth

    ) {

        if (profStatus.isBlank())
            System.out.println("\n\nProfessional Status is empty.\n\n");

        String token = auth.replace("Bearer", "");

        if (new TokenValidation().isTokenValid(token)) {

            String[] parts = token.split("_");
            Long id = Long.parseLong(parts[3]);

            Optional<Token> tokenFromDB = tokenInterface.findById(id);
            String emailFromTokenDB = tokenFromDB.get().getEmail();
            String emailFromBrowserToken = new TokenValidation().extractEmailFromToken(token);


            if (emailFromBrowserToken.equals(emailFromTokenDB)) {
                String result = regService.updateAccount(name, email, password, profilePic,
                        identity, studentId,
                        graduationYear, profStatus, token);

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

    }


    @PostMapping("/deleteAcc")
    public ResponseEntity<?> deleteAccount(@RequestBody TokenDto auth) {


        String token = auth.getToken();

        if (new TokenValidation().isTokenValid(auth.getToken())) {

            String[] parts = token.split("_");
            Long id = Long.parseLong(parts[3]);

            Optional<Token> tokenFromDB = tokenInterface.findById(id);
            String emailFromTokenDB = tokenFromDB.get().getEmail();
            String emailFromBrowserToken = new TokenValidation().extractEmailFromToken(token);


            if (emailFromBrowserToken.equals(emailFromTokenDB)) {
                String result = regService.deleteAccount(auth.getToken());

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }


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




