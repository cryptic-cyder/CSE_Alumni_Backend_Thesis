package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.*;
import com.shahriar.CSE_Alumni_backend.Repos.RegRepoIF;
import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import com.shahriar.CSE_Alumni_backend.Repos.UserDTOInterface;
import com.shahriar.CSE_Alumni_backend.Repos.UsertrackRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.*;

@Service
public class RegService {

    @Autowired
    private RegRepoIF regRepoIF;

    @Autowired
    private UserDTOInterface userDTOInterface;

    @Autowired
    private TokenInterface tokenInterface;

    @Autowired
    private RequestInterceptorService requestInterceptorService;

    public void saveToken(String email, String token, LocalDateTime timeout) {

        Token tokenEntity = new Token();
        tokenEntity.setEmail(email);
        tokenEntity.setToken(token);
        tokenEntity.setTimeOut(timeout);

        tokenInterface.save(tokenEntity);
    }

    /*public void sendOTP(String recipientEmail, String otp) throws MessagingException {

        // Configuration setting for email sending
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");


        /*
        *  with this code, your application can establish a connection to the email server and send emails using the provided credentials. It's like saying, "Hey, email server,
        * here's my email address and password. I'm allowed to send emails from this account, so please let me do that."
         *so it is like verifying my application to allow to send email by using server like gmail server ???
Exactly! You've got it. The getPasswordAuthentication() method is like your application presenting its
* credentials (email address and password) to the email server (e.g., Gmail server) for verification. This
* verification process ensures that your application is authorized to send emails on behalf of the specified
* email account. Once the credentials are verified, your application is granted permission to send emails through
* the email server. So, it's a crucial
*  step in the process of establishing trust and authorization between your application and the email server.
         *
         *  */
        /*Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("shahriarbadhon778@gmail.com", "46180312");
            }
        });

        /*Imagine you have a template for writing letters. This template includes things like the sender's address,
        the recipient's address, the subject, and the body of the letter. Whenever you want to write a new letter,
         you start with this template.In Java, creating an email message is like creating a template for an email.
          The Message object represents this template. But before you can use this template, you need to set it up
          with some basic information, like the email server details and authentication. That's what the Session object
          helps with. It's like setting up the letter-writing environment.So, when you write new MimeMessage(session),
           you're essentially saying, "Java, I want to create a new template for an email message. And I want to use the
           settings we prepared earlier (like the email server details) to set up this template."
          Once you have this Message template, you can then fill in the specific details for each email you want to send,
          like who it's from, who it's to, the subject, and the body of the email. But this line of code just creates the
          blank template, ready for you to fill in the specific details later.*/

        /*Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress("shahriarbadhon778@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("OTP Verification");
        message.setText("Your OTP for account verification is: " + otp);

        Transport.send(message);



        // Storing OTP

        /*UserDTO user = new UserDTO();

        user.setGmail(recipientEmail);
        user.setOTP(otp);

        userDTOInterface.save(user);*/
    //}

    /*public void verifyOTP(String gmail, String OTP){

        UserDTO userDTO = userDTOInterface.findByGmail(gmail);

        if(userDTO.getOTP().equals(OTP)){
            // Create Account

        }
    }*/

/*
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    private final RestTemplate restTemplate = new RestTemplate();

    public UserDTO getUserInfo(String authorizationCode) {
        // Exchange authorization code for access token
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("google", authorizationCode);

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        GoogleUserInfo googleUserInfo = fetchUserInfoFromGoogle(accessToken);

        return convertToUserDTO(googleUserInfo);
    }

    private GoogleUserInfo fetchUserInfoFromGoogle(OAuth2AccessToken accessToken) {

        // Construct request to fetch user info from Google
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Send GET request to Google's user info endpoint
        ResponseEntity<GoogleUserInfo> responseEntity = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                requestEntity,
                GoogleUserInfo.class
        );

        // Extract user info from the response
        return responseEntity.getBody();
    }

    private UserDTO convertToUserDTO(GoogleUserInfo googleUserInfo) {

        UserDTO userDTO = new UserDTO();

        userDTO.setUserName(googleUserInfo.getName());
        userDTO.setGmail(googleUserInfo.getEmail());
        userDTO.setProfilePicture(googleUserInfo.getPicture());
        userDTO.setRole("Student");

        userDTOInterface.save(userDTO);

        return userDTO;
    }*/


    public String requestForAcc(String name, String email, String password, String role, MultipartFile profilePic,
                                String studentId, MultipartFile studentIdCard,
                                String graduationYear, MultipartFile pvc,
                                UserStatus status) {
        try {

            byte[] studentIdBytes = (studentIdCard != null) ? (studentIdCard.getBytes()) : null;
            byte[] profilePicBytes = (profilePic != null) ? (profilePic.getBytes()) : null;
            byte[] pvcBytes = (pvc != null) ? (pvc.getBytes()) : null;

            Register register = Register.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    .role(role)
                    .studentId(studentId)
                    .graduationYear(graduationYear)
                    .studentIdCardPic(studentIdBytes)
                    .profilePic(profilePicBytes)
                    .PVCPic(pvcBytes)
                    .userStatus(status)
                    .build();

            Register savedRegister = regRepoIF.save(register);

            return (savedRegister != null) ? "Account is waiting for approval: " + role :
                    "Error!!! Something went wrong... Account not created";
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            return "Error!!! Something went wrong while processing the request";
        }
    }


