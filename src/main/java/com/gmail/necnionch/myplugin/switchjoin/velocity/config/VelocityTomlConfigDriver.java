package com.gmail.necnionch.myplugin.switchjoin.velocity.config;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.slf4j.Logger;

import java.io.*;

public class VelocityTomlConfigDriver {
    protected final Logger log;
    private final File dataFolder;
    private final String fileName;
    private final String resourceFileName;
    protected Toml config;

    public VelocityTomlConfigDriver(Logger logger, File dataFolder, String fileName, String resourceFileName) {
        this.log = logger;
        this.dataFolder = dataFolder;
        this.fileName = fileName;
        this.resourceFileName = resourceFileName;
    }

    public VelocityTomlConfigDriver(Logger logger, File dataFolder) {
        this(logger, dataFolder, "config.toml", "velocity-config.toml");
    }


    public boolean load() {
        try {
            if (!dataFolder.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dataFolder.mkdir();
            }

            File file = new File(dataFolder, fileName);
            if (!file.exists()) {
                try (InputStream inputStream = getClass().getResourceAsStream(resourceFileName);
                     FileOutputStream outputStream = new FileOutputStream(file)) {
                    if (inputStream == null)
                        throw new NullPointerException("No resource file in plugin: " + resourceFileName);
                    ByteStreams.copy(inputStream, outputStream);
                }
            }

            Toml config;
            try (InputStreamReader stream = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)) {
                config = new Toml().read(stream);
            }

            this.config = config;
            if (config != null) {
                return onLoaded(config);
            } else {
                return false;
            }

        } catch (Exception e) {
            log.error("Unable to load \"" + fileName + "\". An error has occurred.");
            log.error(e.getLocalizedMessage());
            return false;
        }
    }

    public boolean save() {
        if (!dataFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dataFolder.mkdir();
        }

        File file = new File(dataFolder, this.fileName);

        boolean result;
        try {

            try (OutputStreamWriter stream = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
                new TomlWriter().write(config, stream);
            }
            result = true;

        } catch (Exception e) {
            log.error("Unable to save \"" + fileName + "\". An error has occurred.");
            log.error(e.getLocalizedMessage());
            result = false;
        }

        return result;
    }

    public boolean onLoaded(Toml config) {
        return true;
    }

}
