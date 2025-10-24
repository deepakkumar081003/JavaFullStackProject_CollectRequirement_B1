package com.ford.collectionRequirements.service.impl;

import com.ford.collectionRequirements.approval.Approval;
import com.ford.collectionRequirements.repository.*;
import com.ford.collectionRequirements.request.Request;
import com.ford.collectionRequirements.service.RequestService;
import com.ford.collectionRequirements.dto.EditRequestDTO;
import com.ford.collectionRequirements.event.Event;
import com.ford.collectionRequirements.department.Department;
import com.ford.collectionRequirements.user.UserInfo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;

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
        if ("Approved".equals(existingRequest.getRequestStatus()) || "Rejected".equals(existingRequest.getRequestStatus())) {
            Approval approval = approvalRepository.findByRequest_RequestId(existingRequest.getRequestId());
            if (approval == null) {
                approval = new Approval();
                approval.setRequest(existingRequest);
            }
            approval.setApprovalDate(LocalDate.now());
            approval.setApprovedBy(requestor);
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
