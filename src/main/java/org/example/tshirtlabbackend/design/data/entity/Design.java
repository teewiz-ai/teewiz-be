package org.example.tshirtlabbackend.design.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.tshirtlabbackend.user.domain.User;

import java.time.Instant;

@Entity
@Table(name = "designs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Design {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_design_user"))
    private User owner;

    /** Object key inside S3 bucket, e.g. "designs/12345.png" */
    @Column(nullable = false)
    private String s3Key;

    /** Public or signed URL that UI can load */
    @Column(nullable = false, length = 2048)
    private String url;

    /** Prompt used to generate this design */
    @Lob
    @Column(nullable = false)
    private String prompt;

    /** Style used to generate this design */
    private String style;

    /** Sample image URLs used for generation */
    private String sampleImageUrls;

    @Builder.Default
    private Instant createdAt = Instant.now();
}