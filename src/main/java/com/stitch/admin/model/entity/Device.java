package com.stitch.admin.model.entity;

import com.stitch.admin.model.dto.DeviceDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.envers.Audited;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device")
@Audited
public class Device extends BaseEntity {

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "name")
    private String name;

    @Column(name = "model")
    private String model;

    @Column(name = "os")
    private String os;

    public Device(DeviceDto deviceDto){
        this.name = deviceDto.getName();
        this.model = deviceDto.getModel();
        this.os = deviceDto.getOs();
    }
}
