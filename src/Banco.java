import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Banco {
    private List<Usuario> cacheUsuarios = null;
    private List<ContaBase> cacheContas = null;
    private boolean cacheCarregado = false;
    
    public Banco() {
        carregarCache();
    }
    
    private void carregarCache() {
        if (!cacheCarregado) {
            cacheUsuarios = carregarUsuariosDoBanco();
            cacheContas = carregarContasDoBanco();
            associarContasAosUsuarios();
            cacheCarregado = true;
        }
    }
    
    private void associarContasAosUsuarios() {
        for (Usuario usuario : cacheUsuarios) {
            List<ContaBase> contasDoUsuario = getContasPorUsuario(usuario.getId());
            // Usando reflexão para acessar a lista de contas do usuário
            try {
                java.lang.reflect.Field contasField = Usuario.class.getDeclaredField("contas");
                contasField.setAccessible(true);
                // Limpa a lista atual e adiciona as novas contas
                List<ContaBase> contasAtuais = (List<ContaBase>) contasField.get(usuario);
                contasAtuais.clear();
                contasAtuais.addAll(contasDoUsuario);
            } catch (Exception e) {
                System.err.println(" Erro ao associar contas ao usuário: " + e.getMessage());
            }
        }
    }
    
    public void cadastrarUsuario(Usuario usuario) {
        if (buscarUsuarioPorId(usuario.getId()).isEmpty()) {
            GerenciadorBanco.salvarUsuario(usuario);
            cacheUsuarios.add(usuario);
            System.out.println(" Usuário salvo no SQLite e cache");
        }
    }
    
    public void adicionarConta(ContaBase conta) {
        if (buscarContaPorNumero(conta.getNumero()).isEmpty()) {
            // Salva no SQLite
            GerenciadorBanco.salvarConta(conta);
            GerenciadorBanco.atualizarSaldoConta(conta.getNumero(), conta.consultarSaldo());
            
            // Atualiza o cache
            cacheContas.add(conta);
            
            // Associa a conta ao usuário
            Optional<Usuario> usuarioDono = buscarUsuarioPorId(conta.getTitular());
            if (usuarioDono.isPresent()) {
                Usuario usuario = usuarioDono.get();
                usuario.adicionarConta(conta);
                System.out.println(" Conta " + conta.getNumero() + " associada ao usuário " + usuario.getNome());
            }
            
            System.out.println(" Conta salva no SQLite e cache");
        }
    }
    
    public Optional<Usuario> buscarUsuarioPorId(int id) {
        return cacheUsuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }
    
    public Optional<Usuario> buscarUsuarioPorCpf(String cpf) {
        return cacheUsuarios.stream()
                .filter(u -> u instanceof PessoaFisica)
                .map(u -> (PessoaFisica) u)
                .filter(pf -> pf.getCpf().equals(cpf))
                .map(u -> (Usuario) u)
                .findFirst();
    }
    
    public Optional<Usuario> buscarUsuarioPorCnpj(String cnpj) {
        return cacheUsuarios.stream()
                .filter(u -> u instanceof PessoaJuridica)
                .map(u -> (PessoaJuridica) u)
                .filter(pj -> pj.getCnpj().equals(cnpj))
                .map(u -> (Usuario) u)
                .findFirst();
    }
    
    public Optional<ContaBase> buscarContaPorNumero(String numeroConta) {
        return cacheContas.stream()
                .filter(c -> c.getNumero().equals(numeroConta))
                .findFirst();
    }
    
    public List<Usuario> getUsuarios() {
        return new ArrayList<>(cacheUsuarios);
    }
    
    public List<ContaBase> getContas() {
        return new ArrayList<>(cacheContas);
    }
    
    private List<Usuario> carregarUsuariosDoBanco() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String tipo = rs.getString("tipo");
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                
                if ("PessoaFisica".equals(tipo)) {
                    String cpf = rs.getString("cpf");
                    LocalDate dataNasc = LocalDate.parse(rs.getString("data_nascimento"));
                    PessoaFisica pf = new PessoaFisica(id, nome, cpf, dataNasc);
                    usuarios.add(pf);
                } else if ("PessoaJuridica".equals(tipo)) {
                    String cnpj = rs.getString("cnpj");
                    String razaoSocial = rs.getString("razao_social");
                    String nomeFantasia = rs.getString("nome_fantasia");
                    PessoaJuridica pj = new PessoaJuridica(id, nome, cnpj, razaoSocial, nomeFantasia);
                    usuarios.add(pj);
                }
            }
            System.out.println( usuarios.size() + " usuários carregados do SQLite");
        } catch (SQLException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    private List<ContaBase> carregarContasDoBanco() {
        List<ContaBase> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String tipo = rs.getString("tipo");
                String numero = rs.getString("numero");
                int titularId = rs.getInt("titular_id");
                double saldo = rs.getDouble("saldo");
                
                if ("Conta".equals(tipo)) {
                    Conta conta = new Conta(numero, titularId, saldo);
                    contas.add(conta);
                } else if ("ContaPoupanca".equals(tipo)) {
                    double taxaJuros = rs.getDouble("taxa_juros");
                    ContaPoupanca poupanca = new ContaPoupanca(numero, titularId, saldo, taxaJuros);
                    contas.add(poupanca);
                }
            }
            System.out.println( contas.size() + " contas carregadas do SQLite");
        } catch (SQLException e) {
            System.err.println(" Erro ao carregar contas: " + e.getMessage());
        }
        
        return contas;
    }
    
    public void atualizarSaldoConta(ContaBase conta) {
        GerenciadorBanco.atualizarSaldoConta(conta.getNumero(), conta.consultarSaldo());
        System.out.println(" Saldo atualizado no SQLite: R$ " + conta.consultarSaldo());
    }
    
    public void recarregarCache() {
        cacheCarregado = false;
        carregarCache();
        System.out.println(" Cache recarregado");
    }
    
    public List<ContaBase> getContasPorUsuario(int usuarioId) {
        List<ContaBase> contasUsuario = new ArrayList<>();
        
        for (ContaBase conta : cacheContas) {
            if (conta.getTitular() == usuarioId) {
                contasUsuario.add(conta);
            }
        }
        
        return contasUsuario;
    }
}