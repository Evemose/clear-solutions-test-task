package org.users.core.testutils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Properties;

@Slf4j
public abstract class PropertiesAwareTest {
    protected final Properties properties;

    private InputStream guardFromNullStream(String resourceName) {
        var stream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) {
            log.warn("Resource {} not found", resourceName);
            return InputStream.nullInputStream();
        }
        return stream;
    }

    public PropertiesAwareTest() {
        properties = new Properties();
        try (var main = guardFromNullStream("application.properties");
             var test = guardFromNullStream("application-test.properties");
             var input = new SequenceInputStream(main, test)) {
            properties.load(input);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load properties", ex);
        }
    }
}
