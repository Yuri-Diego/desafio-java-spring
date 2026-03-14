package one.digitalinnovation.gof.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ErroResponse> handleClienteNotFound(ClienteNotFoundException ex) {
        ErroResponse erro = ErroResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ErroResponse> handleCepInvalido(CepInvalidoException ex) {
        ErroResponse erro = ErroResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            campos.put(campo, mensagem);
        });
        ErroResponse erro = ErroResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .mensagem("Erro de validação nos campos informados.")
                .timestamp(LocalDateTime.now())
                .campos(campos)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(Exception ex) {
        ErroResponse erro = ErroResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .mensagem("Ocorreu um erro interno. Tente novamente mais tarde.")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}