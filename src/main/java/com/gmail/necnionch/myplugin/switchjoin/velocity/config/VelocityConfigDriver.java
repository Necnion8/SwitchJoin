package com.gmail.necnionch.myplugin.switchjoin.velocity.config;

import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class VelocityConfigDriver {
    protected final Logger log;
    private final Path dataFolder;
    private final String fileName;
    private final String resourceFilePath;
    protected final File path;
    protected final YamlConfigurationLoader loader;
    protected CommentedConfigurationNode config;

    public VelocityConfigDriver(Logger logger, Path dataFolder, String fileName, String resourceFilePath) {
        this.log = logger;
        this.dataFolder = dataFolder;
        this.fileName = fileName;
        this.resourceFilePath = resourceFilePath;
        this.path = new File(dataFolder.toFile(), fileName);
        this.loader = YamlConfigurationLoader.builder().file(path).build();
        this.config = loader.createNode();
    }

    public VelocityConfigDriver(Logger logger, Path dataFolder) {
        this(logger, dataFolder, "config.yml", "velocity-config.yml");
    }


    public boolean load() {
        try {
            if (Files.notExists(dataFolder))
                Files.createDirectory(dataFolder);

            if (!path.exists()) {
                try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceFilePath);
                     FileOutputStream outputStream = new FileOutputStream(path)) {
                    if (inputStream == null)
                        throw new NullPointerException("No resource file in plugin: " + resourceFilePath);
                    ByteStreams.copy(inputStream, outputStream);
                }
            }

            config = loader.load();
            return onLoaded(config);

        } catch (IOException e) {
            log.error("Unable to load \"" + fileName + "\": " + e);

        } catch (Exception e) {
            log.error("Unable to load \"" + fileName + "\". An error has occurred.", e);
        }
        return false;
    }

    public boolean save() {
        try {
            if (Files.notExists(dataFolder))
                Files.createDirectory(dataFolder);

            loader.save(config);
            return true;

        } catch (IOException e) {
            log.error("Unable to save \"" + fileName + "\": " + e);

        } catch (Exception e) {
            log.error("Unable to save \"" + fileName + "\". An error has occurred.", e);
        }
        return false;
    }

    public boolean onLoaded(CommentedConfigurationNode config) {
        return true;
    }

}
