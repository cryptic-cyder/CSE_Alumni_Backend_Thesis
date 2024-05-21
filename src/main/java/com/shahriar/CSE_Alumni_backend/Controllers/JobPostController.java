package com.shahriar.CSE_Alumni_backend.Controllers;

import com.shahriar.CSE_Alumni_backend.Entities.Comment;
import com.shahriar.CSE_Alumni_backend.Entities.CommentDTO;
import com.shahriar.CSE_Alumni_backend.Entities.JobPost;

import com.shahriar.CSE_Alumni_backend.Entities.JobPostDTO;
import com.shahriar.CSE_Alumni_backend.Services.JobPostService;
import com.shahriar.CSE_Alumni_backend.Services.RegService;

import com.shahriar.CSE_Alumni_backend.Services.TokenValidation;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class JobPostController {

    @Autowired
    private JobPostService jobPostService;

    @Autowired
    private RegService regService;


//    @GetMapping("/search")
//    public ResponseEntity<?> search(@RequestParam("searchContent") String query) throws IOException {
//
//        List<JobPost> searchResults = jobPostService.performSearch(query);
//
//        if (searchResults == null) {
//            return new ResponseEntity<>("No matching is found", HttpStatus.OK);
//        }
//
//        saveSearchResults(searchResults);
//
//        return ResponseEntity.ok().body(searchResults);
//    }

    //POST methods
    @PostMapping("/forPostingJob")
    public ResponseEntity<?> forPostingJob(
            @RequestParam("title") String jobTitle,
            @RequestParam("description") String jobDescription,
            @RequestParam(value = "jobImages", required = false) List<MultipartFile> jobImages,

            @RequestHeader("Authorization") String auth
    ) throws IOException {

        String token = auth.replace("Bearer", "");
        //System.out.println("No of images : "+ jobImages.size());

        if(new TokenValidation().isTokenValid(token)){

            String userEmail =  new TokenValidation().extractEmailFromToken(token);

            String response = jobPostService.postJob(jobTitle, userEmail, jobDescription, jobImages);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }


    //GET methods
    @GetMapping("/fetch/allJobPost")
    public ResponseEntity<?> fetchAllJobPost() throws IOException {

        //System.out.println("JobPosts...");
        List<JobPost> allJobPost = jobPostService.getAllJobPost();

        if (allJobPost == null)
            return new ResponseEntity<>("No post yet...Site has just developed", HttpStatus.OK);

//        for(JobPost jobPost: allJobPost){
//            List<Comment> commentList = jobPost.getComments();
//            System.out.println(jobPost.getTitle()+" : "+commentList.size());
//            for(Comment comment:commentList){
//                if(comment.getId()==2){
//                    System.out.println(comment.getTextContent()+" "+comment.getResume());
//                }
//            }
//        }

        //saveImagesInSystem(allJobPost);

        return new ResponseEntity<>(allJobPost, HttpStatus.OK);
    }


//    @GetMapping("/fetch/allJobPostOfAnyUser")
//    public ResponseEntity<?> fetchAllPostOfAnyUser(@PathVariable String userEmail) throws IOException {
//
//        if (regService.returnUserStatus(userEmail) != 1)
//            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);
//
//
//        List<JobPost> allJobPostOfAnyUser = jobPostService.findAllPostOfAnyUser(userEmail);
//
//        if (allJobPostOfAnyUser == null)
//            return new ResponseEntity<>("No post of this user : " + userEmail, HttpStatus.OK);
//
//        saveImagesAndResumesOfAnyUser(allJobPostOfAnyUser, userEmail);
//
//        return new ResponseEntity<>(allJobPostOfAnyUser, HttpStatus.OK);
//    }
//
//    @GetMapping("/fetch/anyParticularJob/{jobId}")
//    public ResponseEntity<?> fetchAnyParticularJob(@PathVariable Long jobId) throws IOException {
//
//        JobPost jobPost = jobPostService.findAnySpecificJob(jobId);
//
//        if (jobPost == null) {
//            return new ResponseEntity<>("No such job post found", HttpStatus.NO_CONTENT);
//        }
//
//        saveImagesInSystemForSpecificPost(jobPost);
//        return new ResponseEntity<>(jobPost, HttpStatus.OK);
//    }
//

    //PUT methods
    @PutMapping("/updateJob/{jobId}")
    public ResponseEntity<?> updateJobPost(
            @PathVariable Long jobId,
            @RequestParam(value = "title", required = false) String jobTitle,
            @RequestParam("userEmail") String userEmail,
            @RequestParam(value = "description", required = false) String jobDescription,
            @RequestParam(value = "jobImages", required = false) List<MultipartFile> jobImages
    ) throws IOException {

        if (regService.returnUserStatus(userEmail) != 1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.UNAUTHORIZED);

        if (!jobPostService.verificationPostCreator(jobId, userEmail)) {
            return new ResponseEntity<>("You aren't owner of this post or the post doesn't exist", HttpStatus.UNAUTHORIZED);
        }

        String response = jobPostService.updateJob(jobId, jobTitle, userEmail, jobDescription, jobImages);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deleteJob(
            @PathVariable Long postId,
            @RequestParam("userEmail") String userEmail
    ) {

        if (regService.returnUserStatus(userEmail) != 1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.UNAUTHORIZED);

        if (!jobPostService.verificationPostCreator(postId, userEmail)) {
            return new ResponseEntity<>("You aren't owner of this post or the post doesn't exist", HttpStatus.UNAUTHORIZED);
        }

        String response = jobPostService.deleteJob(postId);
        return ResponseEntity.ok(response);
    }

//    public void saveImagesInSystemForSpecificPost(JobPost jobPost) {
//
//
//        List<byte[]> images = jobPost.getDecodedImages(); // Assuming you have a method to get the images byte data
//        String jobFolder = "C:\\Users\\Shahriar\\Desktop\\ImageTemp\\Resumes&Images\\Images\\SpecificPost\\" + "Job_";
//
//
//        if (images != null) {
//
//            // Save images of each job into their respective folder
//            for (int i = 0; i < images.size(); i++) {
//                byte[] imageData = images.get(i);
//                String filePath = jobFolder + jobPost.getId() + "." + (i + 1) + ".jpg";
//
//                try (FileOutputStream fos = new FileOutputStream(filePath)) {
//
//                    fos.write(imageData);
//                } catch (IOException e) {
//                    System.out.println("Error writing image file: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//        List<Comment> allCommentOfAnyPost = jobPost.getComments();
//
//        if (allCommentOfAnyPost != null) {
//
//
//            for (Comment comment : allCommentOfAnyPost) {
//
//                if (comment.getResume() != null) {
//
//                    byte[] byteDataFromPostman = comment.getResumeBytes();
//
//                    String filePath = jobFolder + jobPost.getId() + "." + comment.getId() + ".pdf";
//
//                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                        fos.write(byteDataFromPostman);
//                    } catch (IOException e) {
//                        System.out.println("Error writing PDF file: " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    public void saveSearchResults(List<JobPost> jobPosts) {
//
//        String searchResultFolder = "C:\\Users\\Shahriar\\Desktop\\ImageTemp\\Resumes&Images\\Images\\SearchResults\\";
//
//        for (JobPost jobPost : jobPosts) {
//
//            List<byte[]> images = jobPost.getDecodedImages();
//
//            if (images != null) {
//
//                for (int i = 0; i < images.size(); i++) {
//
//                    byte[] imageData = images.get(i);
//                    String filePath = searchResultFolder + jobPost.getId() + "." + (i + 1) + ".jpg";
//
//                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
//
//                        fos.write(imageData);
//                    }
//                    catch (IOException e) {
//                        System.out.println("Error writing image file: " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            List<Comment> comments = jobPost.getComments();
//
//            if(comments!=null){
//
//                for (Comment comment : comments) {
//
//                    if (comment.getResume() != null) {
//
//                        byte[] byteDataFromPostman = comment.getResumeBytes();
//
//                        String filePath = searchResultFolder + jobPost.getId() + "." + comment.getId() + ".pdf";
//
//                        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                            fos.write(byteDataFromPostman);
//                        }
//                        catch (IOException e) {
//                            System.out.println("Error writing PDF file: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    public void saveImagesInSystem(List<JobPost> allJobPost) {
//
//
//        for (JobPost jobPost : allJobPost) {
//
//            List<byte[]> images = jobPost.getDecodedImages(); // Assuming you have a method to get the images byte data
//            String jobFolder = "C:\\Users\\HP\\Desktop\\Badhon\\JobPosts\\" + "Job_" + jobPost.getId() + "\\";
//
//            File folder = new File(jobFolder);
//
//            if (images != null) {
//
//                if (!folder.exists()) {
//
//                    if (!folder.mkdirs()) {
//                        System.out.println("Failed to create directory: " + jobFolder);
//                    }
//                }
//
//                // Save images of each job into their respective folder
//                for (int i = 0; i < images.size(); i++) {
//                    byte[] imageData = images.get(i);
//                    String filePath = jobFolder + jobPost.getId() + "." + (i + 1) + ".jpg";
//
//                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
//
//                        fos.write(imageData);
//                    } catch (IOException e) {
//                        System.out.println("Error writing image file: " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            }
//            List<Comment> allCommentOfAnyPost = jobPost.getComments();
//
//            if (allCommentOfAnyPost != null) {
//
//
//                if (!folder.exists()) {
//
//                    if (!folder.mkdirs()) {
//                        System.out.println("Failed to create directory: " + jobFolder);
//                    }
//                }
//
//                for (Comment comment : allCommentOfAnyPost) {
//
//                    if (comment.getResume() != null) {
//
//                        byte[] byteDataFromPostman = comment.getResumeBytes();
//
//                        String filePath = jobFolder + jobPost.getId() + "." + comment.getId() + ".pdf";
//
//                        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                            fos.write(byteDataFromPostman);
//                        } catch (IOException e) {
//                            System.out.println("Error writing PDF file: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    public void saveImagesAndResumesOfAnyUser(List<JobPost> allJobPostOfAnyUser, String userEmail) {
//
//        String jobFolder = "C:\\Users\\Shahriar\\Desktop\\ImageTemp\\Resumes&Images\\Images\\" + userEmail + "\\";
//        File folder = new File(jobFolder);
//
//        for (JobPost jobPost : allJobPostOfAnyUser) {
//
//            List<byte[]> images = jobPost.getDecodedImages(); // Assuming you have a method to get the images byte data
//            List<Comment> allCommentOfAnyPost = jobPost.getComments();
//
//            if (images != null) {
//
//                if (!folder.exists()) {
//
//                    if (!folder.mkdirs()) {
//                        System.out.println("Failed to create directory: " + jobFolder);
//                    }
//                }
//
//                // Save images of each job into their respective folder
//                for (int i = 0; i < images.size(); i++) {
//                    byte[] imageData = images.get(i);
//                    String filePath = jobFolder + jobPost.getId() + "." + (i + 1) + ".jpg";
//
//                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
//
//                        fos.write(imageData);
//                    } catch (IOException e) {
//                        System.out.println("Error writing image file: " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//
//            if (allCommentOfAnyPost != null) {
//
//                if (!folder.exists()) {
//
//                    if (!folder.mkdirs()) {
//                        System.out.println("Failed to create directory: " + jobFolder);
//                    }
//                }
//
//                for (Comment comment : allCommentOfAnyPost) {
//
//                    if (comment.getResume() != null) {
//
//                        byte[] byteDataFromPostman = comment.getResumeBytes();
//
//                        String filePath = jobFolder + jobPost.getId() + "." + comment.getId() + ".pdf";
//
//                        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                            fos.write(byteDataFromPostman);
//                        } catch (IOException e) {
//                            System.out.println("Error writing PDF file: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }

}
