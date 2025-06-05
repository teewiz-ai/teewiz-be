package org.example.tshirtlabbackend.design.data.repository;

import org.example.tshirtlabbackend.design.data.entity.Design;
import org.example.tshirtlabbackend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignRepository extends JpaRepository<Design, Long> {
    Optional<Design> findByIdAndOwner(Long id, User owner);

    Page<Design> findByOwner(User owner, Pageable pageable);
}