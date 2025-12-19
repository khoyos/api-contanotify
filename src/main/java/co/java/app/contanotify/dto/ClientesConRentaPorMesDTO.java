package co.java.app.contanotify.dto;

public class ClientesConRentaPorMesDTO {

    private String name;   // Mes abreviado (Ene, Feb, etc.)
    private int clientes;  // Cantidad de clientes con renta + pago

    public ClientesConRentaPorMesDTO() {}

    public ClientesConRentaPorMesDTO(String name, int clientes) {
        this.name = name;
        this.clientes = clientes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getClientes() {
        return clientes;
    }

    public void setClientes(int clientes) {
        this.clientes = clientes;
    }
}
