package com.ford.collectionRequirements.service;

import com.ford.collectionRequirements.dto.EditRequestDTO;
import com.ford.collectionRequirements.request.Request;

public interface RequestService {
    Request updateRequest( EditRequestDTO editRequestDTO);
    void deleteRequest(Long requestId);
}
