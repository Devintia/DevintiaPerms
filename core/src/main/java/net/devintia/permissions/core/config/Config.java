package net.devintia.permissions.core.config;

import lombok.Getter;
import net.cubespace.Yamler.Config.YamlConfig;

import java.io.File;

/**
 * @author geNAZt
 * @version 1.0
 */
@Getter
public class Config extends YamlConfig {

    // Default database config values
    private String databaseHost = "";
    private String databaseUser = "";
    private String databasePass = "";
    private int databasePort = 27017;
    private String databaseDatabase = "";

    // Default group config values
    private String defaultGroup = "player";

    public Config( File configFile ) {
        CONFIG_FILE = configFile;
    }

}
