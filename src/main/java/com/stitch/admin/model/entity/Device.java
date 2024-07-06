package com.stitch.admin.model.entity;

import com.stitch.commons.model.entity.BaseEntity;
import com.stitch.user.model.dto.DeviceDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "device")
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
