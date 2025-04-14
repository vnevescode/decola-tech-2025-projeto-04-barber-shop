package br.com.dio.barber_shop_api.integrationTests.service;

import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.controller.response.ClientResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.exception.custom.ResourceNotFoundException;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClientServiceImplIT {
    @Autowired
    private ClientService clientService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IClientRepository clientRepository;

    private UserEntity user;

    @BeforeEach
    void setup() {
        user = userRepository.save(UserEntity.builder()
                .email("cliente@example.com")
                .password("123456")
                .role(UserEntity.Role.ROLE_USER)
                .build());
    }

    @Test
    @DisplayName("✅ Deve criar cliente com sucesso")
    void shouldCreateClientSuccessfully() {
        ClientRequest request = new ClientRequest("João", "11999999999", user.getId());
        ClientResponse response = clientService.create(request);
        assertNotNull(response);
        assertEquals("João", response.name());
    }

    @Test
    @DisplayName("🔍 Deve buscar cliente por ID")
    void shouldGetClientById() {
        ClientEntity entity = clientRepository.save(ClientEntity.builder()
                .name("Maria")
                .phone("11988888888")
                .user(user)
                .build());

        ClientResponse found = clientService.getById(entity.getId());
        assertEquals("Maria", found.name());
    }

    @Test
    @DisplayName("📛 Deve lançar exceção ao buscar ID inexistente")
    void shouldThrowWhenClientIdNotFound() {
        UUID randomId = UUID.randomUUID();
        assertThrows(ResourceNotFoundException.class, () -> clientService.getById(randomId));
    }

    @Test
    @DisplayName("👤 Deve buscar cliente por ID de usuário")
    void shouldGetClientByUserId() {
        ClientEntity entity = clientRepository.save(ClientEntity.builder()
                .name("Lucas")
                .phone("11977777777")
                .user(user)
                .build());

        ClientResponse found = clientService.getByUserId(user.getId());
        assertEquals("Lucas", found.name());
    }

    @Test
    @DisplayName("📋 Deve retornar todos os clientes")
    void shouldReturnAllClients() {
        clientRepository.save(ClientEntity.builder()
                .name("Carlos")
                .phone("11966666666")
                .user(user)
                .build());

        List<ClientResponse> all = clientService.getAll();
        assertFalse(all.isEmpty());
    }

    @Test
    @DisplayName("📑 Deve retornar clientes paginados")
    void shouldReturnPagedClients() {
        for (int i = 1; i <= 5; i++) {
            UserEntity u = userRepository.save(UserEntity.builder()
                    .email("cliente" + i + "@test.com")
                    .password("senha")
                    .role(UserEntity.Role.ROLE_USER)
                    .build());

            clientRepository.save(ClientEntity.builder()
                    .name("Cliente " + i)
                    .phone("1190000000" + i)
                    .user(u)
                    .build());
        }

        Page<ClientResponse> page = clientService.getAllPageable(PageRequest.of(0, 3));
        assertEquals(3, page.getContent().size());
    }

    @Test
    @DisplayName("🔎 Deve buscar clientes por nome ou telefone")
    void shouldSearchClientsByTerm() {
        clientRepository.save(ClientEntity.builder()
                .name("Felipe")
                .phone("11911111111")
                .user(user)
                .build());

        List<ClientResponse> result = clientService.search("Felipe");
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("🔍 Deve buscar clientes por termo com paginação")
    void shouldSearchPagedClientsByTerm() {
        clientRepository.save(ClientEntity.builder()
                .name("Fernanda")
                .phone("11922222222")
                .user(user)
                .build());

        Page<ClientResponse> page = clientService.searchPageable("Fernanda", PageRequest.of(0, 2));
        assertEquals(1, page.getContent().size());
    }

    @Test
    @DisplayName("✏️ Deve atualizar cliente com sucesso")
    void shouldUpdateClient() {
        ClientEntity entity = clientRepository.save(ClientEntity.builder()
                .name("Pedro")
                .phone("11933333333")
                .user(user)
                .build());

        ClientRequest update = new ClientRequest("Pedro Atualizado", "11944444444", user.getId());
        ClientResponse updated = clientService.update(entity.getId(), update);

        assertEquals("Pedro Atualizado", updated.name());
        assertEquals("11944444444", updated.phone());
    }

    @Test
    @DisplayName("❌ Deve lançar exceção ao atualizar cliente inexistente")
    void shouldThrowWhenUpdateNonExistentClient() {
        UUID fakeId = UUID.randomUUID();
        ClientRequest update = new ClientRequest("Nome", "11912345678", user.getId());

        assertThrows(ResourceNotFoundException.class, () -> clientService.update(fakeId, update));
    }

    @Test
    @DisplayName("🗑️ Deve deletar cliente com sucesso")
    void shouldDeleteClient() {
        ClientEntity entity = clientRepository.save(ClientEntity.builder()
                .name("Carlos")
                .phone("11955555555")
                .user(user)
                .build());

        clientService.delete(entity.getId());
        assertFalse(clientRepository.existsById(entity.getId()));
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao deletar cliente inexistente")
    void shouldThrowWhenDeleteNonExistentClient() {
        UUID fakeId = UUID.randomUUID();
        assertThrows(ResourceNotFoundException.class, () -> clientService.delete(fakeId));
    }
}
