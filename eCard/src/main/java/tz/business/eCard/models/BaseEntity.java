package tz.business.eCard.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private  Long id;

    @Column(unique = true)
    private String uuid = UUID.randomUUID().toString() ;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt ;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt ;

    @Basic(optional = true)
    @Column(name = "updated_by")
    private Long createdBy ;

    @Where(clause = "deleted = false")
    @Column(columnDefinition="boolean default false")
    private Boolean deleted = false ;

    @Where(clause = "active = true")
    @Column(columnDefinition = "boolean default true")
    private Boolean active = false ;

}
