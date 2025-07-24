package com.community_service_hub.user_service.rest;

import com.community_service_hub.user_service.dto.NGOPayload;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.serviceImpl.NGOServiceImpl;
import com.community_service_hub.user_service.util.ImageUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/vi/ngo")
public class NGORest {

    private final NGOServiceImpl ngoService;

    @Autowired
    public NGORest(NGOServiceImpl ngoService) {
        this.ngoService = ngoService;
    }


    @GetMapping
    public ResponseEntity<ResponseDTO> findAll(){
        return ngoService.getNGOs();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> saveNGO(@Valid NGOPayload payload) throws IOException {

        NGO ngo = NGO
                .builder()
                .organizationName(payload.getOrganizationName())
                .city(payload.getCity())
                .logo(payload.getLogo() !=null? ImageUtil.compressImage(payload.getLogo().getBytes()):null)
                .email(payload.getEmail())
                .certificate(payload.getCertificate() !=null? ImageUtil.compressImage(payload.getCertificate().getBytes()) :null)
                .state(payload.getState()!=null? payload.getState() : null)
                .password(payload.getPassword())
                .address(payload.getAddress()!=null? payload.getAddress() : null)
                .description(payload.getDescription()!=null? payload.getDescription() : null)
                .latitude(payload.getLatitude()!=null? payload.getLatitude() : null)
                .longitude(payload.getLongitude()!=null? payload.getLongitude() : null)
                .website(payload.getWebsite()!=null? payload.getWebsite() : null)
                .build();

        return ngoService.saveNGO(ngo);
    }

    @GetMapping("/certificate/{id}")
    public ResponseEntity<?>  getCertificate(@PathVariable UUID id){
        byte[] image = ngoService.getCertificate(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping("/logo/{id}")
    public ResponseEntity<?>  getLogo(@PathVariable UUID id){
        byte[] image = ngoService.getLogo(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

}
