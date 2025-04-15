package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import java.util.List;

public class DatosMixtosDTO {
    private Long idOrden;
    private List<Long> idProductos;
    private List<Long> idCombos;
    private Integer cantidadProductos;
    private Integer cantidadCombo;

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

    public Integer getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(Integer cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }

    public Integer getCantidadCombo() {
        return cantidadCombo;
    }

    public void setCantidadCombo(Integer cantidadCombo) {
        this.cantidadCombo = cantidadCombo;
    }
}
