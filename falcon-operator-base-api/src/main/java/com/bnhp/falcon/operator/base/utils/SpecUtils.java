package com.bnhp.falcon.operator.base.utils;

import com.bnhp.falcon.operator.base.exception.YamlParserException;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Properties;

public final class SpecUtils {

    public static Properties toProperties(String payload) throws YamlParserException {
      return YamlToPropertiesConverter.convertToProperties(payload);

    }
}
