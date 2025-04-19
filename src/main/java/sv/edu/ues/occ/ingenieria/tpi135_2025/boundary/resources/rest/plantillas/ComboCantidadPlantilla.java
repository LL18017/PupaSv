package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas;

public class ComboCantidadPlantilla {
    private Long idCombo;
    private Integer cantidad;

    public ComboCantidadPlantilla(Long idCombo, Integer cantidad) {
        this.idCombo = idCombo;
        this.cantidad = cantidad;
    }

    public ComboCantidadPlantilla() {
    }

    public Long getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(Long idCombo) {
        this.idCombo = idCombo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
