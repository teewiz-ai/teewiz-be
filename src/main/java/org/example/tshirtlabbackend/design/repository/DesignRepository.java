package org.example.tshirtlabbackend.design.repository;

import org.example.tshirtlabbackend.design.domain.Design;
import org.example.tshirtlabbackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignRepository extends JpaRepository<Design, Long> {
    List<Design> findByOwner(User owner);
}
