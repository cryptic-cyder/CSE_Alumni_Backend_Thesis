package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.Comment;
import com.shahriar.CSE_Alumni_backend.Entities.JobPost;
import com.shahriar.CSE_Alumni_backend.Repos.CommentInterface;
import com.shahriar.CSE_Alumni_backend.Repos.JobPostInterface;
import jakarta.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.*;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Service
public class CommentService {


    @Autowired
    private JobPostInterface jobPostInterface;

    @Autowired
    private CommentInterface commentInterface;

//    private File convertMultiPartToFile(MultipartFile file) throws IOException {
//
//        File convertedFile = new File(file.getOriginalFilename());
//        FileOutputStream fos = new FileOutputStream(convertedFile);
//
//        fos.write(file.getBytes());
//
//        fos.close();
//
//        return convertedFile;
//    }




//    private byte[] compressFile(File file) throws IOException {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
//            ZipEntry entry = new ZipEntry(file.getName());
//            zos.putNextEntry(entry);
//
//            byte[] bytes = new byte[1024];
//            int length;
//            try (FileInputStream fis = new FileInputStream(file)) {
//                while ((length = fis.read(bytes)) >= 0) {
//                    zos.write(bytes, 0, length);
//                }
//            }
//            zos.closeEntry();
//        }
//        return baos.toByteArray();
//    }


    @Value("${file.upload-dir}")
    private String uploadDir;
    public String addCommentToJob(Long jobId, String commenter, String textContent, String url) {

        Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);

        if (jobPostOptional.isPresent()) {

            JobPost jobPost = jobPostOptional.get();

            Comment comment = new Comment();
            comment.setCommenter(commenter);
            comment.setTextContent(textContent);
            comment.setUrl(url);
            comment.setJobPost(jobPost);

            commentInterface.save(comment);

            return "Comment is added successfully...";
        } else {
            return "No such job post exists";
        }

    }


    public List<Comment> findAllCommentOfAnySpecificPost(Long jobId) {

        Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);

        if (jobPostOptional.isPresent()) {
            JobPost jobPost = jobPostOptional.get();
            return jobPost.getComments();
        }

        return null;
    }






//        try {
//
//            String encodedResume = null;
//
//            if (!resume.isEmpty()) {
//
//                File file = convertMultiPartToFile(resume);
//
//                byte[] compressedResume = compressFile(file);
//
//                encodedResume = Base64.getEncoder().encodeToString(compressedResume);
//            }
//
//            Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);
//
//            if (jobPostOptional.isPresent()) {
//
//                JobPost jobPost = jobPostOptional.get();
//
//                Comment comment = Comment.builder()
//
//                        .commentedAt(LocalDateTime.now())
//                        .commenter(userEmail)
//                        .textContent(textContent)
//                        .resume(encodedResume)
//                        .jobPost(jobPost)
//
//                        .build();
//
//                if (jobPost.getComments() != null) {
//                    jobPost.getComments().add(comment);
//                } else {
//                    List<Comment> commentListToAdd = new ArrayList<>();
//                    commentListToAdd.add(comment);
//                    jobPost.setComments(commentListToAdd);
//                }
//
//                commentInterface.save(comment);
//
//                return "Comment is added successfully...";
//            } else {
//                return "No such job post exists";
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error adding comment";
//        }






//    public String updateCommentToJob(Long jobId, Long commentId, String textContent, MultipartFile resume) {
//
//        try {
//
//            String encodedResume = null;
//
//            if (!resume.isEmpty()) {
//
//                File file = convertMultiPartToFile(resume);
//
//                byte[] compressedResume = compressFile(file);
//
//                encodedResume = Base64.getEncoder().encodeToString(compressedResume);
//            }
//
//            Optional<Comment> commentOptional = commentInterface.findById(commentId);
//            Comment comment = commentOptional.get();
//
//            String textContent1;
//            if (textContent == null)
//                textContent1 = commentOptional.get().textContent;
//            else
//                textContent1 = textContent;
//
//            JobPost jobPost = jobPostInterface.findById(jobId).get();
//
//
//            if (jobPost!=null) {
//
//                if (comment!=null) {
//
//                  if(textContent1!=null)
//                      comment.setTextContent(textContent1);
//                  if(resume!=null)
//                      comment.setResume(encodedResume);
//
//                    commentInterface.save(comment);
//
//                    return "Comment is updated successfully...";
//                }
//                else
//                    return "No such comment exists";
//            }
//            else
//                return "No such job post exists";
//        }
//        catch(Exception e){
//                e.printStackTrace();
//                return "Error adding comment";
//            }
//    }



    public String deleteComment(Long commentId) {

        try{
            if(commentInterface.findById(commentId).isPresent()){

                Comment comment = commentInterface.findById(commentId).get();

                commentInterface.delete(comment);
            }
            else{
                return "No such comment exists...";
            }
            return "Comment is successfully deleted...";
        }
        catch (Exception e){
            return "Something went wrong while deleting the comment...";
        }

    }



//
//            List<Comment> comments = null;
//
//            if (jobPostInterface.findById(jobId) != null) {
//
//                JobPost jobPost = jobPostInterface.findById(jobId).get();
//
//                comments = jobPost.getComments();
//
////                if (comments != null) {
////
////                    for (Comment comment : comments) {
////
////                        if (comment.getResume() != null) {
////
////                            String encodedResume = comment.getResume();
////                            byte[] decodedResume = Base64.getDecoder().decode(encodedResume);
////                            decompressedResume = decompress(decodedResume);
////
////                            comment.setResumeBytes(decompressedResume);
////                        }
////                        else {
////                            comment.setResumeBytes(null);
////                        }
////                    }
////                }
//            } else {
//                return null;
//            }
//
//            return comments;



//    public boolean verificationPostCreator (Long commentId, String userEmail){
//
//
//            Optional<Comment> commentOptional = commentInterface.findById(commentId);
//
//            if (!commentOptional.isPresent())
//                return false;
//
//            Comment specificComment = commentOptional.get();
//
//            if (userEmail.equals(specificComment.getCommenter()))
//                return true;
//
//            return false;
//        }
//
//        public byte[] decompress ( byte[] compressedBytes) throws IOException {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(compressedBytes))) {
//                ZipEntry entry;
//                while ((entry = zis.getNextEntry()) != null) {
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = zis.read(buffer)) > 0) {
//                        bos.write(buffer, 0, length);
//                    }
//                }
//            }
//            return bos.toByteArray(); // Assuming the resume is in string format
//        }
}
