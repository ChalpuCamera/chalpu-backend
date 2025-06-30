package com.example.chalpu.photosetting;

import com.example.chalpu.photo.domain.Photo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photo_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;

    private Integer isoValue;
    private String aperture;
    private String shutterSpeed;
    private String whiteBalance;
    private String notes;
} 