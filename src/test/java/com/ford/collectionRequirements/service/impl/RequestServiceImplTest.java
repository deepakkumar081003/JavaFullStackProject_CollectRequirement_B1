package com.ford.collectionRequirements.service.impl;

import com.ford.collectionRequirements.entity.Approval;
import com.ford.collectionRequirements.entity.Department;
import com.ford.collectionRequirements.dto.*;
import com.ford.collectionRequirements.entity.Event;
import com.ford.collectionRequirements.exception.RequestNotFoundException;
import com.ford.collectionRequirements.exception.UnauthorizedActionException;
import com.ford.collectionRequirements.entity.Region;
import com.ford.collectionRequirements.repository.*;
import com.ford.collectionRequirements.entity.Request;
import com.ford.collectionRequirements.entity.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private ApprovalRepository approvalRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserInfoRepository userInfoRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private UserInfo adminUser;
    private UserInfo requestorUser;
    private UserInfo participantUser1;
    private UserInfo participantUser2;
    private UserInfo managerUser;
    private Department department;
    private Event event;
    private Region region;
    private Request sampleRequest;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setRegionId(1L);
        region.setRegionName("North America");

        department = new Department();
        department.setDepartmentId(1); // User's change: int
        department.setDepartmentName("IT");

        managerUser = new UserInfo();
        managerUser.setUserId(50L);
        managerUser.setFirstName("Manager");
        managerUser.setLastName("User");
        managerUser.setRole("MANAGER");
        managerUser.setEmail("manager.user@ford.com");
        managerUser.setDepartment(department);
        managerUser.setRegion(region);
        // Manager doesn't have a manager in this simplified setup

        adminUser = new UserInfo();
        adminUser.setUserId(22L); // Admin ID
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setDepartment(department);
        adminUser.setRegion(region);
        adminUser.setManager(managerUser);

        requestorUser = new UserInfo();
        requestorUser.setUserId(1L);
        requestorUser.setFirstName("John");
        requestorUser.setLastName("Doe");
        requestorUser.setRole("EMPLOYEE");
        requestorUser.setEmail("john.doe@ford.com");
        requestorUser.setDepartment(department);
        requestorUser.setRegion(region);
        requestorUser.setManager(managerUser);

        participantUser1 = new UserInfo();
        participantUser1.setUserId(2L);
        participantUser1.setFirstName("Jane");
        participantUser1.setLastName("Smith");
        participantUser1.setRole("EMPLOYEE");
        participantUser1.setEmail("jane.smith@ford.com");
        participantUser1.setDepartment(department);
        participantUser1.setRegion(region);
        participantUser1.setManager(managerUser);

        participantUser2 = new UserInfo();
        participantUser2.setUserId(3L);
        participantUser2.setFirstName("Peter");
        participantUser2.setLastName("Jones");
        participantUser2.setRole("EMPLOYEE");
        participantUser2.setEmail("peter.jones@ford.com");
        participantUser2.setDepartment(department);
        participantUser2.setRegion(region);
        participantUser2.setManager(managerUser);

        event = new Event();
        event.setEventId(101L);
        event.setEventName("Spring Boot Workshop");

        sampleRequest = new Request();
        sampleRequest.setRequestId(100L);
        sampleRequest.setUser(requestorUser);
        sampleRequest.setDepartment(department);
        sampleRequest.setEvent(event);
        sampleRequest.setRequestDate(LocalDate.of(2023, 1, 15));
        sampleRequest.setRequestStatus("PENDING");
        sampleRequest.setGroupRequest(true);
        sampleRequest.setJustification("Need training for new project.");
        sampleRequest.setNoOfParticipants(2L);
        sampleRequest.setTAN_Number("TAN001");
        sampleRequest.setCurriculamLink("http://example.com/curriculum");
        // User's change: List.of, but make it mutable for update tests
        sampleRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser1, participantUser2)));



    }

    // Helper to create a mutable Request object for update tests
    private Request createMutableRequest(Long requestId, UserInfo user, Department department, Event event, String status, List<UserInfo> participants) {
        Request req = new Request();
        req.setRequestId(requestId);
        req.setUser(user);
        req.setDepartment(department);
        req.setEvent(event);
        req.setRequestDate(LocalDate.of(2023, 1, 15));
        req.setRequestStatus(status);
        req.setGroupRequest(true);
        req.setJustification("Original justification.");
        req.setTAN_Number("TAN001");
        req.setCurriculamLink("http://example.com/original_link");
        // Ensure this is a mutable collection for testing purposes
        req.setRequestedParticipants(new ArrayList<>(participants)); // Use ArrayList here
        req.setNoOfParticipants((long) participants.size());
        return req;
    }

    @Test
    void getAllRequests_AdminUser_WithAllFilters_ReturnsFilteredList() throws UnauthorizedActionException {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(101L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 2, 1));
        filteredRequest.setRequestStatus("APPROVED");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Approved training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN002");
        filteredRequest.setCurriculamLink("http://example.com/approved");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser1))); // Ensure mutable

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getAllRequests(
                adminUser.getUserId(),
                "APPROVED",
                "IT",
                "Spring Boot Workshop",
                "John Doe",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 3, 1)
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestId()).isEqualTo(101L);
        assertThat(result.get(0).getRequestorName()).isEqualTo("John Doe");
        assertThat(result.get(0).getRequestStatus()).isEqualTo("APPROVED");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllRequests_AdminUser_WithPartialRequestorName_ReturnsFilteredList() throws UnauthorizedActionException {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(101L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 2, 1));
        filteredRequest.setRequestStatus("APPROVED");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Approved training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN002");
        filteredRequest.setCurriculamLink("http://example.com/approved");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser1))); // Ensure mutable

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getAllRequests(
                adminUser.getUserId(),
                null, null, null,
                "John", // Partial name to hit the 'else' branch in requestorName predicate
                null, null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestorName()).isEqualTo("John Doe");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllRequests_AdminUser_NoFilters_ReturnsAllRequests() throws UnauthorizedActionException {
        // Arrange
        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(sampleRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getAllRequests(adminUser.getUserId(), null, null, null, null, null, null);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestId()).isEqualTo(sampleRequest.getRequestId());
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllRequests_NonAdminUser_ThrowsUnauthorizedActionException() {
        // Arrange
        Long nonAdminUserId = 10L; // Outside the admin range

        // Act & Assert
        UnauthorizedActionException exception = assertThrows(UnauthorizedActionException.class, () ->
                requestService.getAllRequests(nonAdminUserId, null, null, null, null, null, null));

        assertThat(exception.getMessage()).isEqualTo("Unauthorized access. Admin privileges required.");
        verifyNoInteractions(requestRepository); // Ensure no repository calls are made
    }

    @Test
    void getAllRequestsSummaryCounts_AdminUser_ReturnsCounts() throws UnauthorizedActionException {
        // Arrange
        when(requestRepository.count()).thenReturn(10L);
        when(requestRepository.countByRequestStatus("APPROVED")).thenReturn(3L);
        when(requestRepository.countByRequestStatus("PENDING")).thenReturn(5L);
        when(requestRepository.countByRequestStatus("REJECTED")).thenReturn(2L);

        // Act
        RequestCountsDTO result = requestService.getAllRequestsSummaryCounts(adminUser.getUserId());

        // Assert
        assertThat(result.getTotalSubmitted()).isEqualTo(10L);
        assertThat(result.getApproved()).isEqualTo(3L);
        assertThat(result.getPending()).isEqualTo(5L);
        assertThat(result.getClosed()).isEqualTo(2L);
        verify(requestRepository).count();
        verify(requestRepository).countByRequestStatus("APPROVED");
        verify(requestRepository).countByRequestStatus("PENDING");
        verify(requestRepository).countByRequestStatus("REJECTED");
    }

    @Test
    void getAllRequestsSummaryCounts_NonAdminUser_ThrowsUnauthorizedActionException() {
        // Arrange
        Long nonAdminUserId = 1L;

        // Act & Assert
        UnauthorizedActionException exception = assertThrows(UnauthorizedActionException.class, () ->
                requestService.getAllRequestsSummaryCounts(nonAdminUserId));

        assertThat(exception.getMessage()).isEqualTo("Unauthorized access. Admin privileges required.");
        verifyNoInteractions(requestRepository);
    }

    @Test
    void getAllRequestSummaryCounts_NonAdminUserGreaterThan26_ThrowsException() {
        // Arrange
        Long nonAdminUserId = 30L;

        // Act & Assert
        UnauthorizedActionException exception = assertThrows(UnauthorizedActionException.class, () ->
                requestService.getAllRequestsSummaryCounts(nonAdminUserId));

        assertThat(exception.getMessage()).isEqualTo("Unauthorized access. Admin privileges required.");
        verifyNoInteractions(requestRepository);

    }

    @Test
    void getRequestDetails_ExistingRequest_ReturnsDTOWithParticipants() throws RequestNotFoundException {
        // Arrange
        when(requestRepository.findById(sampleRequest.getRequestId())).thenReturn(Optional.of(sampleRequest));

        // Act
        RequestDetailsDTO result = requestService.getRequestDetails(sampleRequest.getRequestId());

        // Assert
        assertThat(result.getRequestId()).isEqualTo(sampleRequest.getRequestId());
        assertThat(result.getRequestorId()).isEqualTo(requestorUser.getUserId());
        assertThat(result.getRequestorName()).isEqualTo("John Doe");
        assertThat(result.getDepartmentName()).isEqualTo("IT");
        assertThat(result.getEventName()).isEqualTo("Spring Boot Workshop");
        assertThat(result.getRequestStatus()).isEqualTo("PENDING");
        assertThat(result.getNoOfParticipants()).isEqualTo(2L);
        assertThat(result.getParticipants()).hasSize(2);
        assertThat(result.getParticipants().stream().map(BasicUserDTO::getUserId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(participantUser1.getUserId(), participantUser2.getUserId());
        assertThat(result.getParticipants().get(0).getManagerName()).isEqualTo("Manager User");
        verify(requestRepository).findById(sampleRequest.getRequestId());
    }

    @Test
    void getRequestDetails_RequestWithNullEvent_ReturnsDTO() throws RequestNotFoundException {
        // Arrange
        Request requestWithNullEvent = createMutableRequest(
                105L, requestorUser, department, null, "PENDING", new ArrayList<>()
        );
        requestWithNullEvent.setJustification("No event specified.");
        requestWithNullEvent.setTAN_Number("TAN006");
        requestWithNullEvent.setCurriculamLink("link");

        when(requestRepository.findById(requestWithNullEvent.getRequestId())).thenReturn(Optional.of(requestWithNullEvent));

        // Act
        RequestDetailsDTO dto = requestService.getRequestDetails(requestWithNullEvent.getRequestId());

        // Assert
        assertThat(dto.getRequestId()).isEqualTo(105L);
        assertThat(dto.getEventId()).isNull();
        assertThat(dto.getEventName()).isNull();
        verify(requestRepository).findById(requestWithNullEvent.getRequestId());
    }

    @Test
    void getRequestDetails_RequestNotFound_ThrowsRequestNotFoundException() {
        // Arrange
        Long nonExistentRequestId = 999L;
        when(requestRepository.findById(nonExistentRequestId)).thenReturn(Optional.empty());

        // Act & Assert
        RequestNotFoundException exception = assertThrows(RequestNotFoundException.class, () ->
                requestService.getRequestDetails(nonExistentRequestId));

        assertThat(exception.getMessage()).isEqualTo("Request not found with id: " + nonExistentRequestId);
        verify(requestRepository).findById(nonExistentRequestId);
    }

    @Test
    void getFilteredRequests_LCUser_WithAllFilters_ReturnsFilteredList() {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(102L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 3, 10));
        filteredRequest.setRequestStatus("REJECTED");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Rejected training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN003");
        filteredRequest.setCurriculamLink("http://example.com/rejected");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser2))); // Ensure mutable

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getFilteredRequests(
                requestorUser.getUserId(),
                "REJECTED",
                "IT",
                "Spring Boot Workshop",
                LocalDate.of(2023, 3, 1),
                LocalDate.of(2023, 3, 31)
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestId()).isEqualTo(102L);
        assertThat(result.get(0).getRequestorId()).isEqualTo(requestorUser.getUserId());
        assertThat(result.get(0).getRequestStatus()).isEqualTo("REJECTED");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getFilteredRequests_LCUser_NoFilters_ReturnsAllRequestsForUser() {
        // Arrange
        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(sampleRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getFilteredRequests(requestorUser.getUserId(), null, null, null, null, null);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestorId()).isEqualTo(requestorUser.getUserId());
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getRequestSummaryCounts_LCUser_ReturnsCounts() {
        // Arrange
        when(requestRepository.countByUser_UserId(requestorUser.getUserId())).thenReturn(5L);
        when(requestRepository.countByUser_UserIdAndRequestStatus(requestorUser.getUserId(), "APPROVED")).thenReturn(1L);
        when(requestRepository.countByUser_UserIdAndRequestStatus(requestorUser.getUserId(), "PENDING")).thenReturn(3L);
        when(requestRepository.countByUser_UserIdAndRequestStatus(requestorUser.getUserId(), "REJECTED")).thenReturn(1L);

        // Act
        RequestCountsDTO result = requestService.getRequestSummaryCounts(requestorUser.getUserId());

        // Assert
        assertThat(result.getTotalSubmitted()).isEqualTo(5L);
        assertThat(result.getApproved()).isEqualTo(1L);
        assertThat(result.getPending()).isEqualTo(3L);
        assertThat(result.getClosed()).isEqualTo(1L);
        verify(requestRepository).countByUser_UserId(requestorUser.getUserId());
        verify(requestRepository).countByUser_UserIdAndRequestStatus(requestorUser.getUserId(), "APPROVED");
        verify(requestRepository).countByUser_UserIdAndRequestStatus(requestorUser.getUserId(), "PENDING");
        verify(requestRepository).countByUser_UserIdAndRequestStatus(requestorUser.getUserId(), "REJECTED");
    }

    @Test
    void createRequest_WithParticipants_Success() {
        // Arrange
        CreateRequestDTO createRequestDTO = new CreateRequestDTO();
        createRequestDTO.setRequestorId(requestorUser.getUserId());
        createRequestDTO.setJustification("New training for team.");
        createRequestDTO.setGroupRequest(true);
        createRequestDTO.setTanNumber("TAN004");
        createRequestDTO.setCurriculumLink("http://example.com/new_training");
        createRequestDTO.setParticipantUserIds(Arrays.asList(participantUser1.getUserId(), participantUser2.getUserId()));

        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(userInfoRepository.findAllByUserIdIn(anyList())).thenReturn(Arrays.asList(participantUser1, participantUser2));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> {
            Request savedRequest = invocation.getArgument(0);
            savedRequest.setRequestId(103L); // Simulate ID generation
            return savedRequest;
        });

        // Act
        RequestDetailsDTO result = requestService.createRequest(createRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(103L);
        assertThat(result.getRequestorId()).isEqualTo(requestorUser.getUserId());
        assertThat(result.getRequestStatus()).isEqualTo("PENDING");
        assertThat(result.getNoOfParticipants()).isEqualTo(2L);
        assertThat(result.getDepartmentId()).isEqualTo(department.getDepartmentId());
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(userInfoRepository).findAllByUserIdIn(anyList());
        verify(requestRepository).save(any(Request.class));

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository).save(requestCaptor.capture());
        Request savedRequest = requestCaptor.getValue();
        assertThat(savedRequest.getRequestedParticipants()).hasSize(2);
        assertThat(savedRequest.getRequestedParticipants().stream().map(UserInfo::getUserId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(participantUser1.getUserId(), participantUser2.getUserId());
        assertThat(savedRequest.getRequestDate()).isEqualTo(LocalDate.now());
    }


    @Test
    void createRequest_RequestorNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        CreateRequestDTO createRequestDTO = new CreateRequestDTO();
        createRequestDTO.setRequestorId(999L);
        when(userInfoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                requestService.createRequest(createRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("Requestor not found with ID: " + 999L);
        verify(userInfoRepository).findById(999L);
        verifyNoInteractions(requestRepository);
    }

    @Test
    void createRequest_OneOfParticipantsNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        CreateRequestDTO createRequestDTO = new CreateRequestDTO();
        createRequestDTO.setRequestorId(requestorUser.getUserId());
        createRequestDTO.setParticipantUserIds(Arrays.asList(participantUser1.getUserId(), 999L)); // One invalid ID

        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        // Simulate only one participant found when two were requested
        when(userInfoRepository.findAllByUserIdIn(anyList())).thenReturn(List.of(participantUser1));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                requestService.createRequest(createRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("One or more participant IDs were not found.");
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(userInfoRepository).findAllByUserIdIn(anyList());
        verifyNoInteractions(requestRepository);
    }

    @Test
    void getAllUsers_ReturnsListOfBasicUserDTOs() {
        // Arrange
        UserInfo user2 = new UserInfo();
        user2.setUserId(4L);
        user2.setFirstName("Alice");
        user2.setLastName("Brown");
        user2.setRole("EMPLOYEE");
        user2.setEmail("alice.brown@ford.com");
        user2.setDepartment(department);
        user2.setRegion(region);
        user2.setManager(managerUser);

        when(userInfoRepository.findAll()).thenReturn(Arrays.asList(requestorUser, participantUser1, user2));

        // Act
        List<BasicUserDTO> result = requestService.getAllUsers();

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result.stream().map(BasicUserDTO::getUserId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(requestorUser.getUserId(), participantUser1.getUserId(), user2.getUserId());
        BasicUserDTO firstUser = result.stream().filter(u -> u.getUserId().equals(requestorUser.getUserId())).findFirst().orElseThrow();
        assertThat(firstUser.getUserName()).isEqualTo("John Doe");
        assertThat(firstUser.getEmail()).isEqualTo("john.doe@ford.com");
        assertThat(firstUser.getRole()).isEqualTo("EMPLOYEE");
        assertThat(firstUser.getDepartmentName()).isEqualTo("IT");
        assertThat(firstUser.getManagerName()).isEqualTo("Manager User");
        assertThat(firstUser.getRegionName()).isEqualTo("North America");
        verify(userInfoRepository).findAll();
    }

    @Test
    void updateRequest_Success_WithEventAndParticipantChanges() throws RequestNotFoundException {
        // Arrange
        UserInfo newParticipant = new UserInfo();
        newParticipant.setUserId(4L);
        newParticipant.setFirstName("New");
        newParticipant.setLastName("Participant");
        newParticipant.setDepartment(department);
        newParticipant.setManager(managerUser);
        newParticipant.setRegion(region);

        // Use helper to create a mutable existingRequest
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, null, "PENDING", new ArrayList<>(List.of(participantUser1, participantUser2))
        );

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf
        editRequestDTO.setEventId(event.getEventId()); // Add new event
        editRequestDTO.setRequestDate(LocalDate.of(2023, 1, 20));
        editRequestDTO.setRequestStatus("PENDING"); // Status remains PENDING
        editRequestDTO.setGroupRequest(true);
        editRequestDTO.setJustification("Updated justification.");
        editRequestDTO.setTAN_Number("TAN001_UPDATED");
        editRequestDTO.setCurriculamLink("http://example.com/updated_link");
        editRequestDTO.setRequestedParticipants(Arrays.asList(participantUser1.getUserId(), newParticipant.getUserId())); // Change participants

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(Long.valueOf(department.getDepartmentId()))).thenReturn(Optional.of(department)); // User's change: Long.valueOf
        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        when(userInfoRepository.findById(participantUser1.getUserId())).thenReturn(Optional.of(participantUser1));
        when(userInfoRepository.findById(newParticipant.getUserId())).thenReturn(Optional.of(newParticipant));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the modified request

        // Act
        Request result = requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(existingRequest.getRequestId());
        assertThat(result.getJustification()).isEqualTo("Updated justification.");
        assertThat(result.getTAN_Number()).isEqualTo("TAN001_UPDATED");
        assertThat(result.getCurriculamLink()).isEqualTo("http://example.com/updated_link");
        assertThat(result.getEvent()).isEqualTo(event);
        assertThat(result.getNoOfParticipants()).isEqualTo(2L);
        assertThat(result.getRequestedParticipants()).hasSize(2);
        assertThat(result.getRequestedParticipants().stream().map(UserInfo::getUserId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(participantUser1.getUserId(), newParticipant.getUserId());
        verify(requestRepository).findById(existingRequest.getRequestId());
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(departmentRepository).findById(Long.valueOf(department.getDepartmentId()));
        verify(eventRepository).findById(event.getEventId());
        verify(userInfoRepository).findById(participantUser1.getUserId());
        verify(userInfoRepository).findById(newParticipant.getUserId());
        verify(requestRepository).save(any(Request.class));
        verifyNoInteractions(approvalRepository); // No approval processing for PENDING status
    }

    @Test
    void updateRequest_Throws_NoEventInDTO_RemovesExistingEvent() {
        // Arrange
        // Existing request has an event
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, event, "PENDING", new ArrayList<>(List.of(participantUser1))
        );

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf
        editRequestDTO.setEventId(null); // No event in DTO, should remove existing
        editRequestDTO.setRequestDate(LocalDate.of(2023, 1, 20));
        editRequestDTO.setRequestStatus("PENDING");
        editRequestDTO.setGroupRequest(true);
        editRequestDTO.setJustification("Updated justification, removed event.");
        editRequestDTO.setTAN_Number("TAN001_UPDATED");
        editRequestDTO.setCurriculamLink("http://example.com/updated_link");
        editRequestDTO.setRequestedParticipants(new ArrayList<>(List.of(participantUser1.getUserId()))); // No change in participants

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(Long.valueOf(department.getDepartmentId()))).thenReturn(Optional.of(department)); // User's change: Long.valueOf

        //Act and assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("User not found with id: "+ participantUser1.getUserId());
    }

    @Test
    void updateRequest_Success_NoParticipantChanges() throws RequestNotFoundException {
        // Arrange
        // Existing request has participantUser1
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, event, "PENDING", new ArrayList<>(List.of(participantUser1))
        );

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf
        editRequestDTO.setEventId(event.getEventId());
        editRequestDTO.setRequestDate(LocalDate.of(2023, 1, 20));
        editRequestDTO.setRequestStatus("PENDING");
        editRequestDTO.setGroupRequest(true);
        editRequestDTO.setJustification("Updated justification.");
        editRequestDTO.setTAN_Number("TAN001_UPDATED");
        editRequestDTO.setCurriculamLink("http://example.com/updated_link");
        editRequestDTO.setRequestedParticipants(new ArrayList<>(List.of(participantUser1.getUserId()))); // Same participant, should not re-add

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(Long.valueOf(department.getDepartmentId()))).thenReturn(Optional.of(department)); // User's change: Long.valueOf
        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        when(userInfoRepository.findById(participantUser1.getUserId())).thenReturn(Optional.of(participantUser1)); // Participant is looked up
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Request result = requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNoOfParticipants()).isEqualTo(1L);
        assertThat(result.getRequestedParticipants()).hasSize(1);
        assertThat(result.getRequestedParticipants().get(0).getUserId()).isEqualTo(participantUser1.getUserId());
        verify(requestRepository).findById(existingRequest.getRequestId());
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(departmentRepository).findById(Long.valueOf(department.getDepartmentId()));
        verify(eventRepository).findById(event.getEventId());
        verify(userInfoRepository, times(1)).findById(participantUser1.getUserId()); // Only once for existing participant
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void updateRequest_Success_StatusApproved_CreatesApproval() throws RequestNotFoundException {
        // Arrange
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, null, "PENDING", new ArrayList<>()
        );

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf
        editRequestDTO.setRequestStatus("APPROVED"); // Change status to APPROVED
        editRequestDTO.setApprovalNotes("Approved by manager.");
        editRequestDTO.setApprovedBy(managerUser.getUserId());
        editRequestDTO.setRequestedParticipants(new ArrayList<>()); // Empty for simplicity

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(Long.valueOf(department.getDepartmentId()))).thenReturn(Optional.of(department)); // User's change: Long.valueOf
        when(userInfoRepository.findById(managerUser.getUserId())).thenReturn(Optional.of(managerUser));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(approvalRepository.findByRequest_RequestId(existingRequest.getRequestId())).thenReturn(null); // No existing approval

        // Act
        Request result = requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO);

        // Assert
        assertThat(result.getRequestStatus()).isEqualTo("APPROVED");
        verify(approvalRepository).findByRequest_RequestId(existingRequest.getRequestId());
        verify(userInfoRepository).findById(managerUser.getUserId());

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        verify(approvalRepository).save(approvalCaptor.capture());
        Approval savedApproval = approvalCaptor.getValue();
        assertThat(savedApproval.getRequest()).isEqualTo(existingRequest);
        assertThat(savedApproval.getApprovalStatus()).isEqualTo("APPROVED");
        assertThat(savedApproval.getApprovalNotes()).isEqualTo("Approved by manager.");
        assertThat(savedApproval.getApprovedBy()).isEqualTo(managerUser);
        assertThat(savedApproval.getApprovalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void updateRequest_Success_StatusRejected_UpdatesExistingApproval() throws RequestNotFoundException {
        // Arrange
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, null, "PENDING", new ArrayList<>()
        );

        Approval existingApproval = new Approval();
        existingApproval.setApprovalId(1L);
        existingApproval.setRequest(existingRequest);
        existingApproval.setApprovalStatus("PENDING");
        existingApproval.setApprovalNotes("Initial review.");
        existingApproval.setApprovedBy(managerUser);
        existingApproval.setApprovalDate(LocalDate.of(2023, 1, 1));


        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf
        editRequestDTO.setRequestStatus("REJECTED"); // Change status to REJECTED
        editRequestDTO.setApprovalNotes("Rejected due to budget.");
        editRequestDTO.setApprovedBy(managerUser.getUserId());
        editRequestDTO.setRequestedParticipants(new ArrayList<>());

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(Long.valueOf(department.getDepartmentId()))).thenReturn(Optional.of(department)); // User's change: Long.valueOf
        when(userInfoRepository.findById(managerUser.getUserId())).thenReturn(Optional.of(managerUser));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(approvalRepository.findByRequest_RequestId(existingRequest.getRequestId())).thenReturn(existingApproval);

        // Act
        Request result = requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO);

        // Assert
        assertThat(result.getRequestStatus()).isEqualTo("REJECTED");
        verify(approvalRepository).findByRequest_RequestId(existingRequest.getRequestId());
        verify(userInfoRepository).findById(managerUser.getUserId());

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        verify(approvalRepository).save(approvalCaptor.capture());
        Approval savedApproval = approvalCaptor.getValue();
        assertThat(savedApproval.getApprovalId()).isEqualTo(1L); // Ensure it's the same approval object
        assertThat(savedApproval.getApprovalStatus()).isEqualTo("REJECTED");
        assertThat(savedApproval.getApprovalNotes()).isEqualTo("Rejected due to budget.");
        assertThat(savedApproval.getApprovedBy()).isEqualTo(managerUser);
        assertThat(savedApproval.getApprovalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void updateRequest_RequestNotFound_ThrowsRequestNotFoundException() {
        // Arrange
        Long nonExistentRequestId = 999L;
        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(nonExistentRequestId);
        when(requestRepository.findById(nonExistentRequestId)).thenReturn(Optional.empty());

        // Act & Assert
        RequestNotFoundException exception = assertThrows(RequestNotFoundException.class, () ->
                requestService.updateRequest(nonExistentRequestId, editRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("Request not found with id: " + nonExistentRequestId);
        verify(requestRepository).findById(nonExistentRequestId);
        verifyNoInteractions(userInfoRepository, departmentRepository, eventRepository, approvalRepository);
    }

    @Test
    void updateRequest_RequestorNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, null, "PENDING", new ArrayList<>()
        );

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(999L); // Invalid requestor ID
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("User not found with id: " + 999L);
        verify(requestRepository).findById(existingRequest.getRequestId());
        verify(userInfoRepository).findById(999L);
        verifyNoInteractions(departmentRepository, eventRepository, approvalRepository);
    }

    @Test
    void updateRequest_DepartmentNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, null, "PENDING", new ArrayList<>()
        );

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(999L); // Invalid department ID

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("Department not found with id: " + 999L);
        verify(requestRepository).findById(existingRequest.getRequestId());
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(departmentRepository).findById(999L);
        verifyNoInteractions(eventRepository, approvalRepository);
    }

    @Test
    void updateRequest_EventNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, null, "PENDING", new ArrayList<>()
        );

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf
        editRequestDTO.setEventId(999L); // Invalid event ID

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(Long.valueOf(department.getDepartmentId()))).thenReturn(Optional.of(department)); // User's change: Long.valueOf
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("Event not found with id: " + 999L);
        verify(requestRepository).findById(existingRequest.getRequestId());
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(departmentRepository).findById(Long.valueOf(department.getDepartmentId()));
        verify(eventRepository).findById(999L);
        verifyNoInteractions(approvalRepository);
    }

    @Test
    void updateRequest_ParticipantNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long invalidParticipantId = 999L;
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, event, "PENDING", new ArrayList<>(List.of(participantUser1))
        );

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf
        editRequestDTO.setRequestedParticipants(Arrays.asList(participantUser1.getUserId(), invalidParticipantId)); // One invalid participant

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(Long.valueOf(department.getDepartmentId()))).thenReturn(Optional.of(department)); // User's change: Long.valueOf
        when(userInfoRepository.findById(participantUser1.getUserId())).thenReturn(Optional.of(participantUser1));
        when(userInfoRepository.findById(invalidParticipantId)).thenReturn(Optional.empty()); // This is the trigger

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> // Changed from RequestNotFoundException
                requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("User not found with id: " + invalidParticipantId);
        verify(requestRepository).findById(existingRequest.getRequestId());
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(departmentRepository).findById(Long.valueOf(department.getDepartmentId()));
        verify(userInfoRepository).findById(invalidParticipantId);
        verifyNoMoreInteractions(eventRepository, approvalRepository);
    }

    @Test
    void updateRequest_ApprovedByNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long invalidApproverId = 999L;
        Request existingRequest = createMutableRequest(
                sampleRequest.getRequestId(), requestorUser, department, event, "PENDING", new ArrayList<>(List.of(participantUser1))
        );
        existingRequest.setRequestStatus("APPROVED"); // Set status to trigger approval logic

        EditRequestDTO editRequestDTO = new EditRequestDTO();
        editRequestDTO.setRequestId(existingRequest.getRequestId());
        editRequestDTO.setRequestorId(requestorUser.getUserId());
        editRequestDTO.setDepartmentId(Long.valueOf(department.getDepartmentId())); // User's change: Long.valueOf
        editRequestDTO.setRequestStatus("APPROVED");
        editRequestDTO.setApprovedBy(invalidApproverId); // Invalid approver ID
        editRequestDTO.setRequestedParticipants(new ArrayList<>());

        when(requestRepository.findById(existingRequest.getRequestId())).thenReturn(Optional.of(existingRequest));
        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(departmentRepository.findById(Long.valueOf(department.getDepartmentId()))).thenReturn(Optional.of(department)); // User's change: Long.valueOf
        when(approvalRepository.findByRequest_RequestId(existingRequest.getRequestId())).thenReturn(null);
        when(userInfoRepository.findById(invalidApproverId)).thenReturn(Optional.empty()); // This is the trigger

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                requestService.updateRequest(existingRequest.getRequestId(), editRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("User not found with id: " + invalidApproverId);
        verify(requestRepository).findById(existingRequest.getRequestId());
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(departmentRepository).findById(Long.valueOf(department.getDepartmentId()));
        verify(approvalRepository).findByRequest_RequestId(existingRequest.getRequestId());
        verify(userInfoRepository).findById(invalidApproverId);
        verifyNoMoreInteractions(eventRepository);
    }

    @Test
    void deleteRequest_Success() throws RequestNotFoundException {
        // Arrange
        Long requestIdToDelete = 100L;
        when(requestRepository.existsById(requestIdToDelete)).thenReturn(true);
        doNothing().when(requestRepository).deleteById(requestIdToDelete);

        // Act
        requestService.deleteRequest(requestIdToDelete);

        // Assert
        verify(requestRepository).existsById(requestIdToDelete);
        verify(requestRepository).deleteById(requestIdToDelete);
    }

    @Test
    void deleteRequest_RequestNotFound_ThrowsRequestNotFoundException() {
        // Arrange
        Long nonExistentRequestId = 999L;
        when(requestRepository.existsById(nonExistentRequestId)).thenReturn(false);

        // Act & Assert
        RequestNotFoundException exception = assertThrows(RequestNotFoundException.class, () ->
                requestService.deleteRequest(nonExistentRequestId));

        assertThat(exception.getMessage()).isEqualTo("Request not found with id: " + nonExistentRequestId);
        verify(requestRepository).existsById(nonExistentRequestId);
        verify(requestRepository, never()).deleteById(anyLong()); // Ensure delete is not called
    }

    // --- MODIFIED TEST METHODS ---

    @Test
    void createRequest_NoParticipants_Success() {
        // Arrange
        CreateRequestDTO createRequestDTO = new CreateRequestDTO();
        createRequestDTO.setRequestorId(requestorUser.getUserId());
        createRequestDTO.setJustification("Individual training.");
        createRequestDTO.setGroupRequest(false);
        createRequestDTO.setTanNumber("TAN005");
        createRequestDTO.setCurriculumLink("http://example.com/individual_training");
        createRequestDTO.setParticipantUserIds(new ArrayList<>()); // Explicitly empty list to cover isEmpty() branch

        when(userInfoRepository.findById(requestorUser.getUserId())).thenReturn(Optional.of(requestorUser));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> {
            Request savedRequest = invocation.getArgument(0);
            savedRequest.setRequestId(104L);
            return savedRequest;
        });

        // Act
        RequestDetailsDTO result = requestService.createRequest(createRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(104L);
        assertThat(result.getNoOfParticipants()).isEqualTo(0L);
        verify(userInfoRepository).findById(requestorUser.getUserId());
        verify(requestRepository).save(any(Request.class));

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository).save(requestCaptor.capture());
        Request savedRequest = requestCaptor.getValue();
        assertThat(savedRequest.getRequestedParticipants()).isEmpty();
        // No call to userInfoRepository.findAllByUserIdIn for empty list
        verify(userInfoRepository, never()).findAllByUserIdIn(anyList());
    }

