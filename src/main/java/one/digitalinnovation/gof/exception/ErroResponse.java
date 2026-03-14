package one.digitalinnovation.gof.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErroResponse {

    private int status;
    private String mensagem;
    private LocalDateTime timestamp;
    private Map<String, String> campos;

    private ErroResponse() {}

    public int getStatus() { return status; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, String> getCampos() { return campos; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ErroResponse instance = new ErroResponse();

        public Builder status(int status) {
            instance.status = status;
            return this;
        }
        public Builder mensagem(String mensagem) {
            instance.mensagem = mensagem;
            return this;
        }
        public Builder timestamp(LocalDateTime timestamp) {
            instance.timestamp = timestamp;
            return this;
        }
        public Builder campos(Map<String, String> campos) {
            instance.campos = campos;
            return this;
        }
        public ErroResponse build() {
            return instance;
        }
    }
}