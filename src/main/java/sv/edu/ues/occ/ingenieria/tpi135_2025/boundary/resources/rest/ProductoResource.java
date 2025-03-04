/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.io.Serializable;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

/**
 *
 * @author mjlopez
 */
@Path("producto")
public class ProductoResource extends AbstracDataSource<Producto> implements Serializable {

    @Inject
    ProductoBean pBean;

    @Override
    public AbstractDataAccess<Producto> getBean() {
        return pBean;
    }

    @Override
    public Number getId(Producto registro) {
        return registro.getIdProducto();
    }

    @Override
    public String getClassName() {
return "Producto";}

}
