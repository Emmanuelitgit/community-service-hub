package com.community_service_hub.user_service;

import com.community_service_hub.user_service.repo.NGORepo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NGOControllerTest {

    @Mock
    private NGORepo ngoRepo;
}
