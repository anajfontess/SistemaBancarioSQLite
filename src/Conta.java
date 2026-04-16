

public class Conta extends ContaBase {
    public Conta(String numero, int titular, double saldoInicial) {
        super(numero, titular, saldoInicial);
    }
    
    public Conta(String numero, int titular) {
        super(numero, titular);
    }
    
    @Override
    public boolean sacar(double valor) {
        if (valor > 0 && valor <= saldo) {
            saldo -= valor;
            System.out.printf("Saque de R$%.2f realizado. Saldo: R$%.2f%n", valor, saldo);
            return true;
        } else {
            System.out.println("Saldo insuficiente ou valor inválido.");
            return false;
        }
    }
}
