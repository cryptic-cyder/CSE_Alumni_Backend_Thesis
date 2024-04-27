package com.shahriar.CSE_Alumni_backend.Controllers;

import com.shahriar.CSE_Alumni_backend.Entities.Comment;
import com.shahriar.CSE_Alumni_backend.Services.CommentService;
import com.shahriar.CSE_Alumni_backend.Services.JobPostService;
import com.shahriar.CSE_Alumni_backend.Services.RegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("api/v1")
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private RegService regService;

    @PostMapping("/commnet/{userEmail}/{jobId}")
    public ResponseEntity<String> addCommentToJob(
            @PathVariable Long jobId,
            @PathVariable String userEmail,
            @RequestParam("commentText") String textContent,
            @RequestParam(value = "resume" ,required = false) MultipartFile resume
    ) {

        if (regService.returnUserStatus(userEmail) != 1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);


        String response = commentService.addCommentToJob(jobId,userEmail, textContent, resume);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/fetch/allCommentOfAnyPost/{userEmail}{jobId}")
    public ResponseEntity<?> fetchAllCommentOfAnyPost(@PathVariable Long jobId, @PathVariable String userEmail){

        if (regService.returnUserStatus(userEmail) != 1)
            return new ResponseEntity<>("You are not logged in...or your account is pending", HttpStatus.OK);


        List<Comment> allCommentOfAnyPost = commentService.findAllCommentOfAnySpecificPost(jobId);

        if(allCommentOfAnyPost==null)
            return new ResponseEntity<>("No comment yet to this post", HttpStatus.OK);

        return new ResponseEntity<>(allCommentOfAnyPost, HttpStatus.OK);
    }

}