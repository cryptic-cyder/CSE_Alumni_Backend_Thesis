package com.shahriar.CSE_Alumni_backend.Controllers;

import com.shahriar.CSE_Alumni_backend.Entities.Comment;
import com.shahriar.CSE_Alumni_backend.Entities.JobPost;
import com.shahriar.CSE_Alumni_backend.Entities.LoginResponse;
import com.shahriar.CSE_Alumni_backend.Entities.Token;
import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import com.shahriar.CSE_Alumni_backend.Services.CommentService;
import com.shahriar.CSE_Alumni_backend.Services.JobPostService;
import com.shahriar.CSE_Alumni_backend.Services.RegService;
import com.shahriar.CSE_Alumni_backend.Services.TokenValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JobPostService jobPostService;

    @Autowired
    private RegService regService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private TokenInterface tokenInterface;

    @PostMapping("/comment/{jobId}")
    public ResponseEntity<String> addCommentToJob(
            @PathVariable Long jobId,
            @RequestParam("commentText") String textContent,
            @RequestParam(value = "resume", required = false) MultipartFile resume,


            @RequestHeader("Authorization") String auth
    ) {

        String token = auth.replace("Bearer", "");

        if(new TokenValidation().isTokenValid(token)){

            try {

                String fileUrl = null;

                if (resume != null && !resume.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + jobId + "_" + resume.getOriginalFilename();
                    String filePath = uploadDir + "/" + fileName;
                    File dest = new File(filePath);
                    resume.transferTo(dest);
                    fileUrl = filePath;
                }

                String commenter = new TokenValidation().extractEmailFromToken(token);

                String response = commentService.addCommentToJob(jobId, commenter, textContent, fileUrl);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading resume");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You haven't logged in");
    }


    @PostMapping("/fetch/allCommentOfAnyPost/{jobId}")
    public ResponseEntity<List<Comment>> fetchAllCommentOfAnyPost(@PathVariable Long jobId,
                                                                  @RequestHeader("Authorization") String auth) {

        String token = auth.replace("Bearer", "");

        if(new TokenValidation().isTokenValid(token) ){

            String[] parts = token.split("_");
            Long id = Long.parseLong(parts[3]);

            Optional<Token> tokenFromDB = tokenInterface.findById(id);
            String emailFromTokenDB = tokenFromDB.get().getEmail();
            String emailFromBrowserToken = new TokenValidation().extractEmailFromToken(token);


            if(emailFromBrowserToken.equals(emailFromTokenDB)){

                List<Comment> comments = commentService.findAllCommentOfAnySpecificPost(jobId);

                if (comments == null || comments.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }

                return new ResponseEntity<>(comments, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }


    private static final String PDF_DIRECTORY
            = "C:\\Users\\HP\\Desktop\\Intellij Spring boot\\CSE_Alumni_Backend_Thesis\\Resumes";


    @GetMapping ("/pdf/{filename:.+}")
    public ResponseEntity<Resource> getPdf(@PathVariable String filename) throws IOException {

        System.out.println("FileName is : "+filename);

        Path filePath = Paths.get(PDF_DIRECTORY).resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());

        // Check if the file exists
        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            // Handle file not found or inaccessible
            return ResponseEntity.notFound().build();
        }
    }














//    @PutMapping("/update/comment/{jobId}/{commentId}")
//    public ResponseEntity<?> updateComment(
//            @PathVariable Long jobId,
//            @PathVariable Long commentId,
//            @RequestParam("userEmail") String user,
//            @RequestParam(value = "commentText", required = false) String textContent,
//            @RequestParam(value = "resume", required = false) MultipartFile resume
//    ) {
//
//        if (regService.returnUserStatus(user) != 1)
//            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);
//
//        if (!commentService.verificationPostCreator(commentId, user)) {
//            return new ResponseEntity<>("You aren't owner of this comment or this comment doesn't exist", HttpStatus.UNAUTHORIZED);
//        }
//
//        String response = commentService.updateCommentToJob(jobId, commentId, textContent, resume);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }


    @PostMapping("/delete/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String auth
    ) {


        System.out.println("Hit");

        LoginResponse response = new LoginResponse();

        String token = auth.replace("Bearer", "");

        if (new TokenValidation().isTokenValid(token)) {

            System.out.println("Token is valid...");

            String[] parts = token.split("_");
            Long id = Long.parseLong(parts[3]);

            Optional<Token> tokenFromDB = tokenInterface.findById(id);
            String emailFromTokenDB = tokenFromDB.get().getEmail();
            String emailFromBrowserToken = new TokenValidation().extractEmailFromToken(token);

            if (emailFromBrowserToken.equals(emailFromTokenDB)){

                System.out.println("Email is same...");

                Comment comment = commentService.findComment(commentId);
                String commentCreator = comment.getCommenter();

                System.out.println("\n\n "+commentCreator+" "+emailFromBrowserToken+"\n\n");

                if(commentCreator.equals(emailFromTokenDB)){

                    String feedback = commentService.deleteComment(commentId);

                    response.setMessage(feedback);
                    response.setToken(null);

                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }

                //System.out.println("Not owner...");

                response.setMessage("Token is expired...Or not the owner");
                response.setToken(null);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

        response.setMessage("Token is expired...Or not the owner");
        response.setToken(null);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

    }

//    public void saveFetchedResumes(List<Comment> allCommentOfAnyPost, Long jobId) {
//
//
//        for (Comment comment : allCommentOfAnyPost) {
//
//            if (comment.getResume() != null) {
//
//                byte[] byteDataFromPostman = comment.getResumeBytes();
//
//                String filePath = "C:\\Users\\HP\\Desktop\\Badhon\\Resume\\" + jobId + "." + comment.getId() + ".pdf";
//
//                try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                    fos.write(byteDataFromPostman);
//                    System.out.println("PDF file created successfully.");
//                } catch (IOException e) {
//                    System.out.println("Error writing PDF file: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//    }

}
