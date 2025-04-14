package br.com.dio.barber_shop_api.service;

import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.controller.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientResponse create(ClientRequest request);
    ClientResponse getById(UUID id);
    ClientResponse getByUserId(UUID userId);
    List<ClientResponse> getAll();
    List<ClientResponse> search(String term);
    Page<ClientResponse> getAllPageable(Pageable pageable);
    Page<ClientResponse> searchPageable(String term, Pageable pageable);
    ClientResponse update(UUID id, ClientRequest request);
    void delete(UUID id);
}
