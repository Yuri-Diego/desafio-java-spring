package one.digitalinnovation.gof.exception;

public class CepInvalidoException extends RuntimeException {
    public CepInvalidoException(String cep) {
        super("CEP inválido ou não encontrado: " + cep);
    }
}
