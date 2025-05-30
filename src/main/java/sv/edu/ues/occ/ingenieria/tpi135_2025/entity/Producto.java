/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;

/**
 *
 * @author mjlopez
 */
@Entity
@Table(name = "producto")
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", query = "SELECT p FROM Producto p"),
    @NamedQuery(name = "Producto.findActivosAndIdTipoProducto", query = "SELECT p FROM ProductoDetalle pd JOIN pd.producto p WHERE p.activo=:activo AND pd.productoDetallePK.idTipoProducto=:idTipoProducto"),
    @NamedQuery(name = "Producto.countActivosAndIdTipoProducto", query = "SELECT DISTINCT COUNT(p) FROM ProductoDetalle pd JOIN pd.producto p WHERE p.activo=:activo AND pd.productoDetallePK.idTipoProducto=:idTipoProducto"),
    @NamedQuery(name = "Producto.findByIdProducto", query = "SELECT p FROM Producto p WHERE p.idProducto = :idProducto"),
    @NamedQuery(name = "Producto.findByNombre", query = "SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)"),
    @NamedQuery(name = "Producto.countByNombre", query = "SELECT COUNT(p) FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)"),
    @NamedQuery(name = "Producto.findByAnyActivo", query = "SELECT p FROM Producto p WHERE p.activo = :activo"),
    @NamedQuery(name = "Producto.countByAnyActivo", query = "SELECT COUNT(p) FROM Producto p WHERE p.activo = :activo"),
    @NamedQuery(name = "Producto.findByObservaciones", query = "SELECT p FROM Producto p WHERE p.observaciones = :observaciones"),
    @NamedQuery(name = "Producto.findByIdTipoProductoAndActivo", query = "SELECT DISTINCT p FROM ProductoDetalle pd JOIN pd.producto p WHERE pd.tipoProducto.idTipoProducto = :idTipoProducto AND p.activo=:activo"),
    @NamedQuery(name = "Producto.findByidTipoProducto", query = "SELECT DISTINCT (p) FROM ProductoDetalle pd JOIN pd.producto p WHERE pd.tipoProducto.idTipoProducto = :idTipoProducto"),
    @NamedQuery(name = "Producto.countByidTipoProducto", query = "SELECT DISTINCT count (p) FROM ProductoDetalle pd JOIN pd.producto p WHERE pd.tipoProducto.idTipoProducto = :idTipoProducto"),


})
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_producto")
    private Long idProducto;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "url")
    private String url;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.EAGER)
    @JsonbTransient
    private List<ComboDetalle> comboDetalleList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto")
    @JsonbTransient
    private List<ProductoDetalle> productoDetalleList;


//    @JsonbTransient
    @OneToMany(mappedBy = "idProducto")
    private List<ProductoPrecio> productoPrecioList;

    public Producto() {
    }

    public Producto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ComboDetalle> getComboDetalleList() {
        return comboDetalleList;
    }

    public void setComboDetalleList(List<ComboDetalle> comboDetalleList) {
        this.comboDetalleList = comboDetalleList;
    }
    public List<ProductoDetalle> getProductoDetalleList() {
        return productoDetalleList;
    }

    public void setProductoDetalleList(List<ProductoDetalle> productoDetalleList) {
        this.productoDetalleList = productoDetalleList;
    }

    public List<ProductoPrecio> getProductoPrecioList() {
        return productoPrecioList;
    }

    public void setProductoPrecioList(List<ProductoPrecio> productoPrecioList) {
        this.productoPrecioList = productoPrecioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProducto != null ? idProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.idProducto == null && other.idProducto != null) || (this.idProducto != null && !this.idProducto.equals(other.idProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.extreciondeentities.entities.Producto[ idProducto=" + idProducto + " ]";
    }
    
}
