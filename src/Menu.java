
import java.util.Optional;
import java.util.Random;

public class Menu {
    private final EntradaUsuario entrada;
    private Banco banco;
    private Usuario usuarioLogado;
    
    // REMOVIDO: referência ao arquivo JSON
    // private static final String ARQUIVO_DADOS = "data/dados_banco.json";

    public Menu() {
        this.entrada = new EntradaUsuario();
        this.banco = new Banco();
        // MODIFICADO: Carrega dados do SQLite em vez do JSON
        this.banco = GerenciadorBanco.carregarDados();
        this.usuarioLogado = null;
    }
    
    private void salvarDados() {
        // MODIFICADO: Salva no SQLite em vez do JSON
        GerenciadorBanco.salvarDados(banco);
        entrada.lerString("Pressione Enter para continuar...");
    }

    public void executar() {
        boolean sistemaAtivo = true;
        
        while (sistemaAtivo) {
            exibirTela("SISTEMA BANCARIO", 
                "1. Cadastrar Usuário",
                "2. Entrar", 
                "0. Sair");
            
            int opcao = entrada.lerIntIntervalo("Digite sua opção: ", 0, 2);
            
            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    entrar();
                    break;
                case 0:
                    salvarDados();
                    sistemaAtivo = false;
                    break;
            }
        }
        
