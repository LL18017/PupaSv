package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

public class GenericControlIT {
    static String nombreDb = "TipicosSVtest";
    static String password = "abc123";
    static String username = "postgres";
    static String dockerImagenName = "postgres:16-alpine";
    static String networkAlias = "db";
    static String withInitScript = "tipicos_tpi135_2025.sql";
    static int puerto = 5432;
}
