# Sistema Bancário - Projeto Java com persistência em JSON
Sistema bancário desenvolvido em Java para aprendizado pessoal.
Permite cadastro de usuários (Pessoa Física e Jurídica), criação de contas corrente e poupança, operações bancárias (saque, depósito) e persistência dos dados em arquivo JSON.

## Funcionalidades
### Usuários
- Cadastro de Pessoa Física (CPF, data de nascimento)
- Cadastro de Pessoa Jurídica (CNPJ, razão social, nome fantasia)
- Login por CPF ou CNPJ
- Cada usuário pode ter múltiplas contas

### Contas
- Conta Corrente (Conta)
- Conta Poupança (ContaPoupanca) – com taxa de juros e método aplicarJuros()
- Operações: depósito, saque, consulta de saldo
- Listagem de todas as contas de um usuário

### Persistência
- Dados salvos em arquivo data/dados_banco.json no formato JSON
- Leitura e escrita automática ao iniciar/sair do programa
- Gerenciamento via classe GerenciadorJSON

## Estrutura do Projeto

## 📁 Estrutura do Projeto

```bash
.
├── .vscode/                 # Configurações do VS Code 
├── bin/                     # Arquivos compilados .class)
├── data/                    
│   └── banco_bancario.db     # Persistência dos dados (gerado automaticamente)
├── lib/     # Bibliotecas externas 
│   └── sqlite-jdbc-3.50.3.0.jar # Driver JDBC para SQLite
├── src/                     # Código-fonte principal
│   ├── Banco.java
│   ├── Conta.java
│   ├── ContaBase.java
│   ├── ContaPoupanca.java
│   ├── EntradaUsuario.java
│   ├── GerenciadorBanco.java 
│   ├── DatabaseConnection.java
│   ├── Main.java
│   ├── Menu.java
│   ├── OperacoesBancarias.java
│   ├── PessoaFisica.java
│   ├── PessoaJuridica.java
│   └── Usuario.java
├── README.md
```

## Execução
### Pré‑requisitos
- JDK 11 ou superior (recomendado Java 17+)
- Terminal (cmd, PowerShell, bash)
- Driver JDBC para SQLite – incluído no projeto como lib/sqlite-jdbc-3.5.3.0.jar

### Como compilar e executar 
1. Clone ou baixe o projeto e acesse o diretório raiz.
2. Compile todos os arquivos .java (dentro da pasta src):

```bash
javac -cp "lib/sqlite-jdbc-3.5.3.0.jar" src/*.java -d bin 
```
Isso criará os arquivos .class dentro da pasta bin/.

3. Execute o programa:

```bash
java -cp "bin;lib/sqlite-jdbc-3.50.3.0.jar" Main
```
> Windows usa `;` no classpath, Linux/macOS usa `:`
4. Ou execute o programa com o botão run acima da classe main




> Certifique-se de que o diretório data/ existe. O arquivo banco_bancario.db será gerado automaticamente na primeira execução.

## Como Usar (Menu Principal)
Ao executar, o sistema exibe:

```bash
========= ===== =========
        SISTEMA BANCARIO
========= ===== =========
1. Cadastrar Usuário
2. Entrar
0. Sair
```
### Fluxo típico:
1. Cadastrar um usuário → escolha Pessoa Física ou Jurídica → informe os dados solicitados (CPF/CNPJ, nome, etc.). Um ID numérico será gerado automaticamente.
2. Entrar → informe o CPF (11 dígitos) ou CNPJ (14 dígitos) cadastrado.
3. Menu do usuário – após logado, você pode:
    - Criar conta corrente
    - Criar conta poupança
    - Entrar em uma conta específica (para sacar/depositar)
    - Listar todas as suas contas
    - Dentro de uma conta – operações de saque e depósito.


## Principais Classes e Relacionamentos

| Classe                            | Descrição                                                                                                                       |
| --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `Usuario` (abstrata)              | Classe base para pessoas físicas e jurídicas; mantém lista de contas em memória sincronizada com o banco via `GerenciadorBanco` |
| `PessoaFisica` / `PessoaJuridica` | Especializações de `Usuario`, adicionando CPF/CNPJ e dados específicos (data de nascimento, razão social, nome fantasia)        |
| `ContaBase` (abstrata)            | Implementa operações bancárias (depósito e consulta de saldo) e define `sacar()` como abstrato                                  |
| `Conta` / `ContaPoupanca`         | Herdam de `ContaBase`; a poupança possui taxa de juros e método `aplicarJuros()`                                                |
| `Banco`                           | Gerencia listas em memória de usuários e contas, com métodos de busca (ID, CPF, CNPJ, número da conta)                          |
| `DatabaseConnection`              | Responsável pela conexão com SQLite (`jdbc:sqlite:data/banco_bancario.db`) e criação automática das tabelas                     |
| `GerenciadorBanco`                | Camada de persistência responsável por salvar, atualizar e carregar dados do banco                                              |
| `EntradaUsuario`                  | Realiza validação de entradas (CPF, CNPJ, números, datas, etc.)                                                                 |
| `Menu`                            | Controla o fluxo da aplicação e interação com o usuário                                                                         |
## Banco de Dados (SQLite)

