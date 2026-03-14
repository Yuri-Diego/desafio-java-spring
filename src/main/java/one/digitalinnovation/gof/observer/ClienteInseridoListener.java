package one.digitalinnovation.gof.observer;

import one.digitalinnovation.gof.model.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ClienteInseridoListener {

    private static final Logger log = LoggerFactory.getLogger(ClienteInseridoListener.class);

    @EventListener
    public void onClienteInserido(ClienteInseridoEvent event) {
        Cliente c = event.getCliente();
        log.info("[Observer] Novo cliente: {} | {}/{}",
                c.getNome(), c.getEndereco().getLocalidade(), c.getEndereco().getUf());
        // Aqui poderia: enviar e-mail, acionar CRM, gerar auditoria...
    }
}