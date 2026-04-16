import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class EntradaUsuario {
    private Scanner scanner;
    
    public EntradaUsuario() {
        this.scanner = new Scanner(System.in);
    }
    
    public String lerString(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }
    
    public int lerInt(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            } catch (Exception e) {
                System.out.println("XXXXX Valor inválido! Digite um número inteiro. XXXXX");
                scanner.nextLine();
            }
        }
    }
    
    public double lerDouble(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String input = scanner.nextLine().trim();
                
                // Remove possíveis problemas
                input = input.replace(',', '.')
                            .replace(" ", "")
                            .replace("R$", "")
                            .replace("$", "");
                
                if (input.isEmpty()) {
                    System.out.println("XXXXX Entrada vazia! Digite um número. XXXXX");
                    continue;
                }
                double valor = Double.parseDouble(input);
                return valor;
            } catch (Exception e) {
                System.out.println("XXXXX Valor inválido! Digite um número decimal. XXXXX");
                scanner.nextLine();
            }
        }
    }
    
    public LocalDate lerData(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem + " (AAAA-MM-DD): ");
                String dataStr = scanner.nextLine().trim();
                
                // Validação adicional
                if (dataStr.isEmpty()) {
                    System.out.println("XXXXX Data não pode ser vazia! XXXXX");
                    continue;
                }
                
                return LocalDate.parse(dataStr);
            } catch (DateTimeParseException e) {
                System.out.println("XXXXX Formato de data inválido! Use AAAA-MM-DD. XXXXX");
            }
        }
    }
    
    public String lerCPF(String mensagem) {
        while (true) {
            String cpf = lerString(mensagem).replaceAll("[^0-9]", "");
            if (cpf.length() == 11) {
                return cpf;
            }
            System.out.println("XXXXX CPF inválido! Digite 11 números. XXXXX");
        }
    }
    
    public String lerCNPJ(String mensagem) {
        while (true) {
            String cnpj = lerString(mensagem).replaceAll("[^0-9]", "");
            if (cnpj.length() == 14) {
                return cnpj;
            }
            System.out.println("XXXXX CNPJ inválido! Digite 14 números. XXXXX");
        }
    }
    
    public double lerDoublePositivo(String mensagem) {
        while (true) {
            double valor = lerDouble(mensagem);
            if (valor >= 0) {
                return valor;
            }
            System.out.println("XXXXX Valor deve ser positivo! XXXXX");
        }
    }
    
    public int lerIntIntervalo(String mensagem, int min, int max) {
        while (true) {
            int valor = lerInt(mensagem);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.println("XXXXX Valor deve estar entre " + min + " e " + max + "! XXXXX");
        }
    }
    
    public void fechar() {
        scanner.close();
    }
}
