public class PessoaJuridica extends Usuario {
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    
    public PessoaJuridica(int id, String nome, String cnpj, String razaoSocial, 
                         String nomeFantasia) {
        super(id, nome);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
    }
    
    @Override
    public void exibirDados() {
        System.out.println("\n=== DADOS PESSOA JURÍDICA ===");
        System.out.println("ID: " + id);
        System.out.println("Nome Fantasia: " + nome);
        System.out.println("CNPJ: " + formatarCNPJ(cnpj));
        System.out.println("Razão Social: " + razaoSocial);
        System.out.println("Total de Contas: " + contas.size());
    }
    
    private String formatarCNPJ(String cnpj) {
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
    
    public String getCnpj() { return cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
}
