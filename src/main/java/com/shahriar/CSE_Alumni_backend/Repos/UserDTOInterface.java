package com.shahriar.CSE_Alumni_backend.Repos;

import com.shahriar.CSE_Alumni_backend.Entities.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDTOInterface extends JpaRepository<UserDTO, Long> {


}
