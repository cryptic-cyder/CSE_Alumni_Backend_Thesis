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

//    public boolean verificationPostCreator(Long jobId, String userEmail) {
//
//        Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);
//
//        if (!jobPostOptional.isPresent())
//            return false;
//
//        JobPost specificJobPost = jobPostOptional.get();
//
//        if (userEmail.equals(specificJobPost.getUserEmail()))
//            return true;
//
//        return false;
//    }
//
//    public byte[] decompress(byte[] compressedBytes) throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(compressedBytes))) {
//            ZipEntry entry;
//            while ((entry = zis.getNextEntry()) != null) {
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = zis.read(buffer)) > 0) {
//                    bos.write(buffer, 0, length);
//                }
//            }
//        }
//        return bos.toByteArray(); // Assuming the resume is in string format
//    }

//    private List<byte[]> decodeImages(List<String> listOfBase64VersionOfEachImage) {
//
//        List<byte[]> decodedImages = new ArrayList<>();
//
//        for (String base64VersionOfEachImage : listOfBase64VersionOfEachImage) {
//
//            byte[] decodedImage = Base64.getDecoder().decode(base64VersionOfEachImage);
//            decodedImages.add(decodedImage);
//
//        }
//
//        return decodedImages;
//    }






package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.*;
import com.shahriar.CSE_Alumni_backend.Repos.CommentInterface;
import com.shahriar.CSE_Alumni_backend.Repos.JobPostInterface;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class JobPostService {

    @Autowired
    private JobPostInterface jobPostInterface;

    @Autowired
    private CommentInterface commentInterface;

    public List<JobPost> performSearch(String queryToBeSearched) throws IOException {

        return jobPostInterface.findByDescriptionContaining(queryToBeSearched);
    }


    public String postJob(String title, String userEmail,
                          String company, String vacancy, String location,
                          String requirements, String responsibilities, String salary,
                          List<MultipartFile> jobImagesData) {

        try {
            List<String> compressedImagesBase64 = new ArrayList<>();

            if (jobImagesData == null) {
                compressedImagesBase64 = null;
            }
            else {
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
                    .company(company)
                    .vacancy(vacancy)
                    .location(location)
                    .requirements(requirements)
                    .responsibilities(responsibilities)
                    .salary(salary)
                    .images(compressedImagesBase64)
                    .postedAt(LocalDateTime.now())
                    .build();


            JobPost saveJobPost = jobPostInterface.save(jobpost);

            return "Job is posted successfully";
        }
        catch (IOException e) {

            return "Error!!! Something went wrong while processing the request";
        }
    }

    public byte[] compressImage(byte[] imageData) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(new ByteArrayInputStream(imageData))
                .size(500, 500) // Set desired dimensions
                .outputQuality(0.5) // Set desired quality (0.0 - 1.0)
                .toOutputStream(outputStream); // Write compressed image data to output stream

        return outputStream.toByteArray();
    }


    public List<JobPost> getAllJobPost() throws IOException {

        return jobPostInterface.findAll();
    }


    public JobPost findAnySpecificJob(Long jobId) throws IOException {

        Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);

        return jobPostOptional.orElse(null);
    }


    public List<JobPost> findAllPostOfAnyUser(String userEmail) throws IOException {

        List<JobPost> postListOfAnyUser = jobPostInterface.findByUserEmail(userEmail);

        if (postListOfAnyUser == null) {
            return new ArrayList<>();
        }

        return postListOfAnyUser;
    }


    public String updateJob(Long postId, String title,  String userEmail,
            String company, String vacancy, String location,
                            String requirements, String responsibilities, String salary,
                            List<MultipartFile> jobImagesData) {
        try {

            Optional<JobPost> optionalJobPost = jobPostInterface.findById(postId);

            if (optionalJobPost.isEmpty()) {
                return "Error!!! Job post with ID " + postId + " not found";
            }

            JobPost jobPost = optionalJobPost.get();

            jobPost.setTitle(!title.isEmpty() ? title : jobPost.getTitle());
            jobPost.setUserEmail(!userEmail.isEmpty() ? userEmail : jobPost.getUserEmail());
            jobPost.setCompany(!company.isEmpty() ? company : jobPost.getCompany());
            jobPost.setVacancy(!vacancy.isEmpty() ? vacancy : jobPost.getVacancy());
            jobPost.setLocation(!location.isEmpty() ? location : jobPost.getLocation());
            jobPost.setRequirements(!requirements.isEmpty() ? requirements : jobPost.getRequirements());
            jobPost.setResponsibilities(!responsibilities.isBlank() ? responsibilities : jobPost.getResponsibilities());
            jobPost.setSalary(!salary.isEmpty() ? salary : jobPost.getSalary());


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

            return "Job post updated successfully";
        }
        catch (IOException e) {

            return "Error!!! Something went wrong while processing the request";
        }
    }


    public String deleteJob(Long postId) {
        try {

            Optional<JobPost> optionalJobPost = jobPostInterface.findById(postId);

            if (optionalJobPost.isEmpty()) {
                return "Error!!! Job post with ID " + postId + " not found";
            }

            jobPostInterface.deleteById(postId);

            return "Job post with ID " + postId + " deleted successfully";
        }
        catch (Exception e) {
            return "Error!!! Something went wrong while deleting the job post";
        }

    }

}


