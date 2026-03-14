package one.digitalinnovation.gof.observer;

import one.digitalinnovation.gof.model.Cliente;
import org.springframework.context.ApplicationEvent;

public class ClienteInseridoEvent extends ApplicationEvent {

    private final Cliente cliente;

    public ClienteInseridoEvent(Object source, Cliente cliente) {
        super(source);
        this.cliente = cliente;
    }

    public Cliente getCliente() {
        return cliente;
    }
}