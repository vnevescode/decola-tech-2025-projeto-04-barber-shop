package br.com.dio.barber_shop_api.repository;

import br.com.dio.barber_shop_api.entity.HaircutTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IHaircutTypeRepository extends JpaRepository<HaircutTypeEntity, UUID> {

    boolean existsByName(String name);
}
