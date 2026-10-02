package com.ucao.dgi.l3.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Permet de configurer la base avec une seule variable DATABASE_URL, au format fourni par Render/Heroku
 * (postgresql://utilisateur:motdepasse@hote:port/base) ou directement au format JDBC.
 * Si elle est absente, les variables DB_HOST, DB_PORT, DB_NAME, DB_USER et DB_PASSWORD s'appliquent.
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        String brute = env.getProperty("DATABASE_URL");
        if (brute == null || brute.isBlank()) {
            return;
        }
        Map<String, Object> props = new HashMap<>();
        String url = brute.trim();
        if (url.startsWith("jdbc:")) {
            props.put("spring.datasource.url", url);
        } else {
            URI uri = URI.create(url);
            int port = uri.getPort() == -1 ? 5432 : uri.getPort();
            String jdbc = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath()
                    + (uri.getQuery() != null ? "?" + uri.getQuery() : "");
            props.put("spring.datasource.url", jdbc);
            if (uri.getRawUserInfo() != null) {
                String[] infos = uri.getRawUserInfo().split(":", 2);
                props.put("spring.datasource.username", decoder(infos[0]));
                if (infos.length > 1) {
                    props.put("spring.datasource.password", decoder(infos[1]));
                }
            }
        }
        env.getPropertySources().addFirst(new MapPropertySource("databaseUrl", props));
    }

    private static String decoder(String valeur) {
        return URLDecoder.decode(valeur, StandardCharsets.UTF_8);
    }
}
