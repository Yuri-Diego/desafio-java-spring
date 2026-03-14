package one.digitalinnovation.gof.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import one.digitalinnovation.gof.dto.ClienteRequest;
import one.digitalinnovation.gof.dto.ClienteResponse;
import one.digitalinnovation.gof.exception.CepInvalidoException;
import one.digitalinnovation.gof.exception.ClienteNotFoundException;
import one.digitalinnovation.gof.observer.ClienteInseridoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author falvojr
 */
@Service
public class ClienteServiceImpl implements ClienteService {

	// Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	// Builder: DTO
	private ClienteResponse toResponse(Cliente cliente) {
		return ClienteResponse.builder()
				.id(cliente.getId())
				.nome(cliente.getNome())
				.cep(cliente.getEndereco().getCep())
				.logradouro(cliente.getEndereco().getLogradouro())
				.bairro(cliente.getEndereco().getBairro())
				.localidade(cliente.getEndereco().getLocalidade())
				.uf(cliente.getEndereco().getUf())
				.build();
	}

	@Override
	public Iterable<ClienteResponse> buscarTodos() {
		List<ClienteResponse> respostas = new ArrayList<>();
		clienteRepository.findAll().forEach(c -> respostas.add(toResponse(c)));
		return respostas;
	}

	@Override
	public ClienteResponse buscarPorId(Long id) {
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(() -> new ClienteNotFoundException(id));
		return toResponse(cliente);
	}

	@Override
	public ClienteResponse inserir(ClienteRequest request) {
		Cliente cliente = salvarClienteComCep(new Cliente(), request);
		eventPublisher.publishEvent(new ClienteInseridoEvent(this, cliente));
		return toResponse(cliente);
	}

	@Override
	public ClienteResponse atualizar(Long id, ClienteRequest request) {
		Cliente clientExistence = clienteRepository.findById(id)
				.orElseThrow(() -> new ClienteNotFoundException(id));
		return toResponse(salvarClienteComCep(clientExistence, request));
	}

	@Override
	public void deletar(Long id) {
		if (!clienteRepository.existsById(id)) {
			throw new ClienteNotFoundException(id);
		}
		clienteRepository.deleteById(id);
	}

	private Cliente salvarClienteComCep(Cliente cliente, ClienteRequest request) {
		String cep = request.getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			try {
				Endereco novoEndereco = viaCepService.consultarCep(cep);
				if (novoEndereco == null || novoEndereco.getCep() == null) {
					throw new CepInvalidoException(cep); // CEP não existe no ViaCEP
				}
				return enderecoRepository.save(novoEndereco);
			} catch (CepInvalidoException e) {
				throw e;
			} catch (Exception e) {
				throw new CepInvalidoException(cep);
			}
		});
		cliente.setNome(request.getNome());
		cliente.setEndereco(endereco);
		return clienteRepository.save(cliente);
	}

}
