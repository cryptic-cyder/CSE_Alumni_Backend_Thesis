package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.Comment;
import com.shahriar.CSE_Alumni_backend.Entities.JobPost;
import com.shahriar.CSE_Alumni_backend.Repos.CommentInterface;
import com.shahriar.CSE_Alumni_backend.Repos.JobPostInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.DeflaterOutputStream;

@Service
public class CommentService {


    @Autowired
    private JobPostInterface jobPostInterface;

    private byte[] compressResume(byte[] resumeData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream)) {
            deflaterOutputStream.write(resumeData);
        }
        return outputStream.toByteArray();
    }


    public String addCommentToJob(Long jobId, String userEmail, String textContent, MultipartFile resume) {
        try {

            byte[] compressedResume;
            String compressedResumeBase64 = null;

            if (resume != null && !resume.isEmpty()) {

                compressedResume = compressResume(resume.getBytes());
                compressedResumeBase64 = Base64.getEncoder().encodeToString(compressedResume);

            }

            Comment comment = Comment.builder()
                    .commentedAt(LocalDateTime.now())
                    .commenter(userEmail)
                    .textContent(textContent)
                    .encodedResume(compressedResumeBase64)
                    .jobPost(new JobPost())
                    .build();

            Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);

            JobPost jobPost = jobPostOptional.get();

            if(jobPost.getComments()!=null)
                jobPost.getComments().add(comment);
            else{
                List<Comment> commentsOfAnyPost = new ArrayList<>();
                commentsOfAnyPost.add(comment);
                jobPost.setComments(commentsOfAnyPost);
            }

            jobPost.setComments(jobPost.getComments());

            return  "Comment is added successfully";
        }
        catch (IOException e) {
            e.printStackTrace();
            return "Error!!! Something went wrong while processing the request";
        }
    }


    /*public String addCommentToJob(Long jobId, String textContent, MultipartFile resume) {
        try {
            Optional<JobPost> optionalJobPost = jobPostInterface.findById(jobId);

            if (optionalJobPost.isPresent()) {

                JobPost jobPost = optionalJobPost.get();

                // Create a new comment using the factory method
                Comment comment = Comment.createComment(textContent, resume, jobPost);

                jobPost.getComments().add(comment);
                jobPostInterface.save(jobPost);

                return "Comment added successfully to JobId: " + jobId;
            } else {
                return "Error!!! Job with ID: " + jobId + " not found.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error!!! Something went wrong while processing the request";
        }
    }*/

    @Autowired
    private CommentInterface commentInterface;

    public List<Comment> findAllCommentOfAnySpecificPost(Long jobId) {

        List<Comment> commentsOfSpecificPost = new ArrayList<>();
        Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);

        if (jobPostOptional.isPresent()) {
            commentsOfSpecificPost = commentInterface.findByJobPost(Optional.of(jobPostOptional.get()));
            /*for (Comment comment : commentsOfSpecificPost) {
                var commentDTO = new CommentDTO(
                        comment.getId(),
                        comment.getCommentedAt(),
                        comment.getJobPost().getId(),
                        comment.getTextContent()
                );
                commentDTOs.add(commentDTO);
            }*/
        }

        return commentsOfSpecificPost;
    }


}
