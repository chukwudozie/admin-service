package com.stitch.admin.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;


@Data
@MappedSuperclass
public abstract class BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    protected Long id;

    @Version
    @Column(name = "version")
    private long version;

    @CreationTimestamp()
    @Column(name = "date_created")
    protected Instant dateCreated;

    @UpdateTimestamp
    @Column(name = "last_updated")
    protected Instant lastUpdated;

    @Column(name = "created_by")
    protected String createdBy;

    @Column(name = "modified_by")
    protected String modifiedBy;


}
