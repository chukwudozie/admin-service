package com.stitch.admin.model.entity;



import com.stitch.admin.model.enums.IdentityType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;


@Data
@Entity
@Table(name = "identity_document")
@Audited
public class IdentityDocument extends BaseEntity {

    @Column(name = "citizenship")
    private String citizenship;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "identity_type")
    private IdentityType identityType;

    @Column(name = "identity_number")
    private String IdentityNumber;

    @Lob
    @Column(name = "document")
    private String document; // base64 image
}