    public String updateAccount(String name, String password, String role, MultipartFile profilePic,
                                String studentId, MultipartFile studentIdCard,
                                String graduationYear, MultipartFile pvc
    ) {
        try {

            String email = requestInterceptorService.getTokenEmailUsedToOtherClass();

            Optional<Register> existingAccountOptional = regRepoIF.findByEmail(email);
            Register existingAccount = existingAccountOptional.get();

            if (existingAccount == null)
                return "No Account found";

            byte[] studentIdCardBytes = studentIdCard != null ? studentIdCard.getBytes() : existingAccount.getStudentIdCardPic();
            byte[] profilePicBytes = profilePic != null ? profilePic.getBytes() : existingAccount.getProfilePic();
            byte[] pvcBytes = pvc != null ? pvc.getBytes() : existingAccount.getPVCPic();

            // Update the fields with values from the request if they are not null, otherwise keep the existing values
            existingAccount.setName(name != null ? name : existingAccount.getName());
            existingAccount.setEmail(email != null ? email : existingAccount.getEmail());
            existingAccount.setPassword(password != null ? password : existingAccount.getPassword());
            existingAccount.setRole(role != null ? role : existingAccount.getRole());
            existingAccount.setStudentId(studentId != null ? studentId : existingAccount.getStudentId());
            existingAccount.setGraduationYear(graduationYear != null ? graduationYear : existingAccount.getGraduationYear());
            existingAccount.setStudentIdCardPic(studentIdCardBytes);
            existingAccount.setProfilePic(profilePicBytes);
            existingAccount.setPVCPic(pvcBytes);

            Register temp = regRepoIF.save(existingAccount);

            return (temp != null) ? "Account changes are saved: " + role :
                    "Error!!! Something went wrong...";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error!!! Something went wrong while processing the request";
        }

    }

    public String deleteAccount() {

        String email = requestInterceptorService.getTokenEmailUsedToOtherClass();

        Register existingAcc = regRepoIF.findByEmail(email).get();

        if (existingAcc == null)
            return "No such account exists";

        regRepoIF.delete(existingAcc);

        UserTrack existingUserTrack = usertrackRepo.findByEmail(email);

        if (existingUserTrack != null)
            usertrackRepo.delete(existingUserTrack);

        return "Your account is deleted successfully";
    }


    public boolean isAccountExistsAlready(String email) {

        return regRepoIF.existsByEmail(email);
    }

    public int returnAdminStatus() {

        UserTrack adminUserTrack = usertrackRepo.findByEmail("CUETCSE@admin.cuet.ac.bd");

        if (adminUserTrack == null)
            return -1;

        int adminStatus = adminUserTrack.getStatus();

        return adminStatus;
    }

    public int returnUserStatus(String email) {

        UserTrack userStatus = usertrackRepo.findByEmail(email);

        if (userStatus == null)
            return -2;

        return userStatus.getStatus();
    }

    public List<Register> getPendingUsers() {

        List<Register> existingRegister = regRepoIF.findByUserStatus(UserStatus.PENDING);

        /*for(Register register: existingRegister){
            if(register.getPVCPic()!=null)
                System.out.println(register.getEmail());
        }*/


        if (existingRegister.size() == 0)
            return new ArrayList<>();

        return existingRegister;

    }


    @Autowired
    private UsertrackRepo usertrackRepo;

    public Register approveRegistration(String email) {

        Optional<Register> user = regRepoIF.findByEmail(email);

        if (user.isPresent()) {

            Register pendingAccFromDb = user.get();
            pendingAccFromDb.setUserStatus(UserStatus.APPROVED);

            if (usertrackRepo.findByEmail(email) == null || !usertrackRepo.existsByEmail(email)) {
                UserTrack userTrack = UserTrack.builder()
                        .email(pendingAccFromDb.getEmail())
                        .status(3)
                        .build();
                usertrackRepo.save(userTrack);
            }

            return regRepoIF.save(pendingAccFromDb);
        }

        // Handle the case where no record with the given userId is found
        return new Register(); // Or throw an exception, return a specific response, etc.
    }

    public Register rejectRegistration(String email) {

        Optional<Register> user = regRepoIF.findByEmail(email);

        if (user.isPresent()) {
            //System.out.println("Okkkk");
            Register pendingAccFromDb = user.get();

            Register temp = pendingAccFromDb;
            //System.out.println(temp.getEmail());
            //UserTrack userTrack = usertrackRepo.findByEmail(pendingAccFromDb.getEmail());

            //usertrackRepo.delete(userTrack);

            regRepoIF.delete(pendingAccFromDb);
            return temp;
        }

        return new Register();
    }


