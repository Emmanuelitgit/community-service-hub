package com.community_service_hub.user_service.dto;

import com.community_service_hub.user_service.models.NGO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NGODTO {
    private UUID id;
    private String organizationName;
    private String logo;
    private String state;
    private String city;
    private String website;
    private String socialLinks;
    private String certificate;
    private String email;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;


    public static NGODTO toNGODTO(NGO ngo){
        return NGODTO
                .builder()
                .organizationName(ngo.getOrganizationName())
                .address(ngo.getAddress())
                .logo("http://localhost:8080/api/v1/ngo/logo/"+ngo.getId())
                .certificate("http://localhost:8080/api/v1/ngo/certificate/"+ngo.getId())
                .city(ngo.getCity())
                .build();
    }

    public List<NGODTO> toNGOList(List<NGO> ngos){
        return ngos.stream()
                .map(NGODTO::toNGODTO)
                .collect(Collectors.toList());
    }
}
