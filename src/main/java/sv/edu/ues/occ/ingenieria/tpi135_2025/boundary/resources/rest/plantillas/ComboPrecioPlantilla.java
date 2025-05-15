package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas;

import java.math.BigDecimal;

public class ComboPrecioPlantilla {
    public Long idCombo;
    public Boolean activo;
    public String nombre;
    public String descripcion;
    public String url;
    public BigDecimal precio;

    public ComboPrecioPlantilla(Long idCombo, Boolean activo, String nombre, String descripcion, String url, BigDecimal precio) {
        this.idCombo = idCombo;
        this.activo = activo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.url = url;
        this.precio = precio;
    }

    public ComboPrecioPlantilla() {
        this.idCombo = idCombo;
    }

    public Long getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(Long idCombo) {
        this.idCombo = idCombo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
