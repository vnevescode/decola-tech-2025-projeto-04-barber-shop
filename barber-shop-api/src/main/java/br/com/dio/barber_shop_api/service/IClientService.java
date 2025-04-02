package br.com.dio.barber_shop_api.service;


import br.com.dio.barber_shop_api.entity.ClientEntity;

public interface IClientService {

    ClientEntity save(final ClientEntity entity);

    ClientEntity update(final ClientEntity entity);

    void delete(final long id);


}
