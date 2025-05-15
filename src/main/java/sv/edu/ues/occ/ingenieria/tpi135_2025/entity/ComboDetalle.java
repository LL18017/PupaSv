/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.entity;

import java.io.Serializable;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 *
 * @author mjlopez
 */
@Entity
@Table(name = "combo_detalle")
@NamedQueries({
    @NamedQuery(name = "ComboDetalle.findAll", query = "SELECT c FROM ComboDetalle c"),
    @NamedQuery(name = "ComboDetalle.findByCantidad", query = "SELECT c FROM ComboDetalle c WHERE c.cantidad = :cantidad"),
    @NamedQuery(name = "ComboDetalle.findByIdProducto", query = "SELECT c FROM ComboDetalle c WHERE c.producto.idProducto = :idProducto"),
    @NamedQuery(name = "ComboDetalle.countByIdProducto", query = "SELECT COUNT (c) FROM ComboDetalle c WHERE c.producto.idProducto = :idProducto"),
    @NamedQuery(name = "ComboDetalle.findByIdCombo", query = "SELECT c FROM ComboDetalle c WHERE c.combo.idCombo = :idCombo"),
    @NamedQuery(name = "ComboDetalle.countByIdCombo", query = "SELECT COUNT (c) FROM ComboDetalle c WHERE c.combo.idCombo = :idCombo"),
    @NamedQuery(name = "ComboDetalle.findByIdComboAndIdProducto", query = "SELECT c FROM ComboDetalle c WHERE c.producto.idProducto = :idProducto and c.combo.idCombo=:idCombo"),
    @NamedQuery(name = "ComboDetalle.countByIdComboAndIdProducto", query = "SELECT COUNT (c) FROM ComboDetalle c WHERE c.producto.idProducto = :idProducto and c.combo.idCombo=:idCombo"),
    @NamedQuery(name = "ComboDetalle.deleteByComboDetallePK", query = "DELETE  FROM ComboDetalle c WHERE c.producto.idProducto = :idProducto and c.combo.idCombo=:idCombo"),
    @NamedQuery(name = "ComboDetalle.findProductoPrecioAndCantidadByIdCombo", query = "SELECT  pp, cd.cantidad  FROM ComboDetalle cd JOIN ProductoPrecio pp ON pp.idProducto.idProducto = cd.producto.idProducto WHERE cd.combo.idCombo = :idCombo"),
    @NamedQuery(name = "ComboDetalle.findProductoPrecioProductoAndCantidadByIdCombo", query = "SELECT  pp,pp.idProducto, cd.cantidad  FROM ComboDetalle cd JOIN ProductoPrecio pp ON pp.idProducto.idProducto = cd.producto.idProducto WHERE cd.combo.idCombo = :idCombo"),
    @NamedQuery(name = "ComboDetalle.findProductoPrecioAndCantidadByIdProducto", query = "SELECT  pp ,pp.idProducto.nombre FROM ProductoPrecio pp WHERE pp.idProducto.idProducto = :idProducto"),
    @NamedQuery(name = "ComboDetalle.findByActivo", query = "SELECT c FROM ComboDetalle c WHERE c.activo = :activo"),
    @NamedQuery(name = "ComboDetalle.sumarPrecioTotalByIdCombo",
            query = "SELECT SUM(pp.precioSugerido * cd.cantidad) "
            + "FROM ComboDetalle cd "
            + "JOIN ProductoPrecio pp ON pp.idProducto.idProducto = cd.producto.idProducto "
            + "WHERE cd.combo.idCombo = :idCombo")})

public class ComboDetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ComboDetallePK comboDetallePK;
    @Column(name = "cantidad")
    private Integer cantidad;
    @Column(name = "activo")
    private Boolean activo;

    @JsonbTransient
    @JoinColumn(name = "id_combo", referencedColumnName = "id_combo", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Combo combo;

    @JsonbTransient
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Producto producto;

    public ComboDetalle() {
    }

    public ComboDetalle(ComboDetallePK comboDetallePK) {
        this.comboDetallePK = comboDetallePK;
    }

    public ComboDetalle(long idCombo, long idProducto) {
        this.comboDetallePK = new ComboDetallePK(idCombo, idProducto);
    }

    public ComboDetallePK getComboDetallePK() {
        return comboDetallePK;
    }

    public void setComboDetallePK(ComboDetallePK comboDetallePK) {
        this.comboDetallePK = comboDetallePK;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Combo getCombo() {
        return combo;
    }

    public void setCombo(Combo combo) {
        this.combo = combo;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (comboDetallePK != null ? comboDetallePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComboDetalle)) {
            return false;
        }
        ComboDetalle other = (ComboDetalle) object;
        if ((this.comboDetallePK == null && other.comboDetallePK != null) || (this.comboDetallePK != null && !this.comboDetallePK.equals(other.comboDetallePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.extreciondeentities.entities.ComboDetalle[ comboDetallePK=" + comboDetallePK + " ]";
    }

}
