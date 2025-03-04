/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.io.Serializable;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

/**
 *
 * @author mjlopez
 * implementacion para exponer recurso rest para Orden
 */
@Path("orden")
public class OrdenResource extends AbstracDataSource<Orden> implements Serializable{
    @Inject
    OrdenBean ordenBean;
    
    @Override
    public AbstractDataAccess<Orden> getBean() {
        return ordenBean;
    }

    @Override
    public Long getId(Orden registro) {
        return registro.getIdOrden();
    }

    @Override
    public String getClassName() {
        return "Orden";
    }
    
}
