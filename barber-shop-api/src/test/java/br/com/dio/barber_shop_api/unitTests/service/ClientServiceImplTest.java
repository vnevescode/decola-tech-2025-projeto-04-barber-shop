package br.com.dio.barber_shop_api.unitTests.service;

import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.controller.response.ClientResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.exception.custom.ResourceNotFoundException;
import br.com.dio.barber_shop_api.mapper.ClientMapper;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ClientServiceImplTest {

    @Mock private IClientRepository repository;
    @Mock private ClientMapper mapper;
    @Mock private IUserRepository userRepository;
    @InjectMocks private ClientServiceImpl service;

    private final UUID clientId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private final ClientEntity entity = ClientEntity.builder()
            .id(clientId)
            .name("João Silva")
            .phone("11999999999")
            .user(UserEntity.builder().id(userId).build())
            .build();

    private final ClientResponse response = new ClientResponse(clientId, "João Silva", "11999999999");

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldReturnClientResponse() {
        ClientRequest request = new ClientRequest("João Silva", "11999999999", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(UserEntity.builder().id(userId).build())); // <-- Aqui
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ClientResponse result = service.create(request);

        assertEquals(response, result);
        verify(repository).save(entity);
    }

    @Test
    void getById_WhenFound_ShouldReturnClient() {
        when(repository.findById(clientId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ClientResponse result = service.getById(clientId);

        assertEquals(response, result);
    }

    @Test
    void getById_WhenNotFound_ShouldThrowException() {
        when(repository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(clientId));
    }

    @Test
    void getByUserId_WhenFound_ShouldReturnClient() {
        when(repository.findByUserId(clientId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ClientResponse result = service.getByUserId(clientId);

        assertEquals(response, result);
    }

    @Test
    void getByUserId_WhenNotFound_ShouldThrowException() {
        when(repository.findByUserId(clientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getByUserId(clientId));
    }

    @Test
    void getAll_ShouldReturnClientList() {
        List<ClientEntity> entities = List.of(entity);
        List<ClientResponse> responses = List.of(response);

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toResponseList(entities)).thenReturn(responses);

        List<ClientResponse> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("João Silva", result.get(0).name());
    }

    @Test
    void search_ShouldReturnMatchingClients() {
        String term = "João";
        List<ClientEntity> entities = List.of(entity);
        List<ClientResponse> responses = List.of(response);

        when(repository.findByNameContainingIgnoreCaseOrPhoneContaining(term, term)).thenReturn(entities);
        when(mapper.toResponseList(entities)).thenReturn(responses);

        List<ClientResponse> result = service.search(term);

        assertEquals(1, result.size());
        assertEquals("João Silva", result.get(0).name());
    }

    @Test
    void getAllPageable_ShouldReturnPagedClients() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClientEntity> page = new PageImpl<>(List.of(entity));

        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<ClientResponse> result = service.getAllPageable(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchPageable_ShouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        String term = "119";
        Page<ClientEntity> page = new PageImpl<>(List.of(entity));

        when(repository.findByNameContainingIgnoreCaseOrPhoneContaining(term, term, pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<ClientResponse> result = service.searchPageable(term, pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void update_ShouldUpdateClient() {
        ClientRequest request = new ClientRequest("João Atualizado", "11999998888", userId);
        ClientEntity updated = ClientEntity.builder().id(clientId).name("João Atualizado").phone("11999998888").build();
        ClientResponse updatedResponse = new ClientResponse(clientId, "João Atualizado", "11999998888");

        when(repository.findById(clientId)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(updatedResponse);

        ClientResponse result = service.update(clientId, request);

        assertEquals("João Atualizado", result.name());
        assertEquals("11999998888", result.phone());
    }

    @Test
    void update_WhenClientNotFound_ShouldThrowException() {
        ClientRequest request = new ClientRequest("João", "00000000000", userId);

        when(repository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(clientId, request));
    }

    @Test
    void delete_ShouldRemoveClient() {
        when(repository.existsById(clientId)).thenReturn(true);

        service.delete(clientId);

        verify(repository).deleteById(clientId);
    }

    @Test
    void delete_WhenClientNotFound_ShouldThrowException() {
        when(repository.existsById(clientId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(clientId));
    }
}
