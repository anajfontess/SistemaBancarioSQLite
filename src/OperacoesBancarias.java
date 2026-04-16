public interface OperacoesBancarias {
    void depositar(double valor);
    boolean sacar(double valor);
    double consultarSaldo();
}