        entrada.fechar();
        System.out.println("Sistema encerrado!");
    }
    
    private void cadastrarUsuario() {
        exibirTela("Cadastrar Usuário",
            "1. Pessoa Física",
            "2. Pessoa Jurídica", 
            "0. Voltar");
        
        int opcao = entrada.lerIntIntervalo("Digite sua opção: ", 0, 2);
        
        switch (opcao) {
            case 1:
                cadastrarPessoaFisica();
                break;
            case 2:
                cadastrarPessoaJuridica();
                break;
            case 0:
                break;
        }
    }
    
    private void cadastrarPessoaFisica() {
        exibirTela("Pessoa  Física");
        Random random = new Random();
        String nome = entrada.lerString("Digite Nome: ");
        String cpf = entrada.lerCPF("Digite CPF: ");
        String dataNasc = entrada.lerData("Digite data de nasc").toString();
        int id = random.nextInt(9000) + 1000;
        //String endereco = entrada.lerString("Digite endereço: ");
        //String telefone = entrada.lerString("Digite telefone: ");
        //String email = entrada.lerString("Digite email: ");
        //String profissao = entrada.lerString("Digite profissão: ");
        
        PessoaFisica pf = new PessoaFisica(id, nome, cpf, 
            java.time.LocalDate.parse(dataNasc));
        banco.cadastrarUsuario(pf);
        
        exibirTela("Usuario Cadastrado",
            "Nome: " + nome + " | id: " + id,
            "CPF: " + cpf + " | Nasc: " + dataNasc);
        
        entrada.lerString("Pressione Enter para continuar...");
    }
    
    private void cadastrarPessoaJuridica() {
        exibirTela("Pessoa  Juridica");
        Random random = new Random();
        String nomeFantasia = entrada.lerString("Digite Nome fantasia: ");
        String razaoSocial = entrada.lerString("Digite Razao Social: ");
        String cnpj = entrada.lerCNPJ("Digite CNPJ: ");
        int id = random.nextInt(9000) + 1000;
        
        PessoaJuridica pj = new PessoaJuridica(id, nomeFantasia, cnpj, razaoSocial, 
            nomeFantasia);
        banco.cadastrarUsuario(pj);
        
        exibirTela("Usuario Cadastrado",
            "Razao social: " + razaoSocial,
            "CNPJ: " + cnpj + " | id: " + id,
            "Nome fantasia: " + nomeFantasia);
        
        entrada.lerString("Pressione Enter para continuar...");
    }
    
    private void entrar() {
        exibirTela("==== ENTRAR ====");
        
        String cpfCnpj = entrada.lerString("Digite CPF/CNPJ: ").replaceAll("[^0-9]", "");
        
        Optional<Usuario> usuarioOpt;
        if (cpfCnpj.length() == 11) {
            usuarioOpt = banco.buscarUsuarioPorCpf(cpfCnpj);
        } else {
            usuarioOpt = banco.buscarUsuarioPorCnpj(cpfCnpj);
        }
        
        if (usuarioOpt.isPresent()) {
            usuarioLogado = usuarioOpt.get();
            menuUsuario();
        } else {
            System.out.println("XXXXX Usuário não encontrado! XXXXX");
            entrada.lerString("Pressione Enter para continuar...");
        }
    }
    
    private void menuUsuario() {
        boolean noMenuUsuario = true;
        
        while (noMenuUsuario && usuarioLogado != null) {
            exibirTela("===== MENU =====",
                "USUARIO Id: " + usuarioLogado.getId() + " " + getDocumentoUsuario(),
                "1. Criar conta corrente",
                "2. Criar conta poupança", 
                "3. Entrar Conta",
                "4. Listar contas",
                "0. Sair");
            
            int opcao = entrada.lerIntIntervalo("Digite sua opção: ", 0, 4);
            
            switch (opcao) {
                case 1:
                    criarContaCorrente();
                    break;
                case 2:
                    criarContaPoupanca();
                    break;
                case 3:
                    entrarConta();
                    break;
                case 4:
                    listarContasUsuario();
                    break;
                case 0:
                    noMenuUsuario = false;
                    usuarioLogado = null;
                    break;
            }
        }
    }
    
    private void criarContaCorrente() {
        exibirTela("==== CRIAR CONTA CORRENTE ====");
        
        String numeroConta = entrada.lerString("Digite numero da conta: ");
        double depositoInicial = entrada.lerDoublePositivo("Valor do deposito inicial: R$ ");
        
        Conta conta = new Conta(numeroConta, usuarioLogado.getId(), depositoInicial);
        
        // ORDEM CORRETA: Primeiro banco, depois usuário
        banco.adicionarConta(conta); // Isso salva no SQLite E associa ao usuário
        
        exibirTela("Conta Cadastrada",
            "Numero conta: " + numeroConta + " | id_titular: " + usuarioLogado.getId(),
            "Saldo: R$ " + String.format("%.2f", depositoInicial));
        
        entrada.lerString("Pressione Enter para continuar...");
    }

    
    private void criarContaPoupanca() {
        exibirTela("==== CRIAR CONTA POUPANCA ====");
        
        String numeroConta = entrada.lerString("Digite numero da conta: ");
        double depositoInicial = entrada.lerDoublePositivo("Valor do deposito inicial: R$ ");
        double taxaJuros = entrada.lerDoublePositivo("Digite taxa de juros (ex: 0.005 para 0.5%): ");
        
        ContaPoupanca poupanca = new ContaPoupanca(numeroConta, usuarioLogado.getId(), depositoInicial, taxaJuros);
        
        // ORDEM CORRETA: Primeiro banco
        banco.adicionarConta(poupanca); // Isso salva no SQLite E associa ao usuário
        
        exibirTela("Conta Cadastrada",
            "Numero conta: " + numeroConta + " | id_titular: " + usuarioLogado.getId(),
            "Saldo: R$ " + String.format("%.2f", depositoInicial),
            "Taxa de juros: " + (taxaJuros * 100) + "%");
        
        entrada.lerString("Pressione Enter para continuar...");
    }
    
    private void entrarConta() {
        if (usuarioLogado.getContas().isEmpty()) {
            System.out.println("XXXXX Nenhuma conta cadastrada! XXXXX");
            entrada.lerString("Pressione Enter para continuar...");
            return;
        }
        
        exibirTela(" ENTRAR  CONTA ");
        
        System.out.print("Suas contas: ");
        for (ContaBase conta : usuarioLogado.getContas()) {
            System.out.print(conta.getNumero() + " | ");
        }
        System.out.println();
        
        String numeroConta = entrada.lerString("Digite o numero da conta: ");
        
        Optional<ContaBase> contaOpt = usuarioLogado.getContas().stream()
                .filter(c -> c.getNumero().equals(numeroConta))
                .findFirst();
        
        if (contaOpt.isPresent()) {
            menuConta(contaOpt.get());
        } else {
            System.out.println("XXXXX Conta não encontrada! XXXXX");
            entrada.lerString("Pressione Enter para continuar...");
        }
    }
    
    private void menuConta(ContaBase conta) {
        boolean naConta = true;
        
        while (naConta) {
            exibirTela("CONTA: " + conta.getNumero() + " | Saldo: R$ " + 
                      String.format("%.2f", conta.consultarSaldo()),
                "1. Sacar",
                "2. Depositar", 
                "0. Voltar pro menu");
            
            int opcao = entrada.lerIntIntervalo("Digite sua opção: ", 0, 2);
            
            switch (opcao) {
                case 1:
                    sacar(conta);
                    break;
                case 2:
                    depositar(conta);
                    break;
                case 0:
                    naConta = false;
                    break;
            }
        }
    }
    
    private void sacar(ContaBase conta) {
        double valor = entrada.lerDoublePositivo("Valor do saque: R$ ");
        boolean sucesso = conta.sacar(valor);
        
        if (sucesso) {
            banco.atualizarSaldoConta(conta);
            System.out.println(" Saque realizado com sucesso!");
        } else {
            System.out.println("XXXXX Falha no saque! XXXXX");
        }
        
        entrada.lerString("Pressione Enter para continuar...");
    }
    
    private void depositar(ContaBase conta) {
        double valor = entrada.lerDoublePositivo("Valor do depósito: R$ ");
        conta.depositar(valor);
        banco.atualizarSaldoConta(conta);
        System.out.println(" Depósito realizado com sucesso!");
        entrada.lerString("Pressione Enter para continuar...");
    }
    
    private void listarContasUsuario() {
        usuarioLogado.listarContas();
        entrada.lerString("Pressione Enter para continuar...");
    }
    
    private void exibirTela(String titulo, String... opcoes) {
        //56 char 56-titulo len+2 / 2
        System.out.println("\n" + "=".repeat(7) + " ".repeat(3) + "=".repeat(7) + " ".repeat(3) + 
                          "=".repeat(7) + " ".repeat(2) + "=".repeat(7) + " ".repeat(3) + 
                          "=".repeat(7) + " ".repeat(3) + "=".repeat(7));
        
        String linhaTitulo = "=".repeat((55-titulo.length())/2) + " "+ 
                           titulo + " " + "=".repeat((55-titulo.length())/2);
        System.out.println(linhaTitulo);
        
        System.out.println("=".repeat(7) + " ".repeat(3) + "=".repeat(7) + " ".repeat(3) + 
                          "=".repeat(7) + " ".repeat(2) + "=".repeat(7) + " ".repeat(3) + 
                          "=".repeat(7) + " ".repeat(3) + "=".repeat(7));
        
        for (String opcao : opcoes) {
            System.out.println(opcao);
        }
    }
    
    private String getDocumentoUsuario() {
        if (usuarioLogado instanceof PessoaFisica) {
            return "CPF: " + ((PessoaFisica) usuarioLogado).getCpf();
        } else if (usuarioLogado instanceof PessoaJuridica) {
            return "CNPJ: " + ((PessoaJuridica) usuarioLogado).getCnpj();
        }
        return "";
    }
}