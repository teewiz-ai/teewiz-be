package org.example.tshirtlabbackend.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.tshirtlabbackend.design.domain.Design;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_user_google_id", columnNames = "google_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Google account subject (sub) */
    @Column(name = "google_id", nullable = false, updatable = false)
    private String googleId;

    @Column(nullable = false)
    private String email;

    private String name;

    private String pictureUrl;

    /**
     * User → Designs: 1‑to‑many.
     * We own the relationship from the Design side, so no join table.
     */
    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Design> designs = new ArrayList<>();
}