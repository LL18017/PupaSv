/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.io.Serializable;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

/**
 *
 * @author mjlopez
 */
@Path("TipoProducto")
public class TipoProductoResource extends AbstracDataSource<TipoProducto> implements Serializable {

    @Inject
    TipoProductoBean tpBean;

    @Override
    public AbstractDataAccess<TipoProducto> getBean() {
        return tpBean;
    }

    @Override
    public Number getId(TipoProducto registro) {
        return registro.getIdTipoProducto();
    }

    @Override
    public String getClassName() {
        return "TipoProducto";
    }

}
