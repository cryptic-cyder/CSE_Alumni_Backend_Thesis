package com.shahriar.CSE_Alumni_backend.Services;


import com.fasterxml.jackson.core.JsonToken;
import com.shahriar.CSE_Alumni_backend.Entities.*;
import com.shahriar.CSE_Alumni_backend.Repos.CommentInterface;
import com.shahriar.CSE_Alumni_backend.Repos.JobPostInterface;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.directory.SearchResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class JobPostService {

    @Autowired
    private JobPostInterface jobPostInterface;

    @Autowired
    private CommentInterface commentInterface;

//    public List<JobPost> performSearch(String queryToBeSearched) throws IOException {
//
//        // Perform search in posts and comments
//        List<JobPost> searchResults = jobPostInterface.findByDescriptionContaining(queryToBeSearched);
//        //List<Comment> comments = commentInterface.findByTextContaining(query);
//
//        //searchResults.addAll(comments.stream().map(comment -> new SearchResult(comment.getId(), "comment", comment.getText())).collect(Collectors.toList()));
//
//        for (JobPost eachPost : searchResults) {
//
//            List<String> listOfBase64VersionOfEachImage = new ArrayList<>();
//            List<byte[]> decodedImages = new ArrayList<>();
//
//            listOfBase64VersionOfEachImage = (eachPost.getImages() != null) ? eachPost.getImages() : null;
//
//            decodedImages = (listOfBase64VersionOfEachImage != null) ?
//                    decodeImages(listOfBase64VersionOfEachImage) : null;
//
//            eachPost.setDecodedImages(decodedImages);
//
//            List<Comment> commentsOfThisPost = findAllCommentOfAnySpecificPost(eachPost.getId());
//            eachPost.setComments(commentsOfThisPost);
//        }
//
//        return searchResults;
//    }


    public String postJob(String title, String userEmail, String description, List<MultipartFile> jobImagesData) {

        try {
            List<String> compressedImagesBase64 = new ArrayList<>();

            if (jobImagesData == null) {
                compressedImagesBase64 = null;
            } else {
                for (MultipartFile eachImage : jobImagesData) {
                    if (eachImage != null && !eachImage.isEmpty()) {

                        byte[] compressedImage = compressImage(eachImage.getBytes());
                        String compressedImageBase64 = Base64.getEncoder().encodeToString(compressedImage);

                        compressedImagesBase64.add(compressedImageBase64);
                    }
                }
            }

            JobPost jobpost = JobPost.builder()

                    .title(title)
                    .userEmail(userEmail)
                    .description(description)
                    .images(compressedImagesBase64)
                    .postedAt(LocalDateTime.now())
                    .build();


            JobPost saveJobPost = jobPostInterface.save(jobpost);

            return (saveJobPost != null) ? "Job is posted successfully" :
                    "Error!!! Something went wrong... Job can not be posted...";
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            return "Error!!! Something went wrong while processing the request";
        }
    }

    private byte[] compressImage(byte[] imageData) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(new ByteArrayInputStream(imageData))
                .size(500, 500) // Set desired dimensions
                .outputQuality(0.5) // Set desired quality (0.0 - 1.0)
                .toOutputStream(outputStream); // Write compressed image data to output stream

        return outputStream.toByteArray();
    }



    public boolean verificationPostCreator(Long jobId, String userEmail) {

        Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);

        if (!jobPostOptional.isPresent())
            return false;

        JobPost specificJobPost = jobPostOptional.get();

        if (userEmail.equals(specificJobPost.getUserEmail()))
            return true;

        return false;
    }

    public byte[] decompress(byte[] compressedBytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(compressedBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = zis.read(buffer)) > 0) {
                    bos.write(buffer, 0, length);
                }
            }
        }
        return bos.toByteArray(); // Assuming the resume is in string format
    }

