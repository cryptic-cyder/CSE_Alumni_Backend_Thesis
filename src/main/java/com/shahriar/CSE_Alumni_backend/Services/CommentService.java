package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.Comment;
import com.shahriar.CSE_Alumni_backend.Entities.JobPost;
import com.shahriar.CSE_Alumni_backend.Repos.CommentInterface;
import com.shahriar.CSE_Alumni_backend.Repos.JobPostInterface;
import jakarta.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.*;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Service
public class CommentService {


    @Autowired
    private JobPostInterface jobPostInterface = new JobPostInterface() {
        @Override
        public List<JobPost> findByUserEmail(String userEmail) {
            return null;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends JobPost> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends JobPost> List<S> saveAllAndFlush(Iterable<S> entities) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<JobPost> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<Long> longs) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public JobPost getOne(Long aLong) {
            return null;
        }

        @Override
        public JobPost getById(Long aLong) {
            return null;
        }

        @Override
        public JobPost getReferenceById(Long aLong) {
            return null;
        }

        @Override
        public <S extends JobPost> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends JobPost> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public <S extends JobPost> List<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public List<JobPost> findAll() {
            return null;
        }

        @Override
        public List<JobPost> findAllById(Iterable<Long> longs) {
            return null;
        }

        @Override
        public <S extends JobPost> S save(S entity) {
            return null;
        }

        @Override
        public Optional<JobPost> findById(Long aLong) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(Long aLong) {
            return false;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Long aLong) {

        }

        @Override
        public void delete(JobPost entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Long> longs) {

        }

        @Override
        public void deleteAll(Iterable<? extends JobPost> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<JobPost> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<JobPost> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public <S extends JobPost> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends JobPost> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends JobPost> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends JobPost> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends JobPost, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }
    };

    @Autowired
    private CommentInterface commentInterface;

    private File convertMultiPartToFile(MultipartFile file) throws IOException {

        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);

        fos.write(file.getBytes());

        fos.close();

        return convertedFile;
    }

    private byte[] compressFile(File file) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(file.getName());
            zos.putNextEntry(entry);

            byte[] bytes = new byte[1024];
            int length;
            try (FileInputStream fis = new FileInputStream(file)) {
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
            }
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    public String addCommentToJob(Long jobId, String userEmail, String textContent, MultipartFile resume) {

        try {

            String encodedResume=null;

            if(!resume.isEmpty()){

                File file = convertMultiPartToFile(resume);

                byte[] compressedResume = compressFile(file);

                encodedResume = Base64.getEncoder().encodeToString(compressedResume);
            }

            Optional<JobPost> jobPostOptional = jobPostInterface.findById(jobId);

            if(jobPostOptional.isPresent()){

                JobPost jobPost = jobPostOptional.get();

                Comment comment = Comment.builder()

                        .commentedAt(LocalDateTime.now())
                        .commenter(userEmail)
                        .textContent(textContent)
                        .resume(encodedResume)
                        .jobPost(jobPost)

                        .build();

                if(jobPost.getComments()!=null){
                    jobPost.getComments().add(comment);
                }
                else{
                    List<Comment> commentListToAdd = new ArrayList<>();
                    commentListToAdd.add(comment);
                    jobPost.setComments(commentListToAdd);
                }

                commentInterface.save(comment);

                return "Comment is added successfully...";
            }
            else{
                return "No such job post exists";
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error adding comment";
        }
    }



    public List<Comment> findAllCommentOfAnySpecificPost(Long jobId) throws IOException {

        List<Comment> comments = null;
         byte[] decompressedResume=null;

        if(jobPostInterface.findById(jobId)!=null){

            JobPost jobPost = jobPostInterface.findById(jobId).get();

            comments = jobPost.getComments();

            if(comments!=null){

                for(Comment comment: comments){

                    if(comment.getResume()!=null){

                        String encodedResume = comment.getResume();
                        byte[] decodedResume = Base64.getDecoder().decode(encodedResume);
                        decompressedResume = decompress(decodedResume);

                        comment.setDecodedResume(decompressedResume);
                    }
                    else{
                       comment.setDecodedResume(null);
                    }
                }
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }

        return comments;
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
}