O sistema utiliza **SQLite** para persistência dos dados, garantindo armazenamento eficiente e estruturado.

### Tabela `usuarios`

| Coluna            | Tipo    | Descrição                               |
| ----------------- | ------- | --------------------------------------- |
| `id`              | INTEGER | Chave primária (gerada automaticamente) |
| `tipo`            | TEXT    | `PessoaFisica` ou `PessoaJuridica`      |
| `nome`            | TEXT    | Nome ou nome fantasia                   |
| `cpf`             | TEXT    | CPF (apenas para pessoa física)         |
| `data_nascimento` | TEXT    | Data de nascimento (PF)                 |
| `cnpj`            | TEXT    | CNPJ (apenas para pessoa jurídica)      |
| `razao_social`    | TEXT    | Razão social (PJ)                       |
| `nome_fantasia`   | TEXT    | Nome fantasia (PJ)                      |

---

### Tabela `contas`

| Coluna       | Tipo    | Descrição                             |
| ------------ | ------- | ------------------------------------- |
| `numero`     | TEXT    | Chave primária (número da conta)      |
| `tipo`       | TEXT    | `Conta` ou `ContaPoupanca`            |
| `titular_id` | INTEGER | Chave estrangeira para `usuarios(id)` |
| `saldo`      | REAL    | Saldo atual                           |
| `taxa_juros` | REAL    | Taxa de juros (apenas para poupança)  |

---

## Persistência Automática

* As tabelas são criadas automaticamente na **primeira execução**
* Todas as operações são refletidas em tempo real no banco:

  * Cadastro de usuários
  * Criação de contas
  * Depósitos e saques


## Diferenças em Relação à Versão JSON

| Aspecto        | Versão JSON              | Versão SQLite                              |
| -------------- | ------------------------ | ------------------------------------------ |
| Persistência   | `dados_banco.json`       | `banco_bancario.db`                        |
| Gerenciamento  | `GerenciadorJSON`        | `GerenciadorBanco` + `DatabaseConnection`  |
| Driver externo | Não necessário           | `sqlite-jdbc-3.5.3.0.jar`                  |
| I/O            | Leitura/escrita completa | Operações individuais (`INSERT`, `UPDATE`) |
| Integridade    | Não possui               | Suporte a `FOREIGN KEY`                    |

## Detalhes Técnicos
- Persistência: Implementada com SQLite e JDBC, utilizando o driver sqlite-jdbc-3.5.3.0.jar. As operações são realizadas via PreparedStatement e as tabelas são criadas automaticamente na inicialização (DatabaseConnection.criarTabelas()).
- Local do banco: O arquivo do banco de dados é data/banco_bancario.db – criado automaticamente no primeiro acesso.
- Gerenciador de persistência: Substitui o antigo GerenciadorJSON. A classe GerenciadorBanco fornece métodos para salvar/carregar usuários e contas individualmente, com atualizações em tempo real (cada operação de saque/depósito chama atualizarSaldoConta()).
- Integridade referencial: A tabela contas possui chave estrangeira (FOREIGN KEY) para usuarios(id), garantindo que uma conta só exista vinculada a um titular válido.
- Validação de entrada: CPF e CNPJ aceitam apenas números (removem-se pontos, traços e barras); a formatação para exibição (ex.: 123.456.789-01) é feita nos métodos formatarCPF() e formatarCNPJ() das respectivas classes.
- Geração de IDs: Random.nextInt(9000) + 1000 – IDs numéricos entre 1000 e 9999, atribuídos automaticamente no cadastro do usuário.
- Datas: Utiliza java.time.LocalDate com formato ISO AAAA-MM-DD (padrão do SQLite para TEXT). A conversão é feita diretamente com toString() e LocalDate.parse().
- Tratamento de exceções: Captura SQLException em todas as operações de banco, exibindo mensagens de erro sem interromper o sistema (exceto falhas críticas de conexão).

## Observações
O arquivo banco_bancario.db é criado dentro da pasta data/. Se essa pasta não existir, o programa tentará criá-la (caso contrário, um erro de IO será exibido).

O método aplicarJuros() da poupança não é chamado automaticamente; cabe ao sistema (ou futura melhoria) invocá-lo periodicamente.