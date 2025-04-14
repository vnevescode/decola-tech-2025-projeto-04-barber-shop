package br.com.dio.barber_shop_api.controller.doc;

import br.com.dio.barber_shop_api.controller.request.ScheduleRequest;
import br.com.dio.barber_shop_api.controller.response.ScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "Agendamentos", description = "Operações relacionadas aos agendamentos da barbearia")
public interface ScheduleControllerDoc {
    @Operation(
            summary = "Criar agendamento",
            description = "Cria um novo agendamento para um cliente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso"),
                    @ApiResponse(responseCode = "409", description = "Horário já está ocupado"),
                    @ApiResponse(responseCode = "404", description = "Cliente ou tipo de corte não encontrado")
            }
    )
    @PostMapping
    ScheduleResponse create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do agendamento",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ScheduleRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Agendamento",
                                    value = """
                        {
                          "startAt": "2025-04-12T10:00:00Z",
                          "endAt": "2025-04-12T11:00:00Z",
                          "clientId": "UUID-CLIENTE",
                          "haircutTypeId": "UUID-CORTE"
                        }
                        """
                            )
                    )
            )
            @RequestBody ScheduleRequest request
    );

    @Operation(summary = "Listar todos os agendamentos", responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    List<ScheduleResponse> getAll();

    @Operation(summary = "Listar agendamentos com paginação", responses = {
            @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    })
    @GetMapping("/paged")
    Page<ScheduleResponse> getAllPageable(Pageable pageable);

    @Operation(summary = "Listar agendamentos por intervalo de mês", responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping("/month")
    List<ScheduleResponse> findMonth(
            @Parameter(description = "Data inicial (ex: 2025-04-01T00:00:00Z)") @RequestParam OffsetDateTime start,
            @Parameter(description = "Data final (ex: 2025-04-30T23:59:59Z)") @RequestParam OffsetDateTime end
    );

    @Operation(summary = "Histórico de agendamentos de um cliente", responses = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso")
    })
    @GetMapping("/client/{clientId}/history")
    List<ScheduleResponse> findClientHistory(@PathVariable UUID clientId);

    @Operation(summary = "Agendamentos futuros de um cliente", responses = {
            @ApiResponse(responseCode = "200", description = "Agendamentos futuros retornados com sucesso")
    })
    @GetMapping("/client/{clientId}/upcoming")
    List<ScheduleResponse> findClientUpcoming(@PathVariable UUID clientId);

    @Operation(summary = "Filtro de agendamentos para admin", responses = {
            @ApiResponse(responseCode = "200", description = "Filtros aplicados com sucesso")
    })
    @GetMapping("/admin-filter")
    List<ScheduleResponse> adminFilter(
            @RequestParam boolean confirmed,
            @RequestParam boolean canceled,
            @RequestParam OffsetDateTime start,
            @RequestParam OffsetDateTime end,
            @RequestParam UUID haircutTypeId
    );

    @Operation(
            summary = "Confirmar agendamento",
            description = "Permite ao administrador confirmar um agendamento",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Agendamento confirmado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado para usuários não administradores"),
                    @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
            }
    )
    @PatchMapping("/{id}/confirm")
    ScheduleResponse confirm(@PathVariable UUID id);
}
