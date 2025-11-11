package com.ford.collectionRequirements.controller;

import com.ford.collectionRequirements.dto.*;
import com.ford.collectionRequirements.exception.RequestNotFoundException;
import com.ford.collectionRequirements.exception.UnauthorizedActionException;
import com.ford.collectionRequirements.entity.Request;
import com.ford.collectionRequirements.service.RequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    @Test
    void testGetAllRequestByUserIdWithFilters_ReturnsList() throws UnauthorizedActionException {
        List<RequestDetailsDTO> expectedList = List.of(new RequestDetailsDTO());
        when(requestService.getAllRequests(anyLong(), any(), any(), any(), any(), any(), any()))
                .thenReturn(expectedList);

        ResponseEntity<List<RequestDetailsDTO>> response = requestController.getAllRequestByUserIdWithFilters(
                1L, "APPROVED", "Dept", "Event", "User", LocalDate.now(), LocalDate.now());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedList);

        verify(requestService, times(1)).getAllRequests(anyLong(), any(), any(), any(), any(), any(), any());
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testGetAllRequestByUserIdWithFilters_ThrowsUnauthorized() throws UnauthorizedActionException {
        when(requestService.getAllRequests(anyLong(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new UnauthorizedActionException("Unauthorized"));

        UnauthorizedActionException ex = assertThrows(UnauthorizedActionException.class, () ->
                requestController.getAllRequestByUserIdWithFilters(
                        1L, null, null, null, null, null, null));

        assertThat(ex.getMessage()).isEqualTo("Unauthorized");
        verify(requestService, times(1)).getAllRequests(anyLong(), any(), any(), any(), any(), any(), any());
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testGetAllRequestSummaryForUser_ReturnsCounts() throws UnauthorizedActionException {
        RequestCountsDTO expected = new RequestCountsDTO();
        when(requestService.getAllRequestsSummaryCounts(anyLong())).thenReturn(expected);

        ResponseEntity<RequestCountsDTO> response = requestController.getAllRequestSummaryForUser(1L);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);

        verify(requestService, times(1)).getAllRequestsSummaryCounts(1L);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testGetAllRequestSummaryForUser_ThrowsUnauthorized() throws UnauthorizedActionException {
        when(requestService.getAllRequestsSummaryCounts(anyLong()))
                .thenThrow(new UnauthorizedActionException("Unauthorized"));

        UnauthorizedActionException ex = assertThrows(UnauthorizedActionException.class, () ->
                requestController.getAllRequestSummaryForUser(1L));

        assertThat(ex.getMessage()).isEqualTo("Unauthorized");
        verify(requestService, times(1)).getAllRequestsSummaryCounts(1L);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testGetRequestsByUserIdWithFilters_ReturnsList() {
        List<RequestDetailsDTO> expectedList = List.of(new RequestDetailsDTO());
        when(requestService.getFilteredRequests(anyLong(), any(), any(), any(), any(), any()))
                .thenReturn(expectedList);

        ResponseEntity<List<RequestDetailsDTO>> response = requestController.getRequestsByUserIdWithFilters(
                2L, "PENDING", "Dept", "Event", LocalDate.now(), LocalDate.now());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedList);

        verify(requestService, times(1)).getFilteredRequests(anyLong(), any(), any(), any(), any(), any());
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testGetRequestSummaryForUser_ReturnsCounts() {
        RequestCountsDTO expected = new RequestCountsDTO();
        when(requestService.getRequestSummaryCounts(anyLong())).thenReturn(expected);

        ResponseEntity<RequestCountsDTO> response = requestController.getRequestSummaryForUser(2L);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);

        verify(requestService, times(1)).getRequestSummaryCounts(2L);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testCreateRequest_ReturnsCreated() {
        CreateRequestDTO dto = new CreateRequestDTO();
        RequestDetailsDTO expected = new RequestDetailsDTO();
        when(requestService.createRequest(dto)).thenReturn(expected);

        ResponseEntity<RequestDetailsDTO> response = requestController.createRequest(dto);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);

        verify(requestService, times(1)).createRequest(dto);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testGetRequestById_ReturnsDetails() throws RequestNotFoundException {
        RequestDetailsDTO expected = new RequestDetailsDTO();
        when(requestService.getRequestDetails(10L)).thenReturn(expected);

        ResponseEntity<RequestDetailsDTO> response = requestController.getRequestById(10L);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);

        verify(requestService, times(1)).getRequestDetails(10L);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testGetRequestById_ThrowsNotFound() throws RequestNotFoundException {
        when(requestService.getRequestDetails(10L)).thenThrow(new RequestNotFoundException("Not found"));

        RequestNotFoundException ex = assertThrows(RequestNotFoundException.class, () ->
                requestController.getRequestById(10L));

        assertThat(ex.getMessage()).isEqualTo("Not found");
        verify(requestService, times(1)).getRequestDetails(10L);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testGetAllUsers_ReturnsList() {
        List<BasicUserDTO> expected = List.of(new BasicUserDTO());
        when(requestService.getAllUsers()).thenReturn(expected);

        ResponseEntity<List<BasicUserDTO>> response = requestController.getAllUsers();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);

        verify(requestService, times(1)).getAllUsers();
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testUpdateRequest_ReturnsRequest() throws RequestNotFoundException {
        EditRequestDTO dto = new EditRequestDTO();
        Request expected = new Request();
        when(requestService.updateRequest(5L, dto)).thenReturn(expected);

        ResponseEntity<Request> response = requestController.updateRequest(5L, dto);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);

        verify(requestService, times(1)).updateRequest(5L, dto);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testUpdateRequest_ThrowsNotFound() throws RequestNotFoundException {
        EditRequestDTO dto = new EditRequestDTO();
        when(requestService.updateRequest(5L, dto)).thenThrow(new RequestNotFoundException("Not found"));

        RequestNotFoundException ex = assertThrows(RequestNotFoundException.class, () ->
                requestController.updateRequest(5L, dto));

        assertThat(ex.getMessage()).isEqualTo("Not found");
        verify(requestService, times(1)).updateRequest(5L, dto);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testDeleteRequest_ReturnsNoContent() throws RequestNotFoundException {
        doNothing().when(requestService).deleteRequest(7L);

        ResponseEntity<Void> response = requestController.deleteRequest(7L);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(requestService, times(1)).deleteRequest(7L);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void testDeleteRequest_ThrowsNotFound() throws RequestNotFoundException {
        doThrow(new RequestNotFoundException("Not found")).when(requestService).deleteRequest(7L);

        RequestNotFoundException ex = assertThrows(RequestNotFoundException.class, () ->
                requestController.deleteRequest(7L));

        assertThat(ex.getMessage()).isEqualTo("Not found");
        verify(requestService, times(1)).deleteRequest(7L);
        verifyNoMoreInteractions(requestService);
    }
}
