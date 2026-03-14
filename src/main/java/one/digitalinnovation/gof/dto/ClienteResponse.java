package one.digitalinnovation.gof.dto;

public class ClienteResponse {
    private Long id;
    private String nome;
    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ClienteResponse instance = new ClienteResponse();

        public Builder id(Long id)             { instance.id = id; return this; }
        public Builder nome(String nome)       { instance.nome = nome; return this; }
        public Builder cep(String cep)         { instance.cep = cep; return this; }
        public Builder logradouro(String l)    { instance.logradouro = l; return this; }
        public Builder bairro(String b)        { instance.bairro = b; return this; }
        public Builder localidade(String loc)  { instance.localidade = loc; return this; }
        public Builder uf(String uf)           { instance.uf = uf; return this; }

        public ClienteResponse build()         { return instance; }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
