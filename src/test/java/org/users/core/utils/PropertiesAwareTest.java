package org.users.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Properties;

/**
 * Base class for tests that require access to properties,
 * but do not want to initialize Spring context.
 */
@Slf4j
public abstract class PropertiesAwareTest {
    protected final Properties properties;

    // it`s possible to enable custom profiles support, but for now there is no need
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

    private InputStream guardFromNullStream(String resourceName) {
        var stream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) {
            log.warn("Resource {} not found", resourceName);
            return InputStream.nullInputStream();
        }
        return stream;
    }
}
