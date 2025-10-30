package com.ford.collectionRequirements.service.impl;

import com.ford.collectionRequirements.dto.BasicUserDTO;
import com.ford.collectionRequirements.dto.CreateRequestDTO;
import com.ford.collectionRequirements.dto.RequestCountsDTO;
import com.ford.collectionRequirements.dto.RequestDetailsDTO;
import com.ford.collectionRequirements.repository.RequestRepository;
import com.ford.collectionRequirements.repository.UserInfoRepository;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.approval.Approval;
import com.ford.collectionRequirements.repository.*;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.RequestService;
import com.ford.collectionRequirements.user.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import com.ford.collectionRequirements.dto.EditRequestDTO;
import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.department.Department;
import com.ford.collectionRequirements.user.UserInfo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private ApprovalRepository approvalRepository;
    private RequestRepository requestRepository;
    private DepartmentRepository departmentRepository;
    private EventRepository eventRepository;
    private UserInfoRepository userInfoRepository;


    @Autowired
    public RequestServiceImpl(ApprovalRepository approvalRepository, RequestRepository requestRepository, DepartmentRepository departmentRepository, EventRepository eventRepository, UserInfoRepository userInfoRepository) {
        this.approvalRepository = approvalRepository;
        this.requestRepository = requestRepository;
        this.departmentRepository = departmentRepository;
        this.eventRepository = eventRepository;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public List<RequestDetailsDTO> getAllRequests(Long ldUserId, String status, String departmentName,String eventName, String requestorName , LocalDate fromDate, LocalDate toDate) {

        if(ldUserId < 21 || ldUserId > 26){
            throw new SecurityException("Unauthorized access. Admin privileges required.");
        }

        Specification<Request> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("requestStatus"), status));
            }
            if(departmentName != null) {
                predicates.add(criteriaBuilder.equal(root.get("department").get("departmentName"), departmentName));
            }
            if (eventName != null) {
                predicates.add(criteriaBuilder.equal(root.get("event").get("eventName"), eventName));
            }
            if (requestorName != null && !requestorName.isEmpty()) {
                String[] nameParts = requestorName.trim().split("\\s+");
                if (nameParts.length == 2) {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("user").get("firstName"), nameParts[0]),
                            criteriaBuilder.equal(root.get("user").get("lastName"), nameParts[1])
                    ));
                } else {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("user").get("firstName"), requestorName),
                            criteriaBuilder.equal(root.get("user").get("lastName"), requestorName)
                    ));
                }
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
            dto.setRequestorName(request.getUser().getFirstName() + " " + request.getUser().getLastName());

        }
        if (request.getDepartment() != null) {
            dto.setDepartmentId(request.getDepartment().getDepartmentId());
            dto.setDepartmentName(request.getDepartment().getDepartmentName());

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
            String departmentName,
            String eventName,
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
            if(departmentName != null) {
                predicates.add(criteriaBuilder.equal(root.get("department").get("departmentName"), departmentName));
            }
            if (eventName != null) {
                predicates.add(criteriaBuilder.equal(root.get("event").get("eventName"), eventName));
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
                        dto.setRequestorName(request.getUser().getFirstName() + " " + request.getUser().getLastName());

                    }
                    if (request.getDepartment() != null) {
                        dto.setDepartmentId(request.getDepartment().getDepartmentId());
                        dto.setDepartmentName(request.getDepartment().getDepartmentName());

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
    public List<BasicUserDTO> getAllUsers() {

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
                    dto.setDepartmentName(user.getDepartment().getDepartmentName());
                    dto.setManagerId(user.getManager().getUserId());
                    dto.setManagerName(user.getManager().getFirstName() + " " + user.getManager().getLastName());
                    dto.setRegionId(user.getRegion().getRegionId());
                    dto.setRegionName(user.getRegion().getRegionName());
                    return dto;
                })
                .collect(Collectors.toList());


    }





    @Override
    @Transactional
    public Request updateRequest( EditRequestDTO editRequestDTO) {

        Request existingRequest = requestRepository.findById(editRequestDTO.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException("Request not found with id: " + editRequestDTO.getRequestId()));
        UserInfo requestor= userInfoRepository.findById(editRequestDTO.getRequestorId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + editRequestDTO.getRequestorId()));

        Department department = departmentRepository.findById(editRequestDTO.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + editRequestDTO.getDepartmentId()));

        if(editRequestDTO.getEventId() != null) {
            Event event = eventRepository.findById(editRequestDTO.getEventId())
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + editRequestDTO.getEventId()));
            existingRequest.setEvent(event);
        }

        existingRequest.setUser(requestor);
        existingRequest.setDepartment(department);
//        existingRequest.setEvent(event);
        existingRequest.setRequestDate(LocalDate.now());
        existingRequest.setRequestStatus(editRequestDTO.getRequestStatus());
        existingRequest.setGroupRequest(editRequestDTO.getGroupRequest());
        existingRequest.setJustification(editRequestDTO.getJustification());
        existingRequest.setTAN_Number(editRequestDTO.getTAN_Number());
        existingRequest.setCurriculamLink(editRequestDTO.getCurriculamLink());
        existingRequest.setNoOfParticipants((long) editRequestDTO.getRequestedParticipants().size());

        java.util.Iterator<UserInfo> iterator = existingRequest.getRequestedParticipants().iterator();
        while (iterator.hasNext()) {
            UserInfo participant = iterator.next();
            if (!editRequestDTO.getRequestedParticipants().contains(participant.getUserId())) {
                iterator.remove();
            }
        }
        for (Long participantId : editRequestDTO.getRequestedParticipants()) {
            UserInfo participant = userInfoRepository.findById(participantId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + participantId));
            if (!existingRequest.getRequestedParticipants().contains(participant)) {
                existingRequest.getRequestedParticipants().add(participant);
            }
        }
        if ("APPROVED".equals(existingRequest.getRequestStatus()) || "CLOSED".equals(existingRequest.getRequestStatus())) {
            Approval approval = approvalRepository.findByRequest_RequestId(existingRequest.getRequestId());
            if (approval == null) {
                approval = new Approval();
                approval.setRequest(existingRequest);
            }
            approval.setApprovalDate(LocalDate.now());

            UserInfo approver=userInfoRepository.findById(editRequestDTO.getApprovedBy())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + editRequestDTO.getApprovedBy()));
            approval.setApprovedBy(approver);

            approval.setApprovalStatus(existingRequest.getRequestStatus());
            approvalRepository.save(approval);
        }

        return requestRepository.save(existingRequest);




    }

    @Override
    @Transactional
    public void deleteRequest(Long requestId) {
//        Approval approval = approvalRepository.findByRequest_RequestId(requestId);
//        if (approval != null) {
//            approvalRepository.delete(approval);
//        }

        if (!requestRepository.existsById(requestId)) {
            throw new EntityNotFoundException("Request not found with id: " + requestId);
        }
        requestRepository.deleteById(requestId);
    }


}