//    public List<Comment> findAllCommentOfAnySpecificPost(Long jobId) throws IOException {
//
//        List<Comment> comments = null;
//        byte[] decompressedResume = null;
//
//        if (jobPostInterface.findById(jobId) != null) {
//
//            JobPost jobPost = jobPostInterface.findById(jobId).get();
//
//            comments = jobPost.getComments();
//
//            if (comments != null) {
//
//                for (Comment comment : comments) {
//
//                    if (comment.getResume() != null) {
//
//                        String encodedResume = comment.getResume();
//                        byte[] decodedResume = Base64.getDecoder().decode(encodedResume);
//                        decompressedResume = decompress(decodedResume);
//
//                        comment.setResumeBytes(decompressedResume);
//                    } else {
//                        comment.setResumeBytes(null);
//                    }
//                }
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
//
//        return comments;
//    }


    public List<JobPost> getAllJobPost() throws IOException {



        // Access the images field containing Base64-encoded strings
       List<JobPost> jobPostList = jobPostInterface.findAll();

       return jobPostList;


//        for (JobPost eachPost : postList) {
//
//            List<String> listOfBase64VersionOfEachImage = new ArrayList<>();
//            List<byte[]> decodedImages = new ArrayList<>();
//
//            listOfBase64VersionOfEachImage = (eachPost.getImages() != null) ? eachPost.getImages() : null;
//
//            decodedImages = (listOfBase64VersionOfEachImage != null) ?
//                    decodeImages(listOfBase64VersionOfEachImage) : null;
//
//            eachPost.setDecodedImages(decodedImages);
//
//            List<Comment> commentsOfThisPost = findAllCommentOfAnySpecificPost(eachPost.getId());
//            eachPost.setComments(commentsOfThisPost);

//            if(eachPost.getId()==1){
//                for(Comment comment : commentsOfThisPost){
//                    if(comment.getId()==1){
//                        System.out.println(comment.getTextContent()+"\n"+comment.getResume());
//                    }
//                }
//            }

           //jobPostInterface.save(eachPost);
        //}





//        List<JobPost> jobPostList = jobPostInterface.findAll();
//        List<JobPostDTO> convertedPost = new ArrayList<>();
//
//        for(JobPost job:jobPostList) {
//
//            JobPostDTO jobPostDTO = convertToDTO(job);
//            convertedPost.add(jobPostDTO);
//        }
//
//        return convertedPost;
//

    }

    private List<byte[]> decodeImages(List<String> listOfBase64VersionOfEachImage) {

        List<byte[]> decodedImages = new ArrayList<>();

        for (String base64VersionOfEachImage : listOfBase64VersionOfEachImage) {

            byte[] decodedImage = Base64.getDecoder().decode(base64VersionOfEachImage);
            decodedImages.add(decodedImage);

        }

        return decodedImages;
    }




//    private JobPostDTO convertToDTO(JobPost jobPost) {
//
//        JobPostDTO jobPostDTO = new JobPostDTO();
//        jobPostDTO.setId(jobPost.getId());
//        jobPostDTO.setDescription(jobPost.getDescription());
//        jobPostDTO.setPostedAt(jobPost.getPostedAt());
//
//        List<String> stringListOfAnyPost = new ArrayList<>();
//
//        stringListOfAnyPost.addAll(jobPost.getImages());
//
//        jobPostDTO.setDecodedImages(stringListOfAnyPost);
//
//
//        jobPostDTO.setTitle(jobPost.getTitle());
//        jobPostDTO.setUserEmail(jobPost.getUserEmail());
//
//
//        List<Comment> listOfComments = jobPost.getComments();
//        List<CommentDTO> commentDTOList = new ArrayList<>();
//
//
//        for(int i=0; i<listOfComments.size(); i++){
//            Comment comment = listOfComments.get(i);
//            CommentDTO commentDTO = convertToDTO(comment);
//            commentDTOList.add(commentDTO);
//        }
//
//        jobPostDTO.setComments(commentDTOList);
//
//        return jobPostDTO;
//    }

//    private CommentDTO convertToDTO(Comment comment) {
//
//        CommentDTO commentDTO = new CommentDTO();
//
//        commentDTO.setId(comment.getId());
//        commentDTO.setCommentedAt(comment.getCommentedAt());
//        commentDTO.setCommenter(comment.getCommenter());
//        commentDTO.setTextContent(comment.getTextContent());
//
//        if (comment.getResume() != null) {
//            commentDTO.setDecodedResume(comment.getResume());
//        }
//
//
//        return commentDTO;
//    }





















