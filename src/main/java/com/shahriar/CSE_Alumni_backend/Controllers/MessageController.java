package com.shahriar.CSE_Alumni_backend.Controllers;

import com.shahriar.CSE_Alumni_backend.Entities.DetailsMessage;
import com.shahriar.CSE_Alumni_backend.Entities.Message;
import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Entities.Token;
import com.shahriar.CSE_Alumni_backend.Repos.DetailsMessageRepo;
import com.shahriar.CSE_Alumni_backend.Repos.MessageRepository;
import com.shahriar.CSE_Alumni_backend.Repos.RegRepoIF;
import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import com.shahriar.CSE_Alumni_backend.Services.TokenValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

  @Autowired
  private DetailsMessageRepo detailsMessageRepo;

    @Autowired
    private TokenInterface tokenInterface;

    @Autowired
    private RegRepoIF regRepoIF;


    @PostMapping("/messages/fetch")
    public List<DetailsMessage> getMessages() {

        return detailsMessageRepo.findAll();
    }

    @PostMapping("/messages/send")
    public ResponseEntity<?> sendMessage(
            @RequestBody Message messageRequest,
            @RequestHeader("Authorization") String auth) {

        String token = auth.replace("Bearer ", "");
        String messageToBeSend = messageRequest.getContent();

//        System.out.println("\nMessage : " + messageToBeSend);
//        System.out.println("\nToken: " + token+"\n");


        if(new TokenValidation().isTokenValid(token)){

            String[] parts = token.split("_");
            Long id = Long.parseLong(parts[3]);

            Optional<Token> tokenFromDB = tokenInterface.findById(id);
            String emailFromTokenDB = tokenFromDB.get().getEmail();
            String emailFromBrowserToken = new TokenValidation().extractEmailFromToken(token);


            if(emailFromBrowserToken.equals(emailFromTokenDB)){

                Optional<Register> or = regRepoIF.findByEmail(emailFromTokenDB);
                Register r = or.get();
                String messageOwner = r.getName();

                DetailsMessage detailsMessage = new DetailsMessage();

                detailsMessage.setContent(messageToBeSend);
                detailsMessage.setOwner(messageOwner);

                DetailsMessage savedMessage = detailsMessageRepo.save(detailsMessage);

                return new ResponseEntity<>(savedMessage, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }
}
