import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PessoaFisica extends Usuario {
    private String cpf;
    private LocalDate dataNascimento;
    
    public PessoaFisica(int id, String nome, String cpf, LocalDate dataNascimento) {
        super(id, nome);
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }
    
    @Override
    public void exibirDados() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("\n=== DADOS PESSOA FÍSICA ===");
        System.out.println("ID: " + id);
        System.out.println("Nome: " + nome);
        System.out.println("CPF: " + formatarCPF(cpf));
        System.out.println("Data de Nascimento: " + dataNascimento.format(formatter));
        System.out.println("Total de Contas: " + contas.size());
    }
    
    private String formatarCPF(String cpf) {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
    
    public String getCpf() { return cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
}
