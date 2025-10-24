package com.ford.collectionRequirements.service.impl;

import com.ford.collectionRequirements.dto.BasicUserDTO;
import com.ford.collectionRequirements.dto.CreateRequestDTO;
import com.ford.collectionRequirements.dto.RequestCountsDTO;
import com.ford.collectionRequirements.dto.RequestDetailsDTO;
import com.ford.collectionRequirements.repository.RequestRepository;
import com.ford.collectionRequirements.repository.UserInfoRepository;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.RequestService;
import com.ford.collectionRequirements.user.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserInfoRepository userInfoRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserInfoRepository userInfoRepository) {
        this.requestRepository = requestRepository;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public List<RequestDetailsDTO> getAllRequests(Long ldUserId, String status, Integer departmentId, Long eventId, Long requestorId , LocalDate fromDate, LocalDate toDate) {

        if(ldUserId < 21 || ldUserId > 26){
            throw new SecurityException("Unauthorized access. Admin privileges required.");
        }

        Specification<Request> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("requestStatus"), status));
            }
            if(departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("department").get("departmentId"), departmentId));
            }
            if (eventId != null) {
                predicates.add(criteriaBuilder.equal(root.get("event").get("eventId"), eventId));
            }
            if(requestorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("userId"), requestorId));
            }
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("requestDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("requestDate"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };


        List<Request> requests = requestRepository.findAll((spec)); // Use findAll with Specification

        List<RequestDetailsDTO> requestDetailList=new ArrayList<>();
        for(Request request:requests){
            RequestDetailsDTO dto=new RequestDetailsDTO();
            dto.setRequestId(request.getRequestId());
            if (request.getUser() != null) {
                dto.setRequestorId(request.getUser().getUserId());

            }
            if (request.getDepartment() != null) {
                dto.setDepartmentId(request.getDepartment().getDepartmentId());

            }
            if (request.getEvent() != null) {
                dto.setEventId(request.getEvent().getEventId());

            }
            dto.setRequestDate(request.getRequestDate());
            dto.setRequestStatus(request.getRequestStatus());
            dto.setGroupRequest(request.getGroupRequest());
            dto.setJustification(request.getJustification());
            dto.setNoOfParticipants(request.getNoOfParticipants());
            dto.setTanNumber(request.getTAN_Number());
            dto.setCurriculumLink(request.getCurriculamLink());
            requestDetailList.add(dto);
        }

        return requestDetailList;


    }

    @Override
    public RequestCountsDTO getRequestSummaryCounts(Long requestorId) {
        Long total = requestRepository.countByUser_UserId(requestorId);
        Long approved = requestRepository.countByUser_UserIdAndRequestStatus(requestorId, "APPROVED"); // Use actual status string
        Long pending = requestRepository.countByUser_UserIdAndRequestStatus(requestorId, "PENDING");
        Long closed = requestRepository.countByUser_UserIdAndRequestStatus(requestorId, "CLOSED");
        return new RequestCountsDTO(total, approved, pending,closed);
    }


    @Override
    public List<RequestDetailsDTO> getFilteredRequests(
            Long lcUserId,
            String status,
            Integer departmentId,
            Long eventId, // Added eventId parameter
            LocalDate fromDate,
            LocalDate toDate
    ) {
        // Build the Specification dynamically
        Specification<Request> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by the LC user ID
            predicates.add(criteriaBuilder.equal(root.get("user").get("userId"), lcUserId));

            // Apply optional filters
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("requestStatus"), status));
            }
            if(departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("department").get("departmentId"), departmentId));
            }
            if (eventId != null) {
                predicates.add(criteriaBuilder.equal(root.get("event").get("eventId"), eventId));
            }
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("requestDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("requestDate"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<Request> requests = requestRepository.findAll((spec)); // Use findAll with Specification

        return requests.stream()
                .map(request -> {
                    RequestDetailsDTO dto = new RequestDetailsDTO();
                    dto.setRequestId(request.getRequestId());
                    if (request.getUser() != null) {
                        dto.setRequestorId(request.getUser().getUserId());

                    }
                    if (request.getDepartment() != null) {
                        dto.setDepartmentId(request.getDepartment().getDepartmentId());

                    }
                    if (request.getEvent() != null) {
                        dto.setEventId(request.getEvent().getEventId());

                    }
                    dto.setRequestDate(request.getRequestDate());
                    dto.setRequestStatus(request.getRequestStatus());
                    dto.setGroupRequest(request.getGroupRequest());
                    dto.setJustification(request.getJustification());
                    dto.setNoOfParticipants(request.getNoOfParticipants());
                    dto.setTanNumber(request.getTAN_Number());
                    dto.setCurriculumLink(request.getCurriculamLink());
                    return dto;
                })
                .collect(Collectors.toList());
    }



    @Override
    @Transactional // Ensure the operation is transactional
    public RequestDetailsDTO createRequest(CreateRequestDTO createRequestDTO) {
        Request newRequest = new Request();

        // 1. Set Requestor and Department
        UserInfo requestor = userInfoRepository.findById(createRequestDTO.getRequestorId())
                .orElseThrow(() -> new EntityNotFoundException("Requestor not found with ID: " + createRequestDTO.getRequestorId()));
        newRequest.setUser(requestor);
        newRequest.setDepartment(requestor.getDepartment()); // Department is derived from requestor

        // 2. Set auto-populated fields
        newRequest.setRequestDate(LocalDate.now());
        newRequest.setRequestStatus("PENDING"); // Initial status

        // 3. Set fields from DTO
        newRequest.setJustification(createRequestDTO.getJustification());
        newRequest.setGroupRequest(createRequestDTO.getGroupRequest());
        newRequest.setTAN_Number(createRequestDTO.getTanNumber());
        newRequest.setCurriculamLink(createRequestDTO.getCurriculumLink());

        // 4.can  Handle Participants
        if (createRequestDTO.getParticipantUserIds() != null && !createRequestDTO.getParticipantUserIds().isEmpty()) {
            List<UserInfo> participants = userInfoRepository.findAllByUserIdIn(createRequestDTO.getParticipantUserIds());
            if (participants.size() != createRequestDTO.getParticipantUserIds().size()) {
                throw new EntityNotFoundException("One or more participant IDs were not found.");
            }
            newRequest.setRequestedParticipants(participants);
            newRequest.setNoOfParticipants((long) participants.size());
        } else {
            newRequest.setNoOfParticipants(0L); // No participants explicitly requested
            newRequest.setRequestedParticipants(new ArrayList<>());
        }

        // Save the request
        Request savedRequest = requestRepository.save(newRequest);

        // Convert and return as RequestDetailsDTO
        RequestDetailsDTO dto = new RequestDetailsDTO();
        dto.setRequestId(savedRequest.getRequestId());
        dto.setRequestorId(savedRequest.getUser().getUserId());
        dto.setDepartmentId(savedRequest.getDepartment().getDepartmentId());
        // dto.setEventId(savedRequest.getEvent() != null ? savedRequest.getEvent().getEventId() : null); // Event is null for now
        dto.setRequestDate(savedRequest.getRequestDate());
        dto.setRequestStatus(savedRequest.getRequestStatus());
        dto.setGroupRequest(savedRequest.getGroupRequest());
        dto.setJustification(savedRequest.getJustification());
        dto.setNoOfParticipants(savedRequest.getNoOfParticipants());
        dto.setTanNumber(savedRequest.getTAN_Number());
        dto.setCurriculumLink(savedRequest.getCurriculamLink());

        return dto;
    }

    @Override
    public RequestCountsDTO getAllRequestsSummaryCounts(Long requestorId) {

        //Admin ids are 21 to 26 check if requestorId is in this range
        if(requestorId < 21 || requestorId > 26){
            throw new SecurityException("Unauthorized access. Admin privileges required.");
        }

        Long total=requestRepository.count();
        Long approved=requestRepository.countByRequestStatus("APPROVED"); // Use actual status string
        Long pending=requestRepository.countByRequestStatus("PENDING");
        Long closed=requestRepository.countByRequestStatus("CLOSED");
        return new RequestCountsDTO(total,approved,pending,closed);
    }

    @Override
    public List<BasicUserDTO> getAllUsersIdAndName() {

        List<UserInfo> users=userInfoRepository.findAll();

        //from users i have first name and last name i need to concatenate and then send it as username

        return users.stream()
                .map(user -> {
                    BasicUserDTO dto = new BasicUserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setUserName(user.getFirstName() + " " + user.getLastName());
                    dto.setRole(user.getRole());
                    dto.setEmail(user.getEmail());
                    dto.setDepartmentId(user.getDepartment().getDepartmentId());
                    dto.setManagerId(user.getManager().getUserId());
                    dto.setRegionId(user.getRegion().getRegionId());
                    return dto;
                })
                .collect(Collectors.toList());


    }


}

