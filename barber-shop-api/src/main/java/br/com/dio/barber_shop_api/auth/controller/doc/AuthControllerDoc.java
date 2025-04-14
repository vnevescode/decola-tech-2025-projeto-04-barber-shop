package br.com.dio.barber_shop_api.auth.controller.doc;

import br.com.dio.barber_shop_api.auth.dto.AuthRequest;
import br.com.dio.barber_shop_api.auth.dto.AuthResponse;
import br.com.dio.barber_shop_api.controller.request.RegisterUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Autenticação", description = "Login e registro de usuários")
public interface AuthControllerDoc {

    @Operation(
            summary = "Login do usuário",
            description = "Autentica o usuário e retorna um token JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
                    @ApiResponse(responseCode = "400", description = "Erro de validação no corpo da requisição")
            }
    )
    @PostMapping("/login")
    AuthResponse login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais de login",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthRequest.class),
                            examples = @ExampleObject(name = "Exemplo de login", value = """
                    {
                      "email": "usuario@exemplo.com",
                      "password": "senhaSegura123"
                    }
                """)
                    )
            )
            @RequestBody AuthRequest request
    );

    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria um novo usuário com e-mail e senha",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
                    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado"),
                    @ApiResponse(responseCode = "400", description = "Erro de validação nos dados enviados")
            }
    )
    @PostMapping("/register")
    Map<String, String> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do usuário para registro",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterUserRequest.class),
                            examples = @ExampleObject(name = "Exemplo de registro", value = """
                    {
                      "email": "novo@usuario.com",
                      "password": "novaSenha123"
                    }
                """)
                    )
            )
            @RequestBody RegisterUserRequest request
    );
}
