public abstract class ContaBase implements OperacoesBancarias {
    protected String numero;
    protected int titular;
    protected double saldo;
    
    public ContaBase(String numero, int titular, double saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldoInicial;
    }
    
    public ContaBase(String numero, int titular) {
        this(numero, titular, 0.0);
    }
    
    @Override
    public void depositar(double valor) {
        if (valor > 0) {
            saldo += valor;
            System.out.printf("Depósito de R$%.2f realizado. Saldo: R$%.2f%n", valor, saldo);
        } else {
            System.out.println("Valor de depósito inválido.");
        }
    }
    
    @Override
    public double consultarSaldo() {
        return saldo;
    }
    
    @Override
    public abstract boolean sacar(double valor);
    
    public int getTitular() {
        return titular;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    @Override
    public String toString() {
        return String.format("Conta %s - Titular: %s - Saldo: R$%.2f", numero, titular, saldo);
    }
}
