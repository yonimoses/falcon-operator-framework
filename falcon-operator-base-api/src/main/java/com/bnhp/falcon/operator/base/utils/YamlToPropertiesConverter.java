package com.bnhp.falcon.operator.base.utils;
import com.bnhp.falcon.operator.base.exception.YamlParserException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

public final class YamlToPropertiesConverter {

    /**
     * Extract a flat representation of a Yaml file into a map of key-value pairs.
     *
     * @param inputStream the stream holding the yaml data
     * @return the map with key-value pairs.
     * @throws Exception when the yaml file uses a very high-number of hierarchies, thus causing the
     *                                maximum depth of the stack to be reached. Augmenting the allocated memory may
     *                                help (see {@code -Xss} JVM argument.
     */
    static Properties convertToProperties(final InputStream inputStream)  throws YamlParserException {

        final Yaml yaml = new Yaml();
        final Object object = yaml.load(new UnicodeReader(inputStream));
        return parse(object);

    }
    /**
     * Extract a flat representation of a Yaml file into a map of key-value pairs.
     *
     * @param yamlString the string holding the yaml data
     * @return the map with key-value pairs.
     * @throws Exception when the yaml file uses a very high-number of hierarchies, thus causing the
     *                                maximum depth of the stack to be reached. Augmenting the allocated memory may
     *                                help (see {@code -Xss} JVM argument.
     */
    static Properties convertToProperties(final String yamlString)  throws YamlParserException {
        final Yaml yaml = new Yaml();
        final Object object = yaml.load(yamlString);
        return parse(object);
    }


    private static Properties parse(final Object object ) throws YamlParserException {
        final Properties properties = new Properties();
        if (object instanceof Map) {
            try {
                final Map map = (Map<String, Object>) object;
                final Map<String, String> flatMap = flattenMap(map);
                properties.putAll(flatMap);
            } catch (final StackOverflowError e) {
                throw new YamlParserException("The Yaml file has too many hierarchies", e);
            }
        }

        return properties;
    }
    private static Map<String, String> flattenMap(final Map mapOfObjects) {
        final Map<String, Object> mapOfMaps = toHierarchicalMap(mapOfObjects);
        final Map<String, Object> flattenedMap = toFlatMap(mapOfMaps);

        final Map<String, String> propertiesMap = new LinkedHashMap<String, String>();
        for (final Map.Entry<String, Object> entry : flattenedMap.entrySet()) {
            propertiesMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }

        return propertiesMap;
    }

    private static Map<String, Object> toHierarchicalMap(final Object content) {
        final Map<String, Object> dataMap = new LinkedHashMap<String, Object>();

        for (final Map.Entry<String, Object> entry : ((Map<String, Object>) content).entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            toHierarchicalValue(dataMap, key, value);
        }

        return dataMap;
    }

    private static void toHierarchicalValue(Map<String, Object> dataMap, String key, Object value) {
        if (value instanceof Map) {
            dataMap.put(key, toHierarchicalMap(value));
        } else if (value instanceof Collection) {
            for (final Object element : ((Collection) value)) {
                toHierarchicalValue(dataMap,key,element);
            }
        } else {
            dataMap.put(key, value == null ? "" : value);
        }
    }

    private static Map<String, Object> toFlatMap(final Map<String, Object> source) {
        final Map<String, Object> flattenedMap = new LinkedHashMap<String, Object>();

        for (final String key : source.keySet()) {
            final Object value = source.get(key);

            if (value instanceof Map) {
                final Map<String, Object> nestedMap = toFlatMap((Map<String, Object>) value);

                for (final String nestedKey : nestedMap.keySet()) {
                    flattenedMap.put(String.format("%s.%s", key, nestedKey), nestedMap.get(nestedKey));
                }
            } else if (value instanceof Collection) {
                final StringBuilder stringBuilder = new StringBuilder();

                boolean firstElement = true;
                for (final Object element : ((Collection) value)) {
                    final Map<String, Object> subMap = toFlatMap(Collections.singletonMap(key, element));
                    if (firstElement) {
                        stringBuilder.append(",");
                    }

                    stringBuilder.append(subMap.entrySet().iterator().next().getValue().toString());
                    firstElement = false;
                }

                flattenedMap.put(key, stringBuilder.toString());
            } else {
                flattenedMap.put(key, value == null ? "" : value);
            }
        }

        return flattenedMap;
    }

}