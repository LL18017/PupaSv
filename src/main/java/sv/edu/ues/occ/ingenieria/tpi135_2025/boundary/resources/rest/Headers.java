package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

public class Headers {
    public static final String TOTAL_RECORD="Total-records";
    public static final String UNPROCESSABLE_ENTITY = "Unprocessable-Entity"; // Tipo de contenido (ej. JSON, XML)
    public static final String WRONG_PARAMETER = "Wrong-parameter"; // Tipo de contenido (ej. JSON, XML)
    public static final String NOT_FOUND_ID = "not-found-id"; // Tipo de contenido (ej. JSON, XML)
    public static final String PROCESS_ERROR = "process-error"; // Tipo de contenido (ej. JSON, XML)
    public static final String AUTHORIZATION = "Authorization"; // Token de autorización (si es necesario)
    public static final String LOCATION = "Location"; // Localización de un recurso recién creado (usualmente para POST)
    public static final String X_REQUEST_ID = "X-Request-ID"; // Identificador único de la solicitud
    public static final String ACCEPT_LANGUAGE = "Accept-Language"; // Idioma preferido del cliente
    public static final String CACHE_CONTROL = "Cache-Control";
}
