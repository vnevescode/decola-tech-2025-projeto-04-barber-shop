package br.com.dio.barber_shop_api.service.impl;

import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.controller.response.ClientResponse;
import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.exception.custom.ResourceNotFoundException;
import br.com.dio.barber_shop_api.mapper.ClientMapper;
import br.com.dio.barber_shop_api.repository.IClientRepository;
import br.com.dio.barber_shop_api.repository.IUserRepository;
import br.com.dio.barber_shop_api.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final IClientRepository repository;
    private final IUserRepository userRepository;
    private final ClientMapper mapper;

    @Override
    public ClientResponse create(ClientRequest request) {
        ClientEntity entity = mapper.toEntity(request);

        // Busca o usuário no banco
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        entity.setUser(user);

        ClientEntity saved = repository.save(entity);
        log.info("Cliente criado: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    public ClientResponse getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Cliente com id {} não encontrado", id);
                    return new ResourceNotFoundException("Cliente não encontrado");
                });
    }

    @Override
    public ClientResponse getByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(mapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Cliente com userId {} não encontrado", userId);
                    return new ResourceNotFoundException("Cliente vinculado ao usuário não encontrado");
                });
    }

    @Override
    public List<ClientResponse> getAll() {
        log.info("Buscando todos os clientes");
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public Page<ClientResponse> getAllPageable(Pageable pageable) {
        log.info("Buscando clientes paginados");
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public List<ClientResponse> search(String term) {
        log.info("Buscando clientes pelo termo: {}", term);
        return mapper.toResponseList(repository.findByNameContainingIgnoreCaseOrPhoneContaining(term, term));
    }

    @Override
    public Page<ClientResponse> searchPageable(String term, Pageable pageable) {
        log.info("Buscando clientes paginados pelo termo: {}", term);
        return repository.findByNameContainingIgnoreCaseOrPhoneContaining(term, term, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public ClientResponse update(UUID id, ClientRequest request) {
        ClientEntity existing = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cliente com id {} não encontrado para atualização", id);
                    return new ResourceNotFoundException("Cliente não encontrado");
                });

        existing.setName(request.name());
        existing.setPhone(request.phone());
        log.info("Atualizando cliente com id {}", id);
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            log.warn("Tentativa de deletar cliente inexistente com id {}", id);
            throw new ResourceNotFoundException("Cliente não encontrado");
        }
        repository.deleteById(id);
        log.info("Cliente deletado com sucesso com id {}", id);
    }
}
