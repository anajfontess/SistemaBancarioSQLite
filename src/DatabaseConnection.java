import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:data/banco_bancario.db";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite não encontrado", e);
        }
        return DriverManager.getConnection(URL);
    }
    
    public static void criarTabelas() {
        // SQL tradicional (sem text blocks)
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                            "id INTEGER PRIMARY KEY, " +
                            "tipo TEXT NOT NULL CHECK (tipo IN ('PessoaFisica', 'PessoaJuridica')), " +
                            "nome TEXT NOT NULL, " +
                            "cpf TEXT, " +
                            "data_nascimento TEXT, " +
                            "cnpj TEXT, " +
                            "razao_social TEXT, " +
                            "nome_fantasia TEXT)";
        
        String sqlContas = "CREATE TABLE IF NOT EXISTS contas (" +
                          "numero TEXT PRIMARY KEY, " +
                          "tipo TEXT NOT NULL CHECK (tipo IN ('Conta', 'ContaPoupanca')), " +
                          "titular_id INTEGER NOT NULL, " +
                          "saldo REAL NOT NULL DEFAULT 0.0, " +
                          "taxa_juros REAL, " +
                          "FOREIGN KEY (titular_id) REFERENCES usuarios(id))";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlContas);
            System.out.println(" Tabelas criadas/verificadas com sucesso!");
            
        } catch (SQLException e) {
            System.err.println(" Erro ao criar tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void inicializarBanco() {
        criarTabelas();
    }
    
    // Método auxiliar para testar a conexão
    public static void testarConexao() {
        try (Connection conn = getConnection()) {
            System.out.println(" Conexão com SQLite estabelecida com sucesso!");
        } catch (SQLException e) {
            System.err.println(" Erro na conexão: " + e.getMessage());
        }
    }
}