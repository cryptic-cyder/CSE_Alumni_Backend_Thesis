package com.shahriar.CSE_Alumni_backend.Controllers;

import com.shahriar.CSE_Alumni_backend.Entities.Comment;
import com.shahriar.CSE_Alumni_backend.Entities.JobPost;
import com.shahriar.CSE_Alumni_backend.Entities.JobPostDTO;
import com.shahriar.CSE_Alumni_backend.Services.JobPostService;
import com.shahriar.CSE_Alumni_backend.Services.RegService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("api/v1")
@RestController
public class JobPostController {

    @Autowired
    private JobPostService jobPostService;

    @Autowired
    private RegService regService;

    //POST methods
    @PostMapping("/forPostingJob")
    public ResponseEntity<?> forPostingJob(
            @RequestParam("title") String jobTitle,
            @RequestParam("userEmail") String userEmail,
            @RequestParam("description") String jobDescription,
            @RequestParam(value = "jobImages", required = false) List<MultipartFile> jobImages
    ) throws IOException {

        if (regService.returnUserStatus(userEmail) != 1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);

        String response = jobPostService.postJob(jobTitle, userEmail, jobDescription, jobImages);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //GET methods
    @GetMapping("/fetch/allJobPost/{userEmail}")
    public ResponseEntity<?> fetchAllJobPost(@PathVariable String userEmail){

        if (regService.returnUserStatus(userEmail) != 1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);


        List<JobPost> allJobPost = jobPostService.getAllJobPost();

        if(allJobPost==null)
            return new ResponseEntity<>("No post yet...Site has just developed", HttpStatus.OK);

        return new ResponseEntity<>(allJobPost, HttpStatus.OK);
    }


    @GetMapping("/fetch/allJobPostOfAnyUser/{userEmail}")
    public ResponseEntity<?> fetchAllPostOfAnyUser(@PathVariable String userEmail){

        if (regService.returnUserStatus(userEmail) != 1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);


        List<JobPost> allJobPostOfAnyUser = jobPostService.findAllPostOfAnyUser(userEmail);

        if(allJobPostOfAnyUser==null)
            return new ResponseEntity<>("No post of this user : "+userEmail, HttpStatus.OK);

        return new ResponseEntity<>(allJobPostOfAnyUser, HttpStatus.OK);
    }

    @GetMapping("/fetch/anyParticularJob/{jobId}")
    public ResponseEntity<?> fetchAnyParticularJob(@PathVariable Long jobId){
        JobPost jobPost = jobPostService.findAnySpecificJob(jobId);

        if(jobPost == null){
            return new ResponseEntity<>("No such job post found", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(jobPost, HttpStatus.OK);
    }



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

        if(jobPostService.verificationPostCreator(jobId, userEmail)==false){
            return new ResponseEntity<>("You aren't owner of this post or the post doesn't exist",HttpStatus.UNAUTHORIZED);
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

        if(jobPostService.verificationPostCreator(postId, userEmail)==false){
            return new ResponseEntity<>("You aren't owner of this post or the post doesn't exist",HttpStatus.UNAUTHORIZED);
        }

        String response = jobPostService.deleteJob(postId);
        return ResponseEntity.ok(response);
    }

}
