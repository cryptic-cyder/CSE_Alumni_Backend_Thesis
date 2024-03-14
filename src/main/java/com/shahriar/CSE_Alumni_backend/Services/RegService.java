package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Entities.UserDTO;
import com.shahriar.CSE_Alumni_backend.Entities.UserStatus;
import com.shahriar.CSE_Alumni_backend.Entities.UserTrack;
import com.shahriar.CSE_Alumni_backend.Repos.RegRepoIF;
import com.shahriar.CSE_Alumni_backend.Repos.UsertrackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import java.util.*;

@Service
public class RegService {

    @Autowired
    private RegRepoIF regRepoIF;

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
                    .studentId(role.equalsIgnoreCase("student") ? studentId : null)
                    .graduationYear(role.equalsIgnoreCase("alumni") ? graduationYear : null)
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


    public String updateAccount(String email, String name, String password, String role, MultipartFile profilePic,
                                String studentId, MultipartFile studentIdCard,
                                String graduationYear, MultipartFile pvc
                                ){
        try {

            Optional<Register> existingAccountOptional =regRepoIF.findByEmail(email);
            Register existingAccount = existingAccountOptional.get();

            if(existingAccount==null)
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

            return (temp!= null) ? "Account changes are saved: " + role :
                    "Error!!! Something went wrong...";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error!!! Something went wrong while processing the request";
        }

    }

    public String deleteAccount(String email){

        Register existingAcc = regRepoIF.findByEmail(email).get();

        if(existingAcc==null)
            return "No such account exists";

        regRepoIF.delete(existingAcc);

        UserTrack existingUserTrack = usertrackRepo.findByEmail(email);

        if(existingUserTrack!=null)
            usertrackRepo.delete(existingUserTrack);

        return "Your account is deleted successfully";
    }


    public boolean isAccountExistsAlready(String email){

        return regRepoIF.existsByEmail(email);
    }

    public int returnAdminStatus(){

        UserTrack adminUserTrack = usertrackRepo.findByEmail("CUETCSE@admin.cuet.ac.bd");

        if(adminUserTrack==null)
            return -1;

        int adminStatus = adminUserTrack.getStatus();

        return adminStatus;
    }

    public int returnUserStatus(String email){

        UserTrack userStatus = usertrackRepo.findByEmail(email);

        if(userStatus==null)
            return -2;

        return userStatus.getStatus();
    }

    public List<Register> getPendingUsers() {

        List<Register> existingRegister = regRepoIF.findByUserStatus(UserStatus.PENDING);

        if(existingRegister.size()==0)
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

            if(usertrackRepo.findByEmail(email)==null || !usertrackRepo.existsByEmail(email)){
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

    public Register rejectRegistration(String email){

        Optional<Register> user = regRepoIF.findByEmail(email);

        if(user.isPresent()) {
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

        if(userTrack==null || userTrack.getStatus()!=1)
            return new Register();

        if(!dbDataOptional.isPresent())
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

    public List<UserDTO> getAllAcceptedAccBasic() {

        List<Register> userAllBasic = regRepoIF.findByUserStatus(UserStatus.APPROVED);

        return userAllBasic.stream()
                .map(user -> {
                    UserDTO userDto = new UserDTO();

                    userDto.setEmail(user.getEmail());
                    userDto.setName(user.getName());
                    userDto.setProfilePic(user.getProfilePic());
                    userDto.setRole(user.getRole());

                    return userDto;
                })
                .collect(Collectors.toList());
    }


    public List<Register> getAllRegisteredStudents(){

        List<Register> approvedAcc = regRepoIF.findByUserStatus(UserStatus.APPROVED);

        if(approvedAcc.size()==0)
            return new ArrayList<>();

        return approvedAcc;
    }



    //private String currentlyLoggedInUserEmail=null;

    public int login(String email, String password){

        Optional<Register> recordFromDBOptional = regRepoIF.findByEmail(email);

        if(!recordFromDBOptional.isPresent())
            return 0;

        Register recordFromDB = recordFromDBOptional.get();
        UserTrack userTrack = usertrackRepo.findByEmail(recordFromDB.getEmail());

        String passwordFromDB = recordFromDB.getPassword();

        if(recordFromDB.getUserStatus().equals(UserStatus.APPROVED) ){

            if(password.equals(passwordFromDB)){

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

        //currentlyLoggedInUserEmail=null;
        return 3;

    }


    public void logout(String email){

        UserTrack userTrack = usertrackRepo.findByEmail(email);
        System.out.println(userTrack);

        userTrack.setStatus(3);
        usertrackRepo.save(userTrack);

        //currentlyLoggedInUserEmail = null;
    }


    public UserTrack trackFindByEmail(String email){
        return usertrackRepo.findByEmail(email);
    }


    public boolean adminLogin(String email, String password){

           if(email.equals("CUETCSE@admin.cuet.ac.bd") && password.equals("1234")){

               if(!usertrackRepo.existsByEmail(email)){
                   UserTrack AdminUserTrack = UserTrack.builder()
                           .email(email)
                           .status(1)
                           .build();

                   usertrackRepo.save(AdminUserTrack);
               }
               else{
                   UserTrack adminUserTrack = usertrackRepo.findByEmail(email);
                   adminUserTrack.setStatus(1);
                   usertrackRepo.save(adminUserTrack);
               }

               return true;
           }
          return false;
    }

    public void adminLogout(String email){

        UserTrack adminUserTrack = usertrackRepo.findByEmail("CUETCSE@admin.cuet.ac.bd");

        adminUserTrack.setStatus(3);
        usertrackRepo.save(adminUserTrack);
    }

}
