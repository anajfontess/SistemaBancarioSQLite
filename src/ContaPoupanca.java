public class ContaPoupanca extends ContaBase {
    private double taxaJuros;
    
    public ContaPoupanca(String numero, int titular, double saldoInicial, double taxaJuros) {
        super(numero, titular, saldoInicial);
        this.taxaJuros = taxaJuros;
    }
    
    public ContaPoupanca(String numero, int titular, double taxaJuros) {
        super(numero, titular);
        this.taxaJuros = taxaJuros;
    }
    
    @Override
    public boolean sacar(double valor) {
        if (valor > 0 && valor <= saldo) {
            saldo -= valor;
            System.out.printf("Saque de R$%.2f realizado da Poupança. Saldo: R$%.2f%n", valor, saldo);
            return true;
        } else {
            System.out.println("Saldo insuficiente na Poupança ou valor inválido.");
            return false;
        }
    }
    
    public void aplicarJuros() {
        double juros = saldo * taxaJuros;
        saldo += juros;
        System.out.printf("Juros de R$%.2f aplicados. Novo saldo: R$%.2f%n", juros, saldo);
    }
    
    public double getTaxaJuros() {
        return taxaJuros;
    }
}