//    public JobPost findAnySpecificJob(Long jobId) throws IOException {
//
//        Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);
//
//        if (jobPostOptional.isPresent()) {
//            JobPost jobPost = jobPostOptional.get();
//
//            List<String> listOfBase64VersionOfEachImage = new ArrayList<>();
//            List<byte[]> decodedImages = new ArrayList<>();
//
//            listOfBase64VersionOfEachImage = (jobPost.getImages() != null) ? jobPost.getImages() : null;
//
//            decodedImages = (listOfBase64VersionOfEachImage != null) ?
//                    decodeImages(listOfBase64VersionOfEachImage) : null;
//
//            jobPost.setDecodedImages(decodedImages);
//
//            List<Comment> comments = findAllCommentOfAnySpecificPost(jobId);
//            jobPost.setComments(comments);
//
//            return jobPost;
//        }
//
//        return null;
//    }


//    public List<JobPost> findAllPostOfAnyUser(String userEmail) throws IOException {
//
//        List<JobPost> postListOfAnyUser = jobPostInterface.findByUserEmail(userEmail);
//
//        if (postListOfAnyUser == null) {
//            return new ArrayList<>();
//        }
//
//        for (JobPost eachPost : postListOfAnyUser) {
//
//            List<String> listOfBase64VersionOfEachImage = new ArrayList<>();
//            List<byte[]> decodedImages = new ArrayList<>();
//
//            listOfBase64VersionOfEachImage = (eachPost.getImages() != null) ? eachPost.getImages() : null;
//
//            decodedImages = (listOfBase64VersionOfEachImage != null) ?
//                    decodeImages(listOfBase64VersionOfEachImage) : null;
//
//            eachPost.setDecodedImages(decodedImages);
//
//            List<Comment> comments = findAllCommentOfAnySpecificPost(eachPost.getId());
//            eachPost.setComments(comments);
//
//
//        }
//
//        return postListOfAnyUser;
//
//    }


    public String updateJob(Long postId, String title, String userEmail, String description, List<MultipartFile> jobImagesData) {
        try {

            Optional<JobPost> optionalJobPost = jobPostInterface.findById(postId);

            if (!optionalJobPost.isPresent()) {
                return "Error!!! Job post with ID " + postId + " not found";
            }

            JobPost jobPost = optionalJobPost.get();

            // Update fields if provided
            if (title != null) {
                jobPost.setTitle(title);
            }
            if (userEmail != null) {
                jobPost.setUserEmail(userEmail);
            }
            if (description != null) {
                jobPost.setDescription(description);
            }
            // Update images if provided
            if (jobImagesData != null) {
                List<String> compressedImagesBase64 = new ArrayList<>();
                for (MultipartFile eachImage : jobImagesData) {
                    if (eachImage != null && !eachImage.isEmpty()) {
                        byte[] compressedImage = compressImage(eachImage.getBytes());
                        String compressedImageBase64 = Base64.getEncoder().encodeToString(compressedImage);
                        compressedImagesBase64.add(compressedImageBase64);
                    }
                }
                jobPost.setImages(compressedImagesBase64);
            }


            JobPost updatedJobPost = jobPostInterface.save(jobPost);

            return (updatedJobPost != null) ? "Job post updated successfully" :
                    "Error!!! Something went wrong... Job post could not be updated";
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            return "Error!!! Something went wrong while processing the request";
        }
    }


    public String deleteJob(Long postId) {
        try {

            Optional<JobPost> optionalJobPost = jobPostInterface.findById(postId);
            if (!optionalJobPost.isPresent()) {
                return "Error!!! Job post with ID " + postId + " not found";
            }

            jobPostInterface.deleteById(postId);

            return "Job post with ID " + postId + " deleted successfully";
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return "Error!!! Something went wrong while deleting the job post";
        }

    }
}