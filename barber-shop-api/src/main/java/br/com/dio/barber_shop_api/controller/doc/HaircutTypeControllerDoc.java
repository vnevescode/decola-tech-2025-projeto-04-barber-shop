package br.com.dio.barber_shop_api.controller.doc;

import br.com.dio.barber_shop_api.controller.request.HaircutTypeRequest;
import br.com.dio.barber_shop_api.controller.response.HaircutTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Tag(name = "Tipos de Corte", description = "Gerenciamento dos tipos de corte disponíveis")
public interface HaircutTypeControllerDoc {

    @Operation(
            summary = "Criar um novo tipo de corte",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HaircutTypeRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de tipo de corte",
                                    value = """
                            {
                              "name": "Corte Degradê",
                              "price": 45.00
                            }
                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tipo de corte criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "409", description = "Nome do tipo de corte já em uso")
            }
    )
    HaircutTypeResponse create(HaircutTypeRequest request);

    @Operation(
            summary = "Listar todos os tipos de corte",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tipos de corte listados com sucesso")
            }
    )
    List<HaircutTypeResponse> getAll();

    @Operation(
            summary = "Listar tipos de corte com paginação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tipos de corte paginados listados com sucesso")
            }
    )
    Page<HaircutTypeResponse> getAllPageable(Pageable pageable);

    @Operation(
            summary = "Verificar se existe um tipo de corte pelo nome",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Retorna true ou false")
            }
    )
    boolean existsByName(
            @Parameter(description = "Nome do corte a ser verificado") String name
    );
}