// --- NEW TEST METHODS FOR PREDICATE COVERAGE ---

    @Test
    void getAllRequests_AdminUser_OnlyStatusFilter_ReturnsFilteredList() throws UnauthorizedActionException {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(101L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 2, 1));
        filteredRequest.setRequestStatus("APPROVED");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Approved training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN002");
        filteredRequest.setCurriculamLink("http://example.com/approved");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser1)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getAllRequests(
                adminUser.getUserId(),
                "APPROVED", // Only status filter
                null, null, null, null, null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestStatus()).isEqualTo("APPROVED");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllRequests_AdminUser_OnlyDepartmentFilter_ReturnsFilteredList() throws UnauthorizedActionException {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(101L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 2, 1));
        filteredRequest.setRequestStatus("PENDING");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN002");
        filteredRequest.setCurriculamLink("http://example.com/training");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser1)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getAllRequests(
                adminUser.getUserId(),
                null,
                "IT", // Only department filter
                null, null, null, null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentName()).isEqualTo("IT");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllRequests_AdminUser_OnlyEventFilter_ReturnsFilteredList() throws UnauthorizedActionException {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(101L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 2, 1));
        filteredRequest.setRequestStatus("PENDING");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN002");
        filteredRequest.setCurriculamLink("http://example.com/training");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser1)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getAllRequests(
                adminUser.getUserId(),
                null, null,
                "Spring Boot Workshop", // Only event filter
                null, null, null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventName()).isEqualTo("Spring Boot Workshop");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllRequests_AdminUser_OnlyFromDateFilter_ReturnsFilteredList() throws UnauthorizedActionException {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(101L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 2, 1));
        filteredRequest.setRequestStatus("PENDING");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN002");
        filteredRequest.setCurriculamLink("http://example.com/training");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser1)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getAllRequests(
                adminUser.getUserId(),
                null, null, null, null,
                LocalDate.of(2023, 1, 1), // Only fromDate filter
                null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestDate()).isEqualTo(LocalDate.of(2023, 2, 1));
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllRequests_AdminUser_OnlyToDateFilter_ReturnsFilteredList() throws UnauthorizedActionException {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(101L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 2, 1));
        filteredRequest.setRequestStatus("PENDING");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN002");
        filteredRequest.setCurriculamLink("http://example.com/training");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser1)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getAllRequests(
                adminUser.getUserId(),
                null, null, null, null, null,
                LocalDate.of(2023, 3, 1) // Only toDate filter
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestDate()).isEqualTo(LocalDate.of(2023, 2, 1));
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getFilteredRequests_LCUser_OnlyStatusFilter_ReturnsFilteredList() {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(102L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 3, 10));
        filteredRequest.setRequestStatus("REJECTED");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Rejected training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN003");
        filteredRequest.setCurriculamLink("http://example.com/rejected");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser2)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getFilteredRequests(
                requestorUser.getUserId(),
                "REJECTED", // Only status filter
                null, null, null, null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestStatus()).isEqualTo("REJECTED");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getFilteredRequests_LCUser_OnlyDepartmentFilter_ReturnsFilteredList() {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(102L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 3, 10));
        filteredRequest.setRequestStatus("PENDING");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN003");
        filteredRequest.setCurriculamLink("http://example.com/training");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser2)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getFilteredRequests(
                requestorUser.getUserId(),
                null,
                "IT", // Only department filter
                null, null, null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentName()).isEqualTo("IT");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getFilteredRequests_LCUser_OnlyEventFilter_ReturnsFilteredList() {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(102L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 3, 10));
        filteredRequest.setRequestStatus("PENDING");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN003");
        filteredRequest.setCurriculamLink("http://example.com/training");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser2)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getFilteredRequests(
                requestorUser.getUserId(),
                null, null,
                "Spring Boot Workshop", // Only event filter
                null, null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventName()).isEqualTo("Spring Boot Workshop");
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getFilteredRequests_LCUser_OnlyFromDateFilter_ReturnsFilteredList() {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(102L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 3, 10));
        filteredRequest.setRequestStatus("PENDING");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN003");
        filteredRequest.setCurriculamLink("http://example.com/training");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser2)));

        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getFilteredRequests(
                requestorUser.getUserId(),
                null, null, null,
                LocalDate.of(2023, 3, 1), // Only fromDate filter
                null
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestDate()).isEqualTo(LocalDate.of(2023, 3, 10));
        verify(requestRepository).findAll(any(Specification.class));
    }

    @Test
    void getFilteredRequests_LCUser_OnlyToDateFilter_ReturnsFilteredList() {
        // Arrange
        Request filteredRequest = new Request();
        filteredRequest.setRequestId(102L);
        filteredRequest.setUser(requestorUser);
        filteredRequest.setDepartment(department);
        filteredRequest.setEvent(event);
        filteredRequest.setRequestDate(LocalDate.of(2023, 3, 10));
        filteredRequest.setRequestStatus("PENDING");
        filteredRequest.setGroupRequest(false);
        filteredRequest.setJustification("Training.");
        filteredRequest.setNoOfParticipants(1L);
        filteredRequest.setTAN_Number("TAN003");
        filteredRequest.setCurriculamLink("http://example.com/training");
        filteredRequest.setRequestedParticipants(new ArrayList<>(List.of(participantUser2)));



        when(requestRepository.findAll(any(Specification.class))).thenReturn(List.of(filteredRequest));

        // Act
        List<RequestDetailsDTO> result = requestService.getFilteredRequests(
                requestorUser.getUserId(),
                null, null, null, null,
                LocalDate.of(2023, 3, 15) // Only toDate filter
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestDate()).isEqualTo(LocalDate.of(2023, 3, 10));
        verify(requestRepository).findAll(any(Specification.class));
    }











}
