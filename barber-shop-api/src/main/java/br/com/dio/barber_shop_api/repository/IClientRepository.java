package br.com.dio.barber_shop_api.repository;

import br.com.dio.barber_shop_api.entity.ClientEntity;
import br.com.dio.barber_shop_api.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IClientRepository extends JpaRepository<ClientEntity, UUID> {
    boolean existsByPhone(String phone);

    Optional<ClientEntity> findByPhone(String phone);

    Optional<ClientEntity> findByUserId(UUID userId);

    boolean existsByUser(UserEntity user);

    List<ClientEntity> findByNameContainingIgnoreCaseOrPhoneContaining(String name, String phone);

    Page<ClientEntity> findByNameContainingIgnoreCaseOrPhoneContaining(String name, String phone, Pageable pageable);
}
