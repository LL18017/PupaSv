package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import java.util.List;

public class DatosMixtosDTO {
    private Long idOrden;
    private List<Long> idProductos;
    private List<Long> idCombos;
    private List<Integer> cantidadProductos;
    private List<Integer> cantidadCombo;

    // Getters y Setters
    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public List<Long> getIdProductos() {
        return idProductos;
    }

    public void setIdProductos(List<Long> idProductos) {
        this.idProductos = idProductos;
    }

    public List<Long> getIdCombos() {
        return idCombos;
    }

    public void setIdCombos(List<Long> idCombos) {
        this.idCombos = idCombos;
    }

    public List<Integer> getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(List<Integer> cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }

    public List<Integer> getCantidadCombo() {
        return cantidadCombo;
    }

    public void setCantidadCombo(List<Integer> cantidadCombo) {
        this.cantidadCombo = cantidadCombo;
    }
}