    public Register fetchRecord(String email) throws DataFormatException {

        Optional<Register> dbDataOptional = regRepoIF.findByEmail(email);

        UserTrack userTrack = usertrackRepo.findByEmail(email);

        if (userTrack == null || userTrack.getStatus() != 1)
            return new Register();

        if (!dbDataOptional.isPresent())
            return new Register();

        Register dbData = dbDataOptional.get();

        return dbData;

        /*byte[] profilePic = dbData.getProfilePic();
        byte[] studentIdPic = (dbData.getRole().equalsIgnoreCase("student") && dbData.getStudentIdCardPic() != null) ? dbData.getStudentIdCardPic() : null;
        byte[] pvcPic = (dbData.getRole().equalsIgnoreCase("alumni") && dbData.getPVCPic() != null) ? dbData.getPVCPic() : null;


        Register fetchRecordWithDP = new Register();

        if(pvcPic==null){
                 fetchRecordWithDP = Register.builder()
                    .id(dbData.getId())
                    .name(dbData.getName())
                    .email(dbData.getEmail())
                    .password(dbData.getPassword())
                    .role(dbData.getRole())
                    .profilePic(profilePic)
                    .studentId(dbData.getStudentId())
                    .studentIdCardPic(studentIdPic)
                         .userStatus(dbData.getUserStatus())
                    .build();
        }
        else if(studentIdPic==null){
            fetchRecordWithDP = Register.builder()
                    .id(dbData.getId())
                    .name(dbData.getName())
                    .email(dbData.getEmail())
                    .password(dbData.getPassword())
                    .role(dbData.getRole())
                    .profilePic(profilePic)
                    .graduationYear(dbData.getGraduationYear())
                    .PVCPic(pvcPic)
                    .userStatus(dbData.getUserStatus())
                    .build();
        }*/

    }


    public List<Register> getAllRegisteredStudents() {

        List<Register> approvedAcc = regRepoIF.findByUserStatus(UserStatus.APPROVED);

        if (approvedAcc.size() == 0)
            return new ArrayList<>();

        return approvedAcc;
    }


    //private String currentlyLoggedInUserEmail=null;

    public int login(String email, String password) {

        Optional<Register> recordFromDBOptional = regRepoIF.findByEmail(email);

        if (!recordFromDBOptional.isPresent())
            return 0;

        Register recordFromDB = recordFromDBOptional.get();
        UserTrack userTrack = usertrackRepo.findByEmail(recordFromDB.getEmail());

        if (userTrack == null)
            return 3;

        String passwordFromDB = recordFromDB.getPassword();

        if (isAccountExistsAlready(email)) {

            if (recordFromDB.getUserStatus().equals(UserStatus.APPROVED)) {

                if (password.equals(passwordFromDB)) {

                    //currentlyLoggedInUserEmail = email;
                    userTrack.setStatus(1);
                    usertrackRepo.save(userTrack);

                    return 1;
                }

                //currentlyLoggedInUserEmail=null;
                userTrack.setStatus(2);
                usertrackRepo.save(userTrack);

                return 2;
            }
            else {
                return 0;
            }
        }
        else{
            return 3;
        }
    }


        public void logout (){

            String emailFromToken = requestInterceptorService.getTokenEmailUsedToOtherClass();
            System.out.println(emailFromToken);

            UserTrack userTrack = usertrackRepo.findByEmail(emailFromToken);
            System.out.println(userTrack);

            userTrack.setStatus(3);
            usertrackRepo.save(userTrack);

            // Destroying Token

            List<Token> expiredTokens = tokenInterface.findExpiredTokens(LocalDateTime.now());
            if (!expiredTokens.isEmpty()) {
                tokenInterface.deleteAll(expiredTokens);
            }

            //currentlyLoggedInUserEmail = null;
        }


        public UserTrack trackFindByEmail (String email){
            return usertrackRepo.findByEmail(email);
        }


        public boolean adminLogin (String email, String password){

            if (email.equals("CUETCSE@admin.cuet.ac.bd") && password.equals("1234")) {

                if (!usertrackRepo.existsByEmail(email)) {
                    UserTrack AdminUserTrack = UserTrack.builder()
                            .email(email)
                            .status(1)
                            .build();

                    usertrackRepo.save(AdminUserTrack);
                } else {
                    UserTrack adminUserTrack = usertrackRepo.findByEmail(email);
                    adminUserTrack.setStatus(1);
                    usertrackRepo.save(adminUserTrack);
                }

                return true;
            }
            return false;
        }

        public void adminLogout (){

            UserTrack adminUserTrack = usertrackRepo.findByEmail("CUETCSE@admin.cuet.ac.bd");

            adminUserTrack.setStatus(3);
            usertrackRepo.save(adminUserTrack);

            List<Token> expiredTokens = tokenInterface.findExpiredTokens(LocalDateTime.now());
            if (!expiredTokens.isEmpty()) {
                tokenInterface.deleteAll(expiredTokens);
            }
        }
    }
