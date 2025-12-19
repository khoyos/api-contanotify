package co.java.app.contanotify.dto;

public class CardGeneralDTO {
    private int porHacer;
    private int pendientePorDocs;
    private int elaboracion;
    private int declaradoPresentado;
    private int vencidas;

    public int getPorHacer() { return porHacer; }

    public void setPorHacer(int porHacer) { this.porHacer = porHacer; }

    public int getPendientePorDocs() { return pendientePorDocs; }

    public void setPendientePorDocs(int pendientePorDocs) { this.pendientePorDocs = pendientePorDocs; }

    public int getElaboracion() { return elaboracion; }

    public void setElaboracion(int elaboracion) { this.elaboracion = elaboracion; }

    public int getDeclaradoPresentado() { return declaradoPresentado; }

    public void setDeclaradoPresentado(int declaradoPresentado) { this.declaradoPresentado = declaradoPresentado; }

    public int getVencidas() { return vencidas; }

    public void setVencidas(int vencidas) { this.vencidas = vencidas; }
}
