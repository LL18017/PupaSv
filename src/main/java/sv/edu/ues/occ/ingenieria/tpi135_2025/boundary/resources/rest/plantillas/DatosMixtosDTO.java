package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas;

import java.util.List;

public class DatosMixtosDTO {
    private List<ComboCantidadPlantilla> comboList;
    private List<ProductoCantidadPLantilla> productoList;

    public DatosMixtosDTO() {
    }

    public DatosMixtosDTO(List<ComboCantidadPlantilla> comboList, List<ProductoCantidadPLantilla> productoList) {
        this.comboList = comboList;
        this.productoList = productoList;
    }

    public List<ComboCantidadPlantilla> getComboList() {
        return comboList;
    }

    public void setComboList(List<ComboCantidadPlantilla> comboList) {
        this.comboList = comboList;
    }

    public List<ProductoCantidadPLantilla> getProductoList() {
        return productoList;
    }

    public void setProductoList(List<ProductoCantidadPLantilla> productoList) {
        this.productoList = productoList;
    }
}
