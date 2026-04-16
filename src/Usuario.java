import java.util.ArrayList;
import java.util.List;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected List<ContaBase> contas;
    
    public Usuario(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.contas = new ArrayList<>();
    }
    
    public abstract void exibirDados();
    
    public void adicionarConta(ContaBase conta) {
        if (conta != null && !contas.contains(conta)) {
            contas.add(conta);
            System.out.println("Conta " + conta.getNumero() + " adicionada ao usuário " + nome);
        } 
    }
    
    public void listarContas() {
        System.out.println("\n=== Contas de " + nome + " ===");
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
        } else {
            for (ContaBase conta : contas) {
                System.out.println(conta);
            }
        }
    }
    
    public int getId() { return id; }
    public String getNome() { return nome; }
    public List<ContaBase> getContas() { return new ArrayList<>(contas); }
}
