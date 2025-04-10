package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

import java.io.Serializable;

@Path("combo")
public class ComboResource extends AbstracDataResource<Combo> implements Serializable {
    @Inject
    ComboBean comboBean;
    @Override
    public AbstractDataAccess<Combo> getBean() {
        return comboBean;
    }

    @Override
    public Object getId(Combo registro) {
        return registro.getIdCombo();
    }

    @Override
    public String getClassName() {
        return "Combo";
    }
}
