package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas;

public class ProductoCantidadPLantilla {
    Long idProducto;
    Integer cantidad;

    public ProductoCantidadPLantilla(Long idProducto, Integer cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public ProductoCantidadPLantilla() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
