package br.com.dio.barber_shop_api.controller.doc;

import br.com.dio.barber_shop_api.controller.request.ClientRequest;
import br.com.dio.barber_shop_api.controller.response.ClientResponse;
import br.com.dio.barber_shop_api.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Clientes", description = "Operações relacionadas aos clientes da barbearia")
public interface ClientControllerDoc {


    @Operation(
            summary = "Criar novo cliente",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de cliente",
                                    value = """
                            {
                              "name": "João Silva",
                              "phone": "11999999999"
                            }
                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "409", description = "Telefone já está em uso")
            }
    )
    ClientResponse create(ClientRequest request);

    @Operation(
            summary = "Buscar cliente por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    ClientResponse getById(
            @Parameter(description = "UUID do cliente") UUID id
    );

    @Operation(
            summary = "Buscar cliente por ID do usuário vinculado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    ClientResponse getByUserId(
            @Parameter(description = "UUID do usuário") UUID userId
    );

    @Operation(
            summary = "Listar todos os clientes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada")
            }
    )
    List<ClientResponse> getAll();

    @Operation(
            summary = "Buscar clientes por nome ou telefone",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes filtrada retornada")
            }
    )
    List<ClientResponse> search(
            @Parameter(description = "Termo para buscar por nome ou telefone") String term
    );

    @Operation(
            summary = "Listar clientes com paginação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista paginada de clientes")
            }
    )
    Page<ClientResponse> getAllPaged(Pageable pageable);

    @Operation(
            summary = "Buscar clientes por termo com paginação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista paginada filtrada de clientes")
            }
    )
    Page<ClientResponse> searchPaged(
            @Parameter(description = "Termo para buscar por nome ou telefone") String term,
            Pageable pageable
    );

    @Operation(
            summary = "Atualizar cliente por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            }
    )
    ClientResponse update(
            @Parameter(description = "UUID do cliente") UUID id,
            ClientRequest request
    );

    @Operation(
            summary = "Deletar cliente por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente deletado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    void delete(
            @Parameter(description = "UUID do cliente") UUID id
    );

    @Operation(
            summary = "Buscar cliente autenticado",
            description = "Retorna o cliente vinculado ao usuário autenticado via token JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o usuário autenticado")
            }
    )
    @GetMapping("/me")
    ClientResponse getCurrentClient(@Parameter(hidden = true) @AuthenticationPrincipal UserEntity user);

}
