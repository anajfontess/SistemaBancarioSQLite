//package src;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GerenciadorBanco {

    public static void salvarDados(Banco banco) {
        // No SQLite, os dados já estão salvos automaticamente
        // Este método é mantido para compatibilidade com a interface existente
        System.out.println(" Dados persistidos no SQLite");
    }

    public static Banco carregarDados() {
        DatabaseConnection.criarTabelas();
        Banco banco = new Banco();
        System.out.println(" Sistema inicializado com SQLite");
        return banco;
    }
    
    private static List<Usuario> carregarUsuarios() {
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
            
        } catch (SQLException e) {
            System.err.println(" Erro ao carregar usuários: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    private static List<ContaBase> carregarContas() {
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
            
        } catch (SQLException e) {
            System.err.println(" Erro ao carregar contas: " + e.getMessage());
        }
        
        return contas;
    }
    
    // Métodos para salvar individualmente (usados pelo Banco)
    public static void salvarUsuario(Usuario usuario) {
        String sql = "INSERT OR REPLACE INTO usuarios (id, tipo, nome, cpf, data_nascimento, cnpj, razao_social, nome_fantasia) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuario.getId());
            pstmt.setString(2, usuario.getClass().getSimpleName());
            pstmt.setString(3, usuario.getNome());
            
            if (usuario instanceof PessoaFisica) {
                PessoaFisica pf = (PessoaFisica) usuario;
                pstmt.setString(4, pf.getCpf());
                pstmt.setString(5, pf.getDataNascimento().toString());
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.VARCHAR);
            } else if (usuario instanceof PessoaJuridica) {
                PessoaJuridica pj = (PessoaJuridica) usuario;
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setString(6, pj.getCnpj());
                pstmt.setString(7, pj.getRazaoSocial());
                pstmt.setString(8, pj.getNomeFantasia());
            }
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println(" Erro ao salvar usuário: " + e.getMessage());
        }
    }
    
    public static void salvarConta(ContaBase conta) {
        String sql = "INSERT OR REPLACE INTO contas (numero, tipo, titular_id, saldo, taxa_juros) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, conta.getNumero());
            pstmt.setString(2, conta.getClass().getSimpleName());
            pstmt.setInt(3, conta.getTitular());
            pstmt.setDouble(4, conta.consultarSaldo());
            
            if (conta instanceof ContaPoupanca) {
                ContaPoupanca cp = (ContaPoupanca) conta;
                pstmt.setDouble(5, cp.getTaxaJuros());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println(" Erro ao salvar conta: " + e.getMessage());
        }
    }
    
    public static void atualizarSaldoConta(String numeroConta, double novoSaldo) {
        String sql = "UPDATE contas SET saldo = ? WHERE numero = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, novoSaldo);
            pstmt.setString(2, numeroConta);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println(" Erro ao atualizar saldo: " + e.getMessage());
        }
    }
}