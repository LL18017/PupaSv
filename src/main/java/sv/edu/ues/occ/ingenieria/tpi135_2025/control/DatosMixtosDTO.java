package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import java.util.List;

public class DatosMixtosDTO {
    private Long idProductos;
    private Integer cantidadProductos;
    private Long idCombos;
    private Integer cantidadCombo;

    public DatosMixtosDTO(Long idProductos, Integer cantidadProductos, Long idCombos, Integer cantidadCombo) {
        this.idProductos = idProductos;
        this.cantidadProductos = cantidadProductos;
        this.idCombos = idCombos;
        this.cantidadCombo = cantidadCombo;
    }

    public DatosMixtosDTO() {
    }

    public Long getIdProductos() {
        return idProductos;
    }

    public void setIdProductos(Long idProductos) {
        this.idProductos = idProductos;
    }

    public Integer getCantidadCombo() {
        return cantidadCombo;
    }

    public void setCantidadCombo(Integer cantidadCombo) {
        this.cantidadCombo = cantidadCombo;
    }

    public Long getIdCombos() {
        return idCombos;
    }

    public void setIdCombos(Long idCombos) {
        this.idCombos = idCombos;
    }

    public Integer getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(Integer cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }
}
