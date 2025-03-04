/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

/**
 *
 * @author mjlopez
 * ENUMS para el proyecto
 */
public enum ConstantesGlobales {
    //excepciones
     ERROR_ACCESO("error al acceder al repositorio");
     private final String mensaje;

        ConstantesGlobales(String mensaje) {
            this.mensaje = mensaje;
        }

        public String getMensaje() {
            return mensaje;
        }
}
