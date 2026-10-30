package co.edu.unilibre.dao;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * GestorConexiones
 */
public class GestorConexiones {

    private static HikariDataSource dataSource;

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String getDbUrl() {
        return getEnv("DB_URL");
    }

    public static String getDbUser() {
        return getEnv("DB_USER");
    }

    public static String getDbPassword() {
        return getEnv("DB_PASSWORD");
    }

    private static String getEnv(String key) {
        // Intenta leer del .env local; si no existe, busca en las variables de entorno del sistema/nube
        String value = dotenv.get(key);
        return (value != null) ? value : System.getenv(key);
    }

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getDbUrl());
        config.setUsername(getDbUser());
        config.setPassword(getDbPassword());

        // Configuraciones opcionales de optimización
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        dataSource = new HikariDataSource(config);
    }

    public Connection obtenerConexion() throws SQLException {
        return dataSource.getConnection();
    }

    public void inicializarBaseDeDatos(Connection conn) {
        try (InputStream is = GestorConexiones.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (is == null) {
                throw new RuntimeException("No se encontró el archivo schema.sql en resources");
            }

            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Base de datos inicializada correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
