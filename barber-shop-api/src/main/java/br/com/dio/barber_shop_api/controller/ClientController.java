package br.com.dio.barber_shop_api.controller;

import br.com.dio.barber_shop_api.controller.doc.ClientControllerDoc;
import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.controller.response.ClientResponse;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController implements ClientControllerDoc {
    private final ClientService clientService;

    @Override
    @PostMapping
    public ClientResponse create(@RequestBody @Valid ClientRequest request) {
        log.info("[POST] /clients - Criando cliente com dados: {}", request);
        return clientService.create(request);
    }

    @Override
    @GetMapping("/{id}")
    public ClientResponse getById(@PathVariable UUID id) {
        log.info("[GET] /clients/{} - Buscando cliente por ID", id);
        return clientService.getById(id);
    }

    @Override
    @GetMapping("/user/{userId}")
    public ClientResponse getByUserId(@PathVariable UUID userId) {
        log.info("[GET] /clients/user/{} - Buscando cliente por User ID", userId);
        return clientService.getByUserId(userId);
    }

    @Override
    @GetMapping
    public List<ClientResponse> getAll() {
        log.info("[GET] /clients - Listando todos os clientes");
        return clientService.getAll();
    }

    @Override
    @GetMapping("/search")
    public List<ClientResponse> search(@RequestParam String term) {
        log.info("[GET] /clients/search - Buscando clientes com termo: {}", term);
        return clientService.search(term);
    }

    @Override
    @GetMapping("/paged")
    public org.springframework.data.domain.Page<ClientResponse> getAllPaged(Pageable pageable) {
        log.info("[GET] /clients/paged - Listando clientes paginados");
        return clientService.getAllPageable(pageable);
    }

    @Override
    @GetMapping("/search-paged")
    public org.springframework.data.domain.Page<ClientResponse> searchPaged(@RequestParam String term, Pageable pageable) {
        log.info("[GET] /clients/search-paged - Buscando clientes com termo: {} paginado", term);
        return clientService.searchPageable(term, pageable);
    }

    @Override
    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable UUID id, @RequestBody @Valid ClientRequest request) {
        log.info("[PUT] /clients/{} - Atualizando cliente com dados: {}", id, request);
        return clientService.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        log.info("[DELETE] /clients/{} - Deletando cliente", id);
        clientService.delete(id);
    }

    @GetMapping("/me")
    public ClientResponse getCurrentClient(@AuthenticationPrincipal UserEntity user) {
        log.info("[GET] /clients/me - Buscando cliente do usuário autenticado: {}", user.getEmail());
        return clientService.getByUserId(user.getId());
    }
}
