/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.kloadgen.config.schemaregistry;

import static io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE;
import static io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.BEARER_AUTH_CREDENTIALS_SOURCE;
import static io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.BEARER_AUTH_TOKEN_CONFIG;
import static io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.USER_INFO_CONFIG;
import static net.coru.kloadgen.util.ProducerKeysHelper.FLAG_YES;
import static net.coru.kloadgen.util.SchemaRegistryKeyHelper.SCHEMA_REGISTRY_AUTH_BASIC_TYPE;
import static net.coru.kloadgen.util.SchemaRegistryKeyHelper.SCHEMA_REGISTRY_AUTH_BEARER_KEY;
import static net.coru.kloadgen.util.SchemaRegistryKeyHelper.SCHEMA_REGISTRY_AUTH_BEARER_TYPE;
import static net.coru.kloadgen.util.SchemaRegistryKeyHelper.SCHEMA_REGISTRY_AUTH_FLAG;
import static net.coru.kloadgen.util.SchemaRegistryKeyHelper.SCHEMA_REGISTRY_AUTH_KEY;
import static net.coru.kloadgen.util.SchemaRegistryKeyHelper.SCHEMA_REGISTRY_PASSWORD_KEY;
import static net.coru.kloadgen.util.SchemaRegistryKeyHelper.SCHEMA_REGISTRY_URL;
import static net.coru.kloadgen.util.SchemaRegistryKeyHelper.SCHEMA_REGISTRY_USERNAME_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.coru.kloadgen.model.PropertyMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;

@Getter
@Setter
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class SchemaRegistryConfigElement extends ConfigTestElement implements TestBean, LoopIterationListener {

  private String schemaRegistryUrl;

  private List<PropertyMapping> schemaRegistryProperties;

  @Override
  public void iterationStart(LoopIterationEvent iterEvent) {
    serializeProperties();
  }

  private void serializeProperties() {
    JMeterVariables jMeterVariables = getThreadContext().getVariables();

    Map<String, String> schemaProperties = getProperties();

    jMeterVariables.put(SCHEMA_REGISTRY_URL, checkPropertyOrVariable(getRegistryUrl()));
    if (FLAG_YES.equalsIgnoreCase(schemaProperties.get(SCHEMA_REGISTRY_AUTH_FLAG))) {
      jMeterVariables.put(SCHEMA_REGISTRY_AUTH_FLAG, FLAG_YES);
      if (SCHEMA_REGISTRY_AUTH_BASIC_TYPE.equalsIgnoreCase(schemaProperties.get(SCHEMA_REGISTRY_AUTH_KEY))) {
        jMeterVariables.put(SCHEMA_REGISTRY_AUTH_KEY, SCHEMA_REGISTRY_AUTH_BASIC_TYPE);
        jMeterVariables.put(BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        jMeterVariables.put(USER_INFO_CONFIG,
                            schemaProperties.get(SCHEMA_REGISTRY_USERNAME_KEY) + ":" + schemaProperties.get(SCHEMA_REGISTRY_PASSWORD_KEY));
      } else if (SCHEMA_REGISTRY_AUTH_BEARER_TYPE.equalsIgnoreCase(schemaProperties.get(SCHEMA_REGISTRY_AUTH_KEY))) {
        jMeterVariables.put(SCHEMA_REGISTRY_AUTH_KEY, SCHEMA_REGISTRY_AUTH_BEARER_TYPE);
        jMeterVariables.put(BEARER_AUTH_CREDENTIALS_SOURCE, "STATIC_TOKEN");
        jMeterVariables.put(BEARER_AUTH_TOKEN_CONFIG, schemaProperties.get(SCHEMA_REGISTRY_AUTH_BEARER_KEY));
      }
    }
  }

  private Map<String, String> getProperties() {
    Map<String, String> result = new HashMap<>();
    if (Objects.nonNull(getProperty("schemaRegistryProperties"))) {
      result.putAll(
          this.fromTestElementToPropertiesMap((List<TestElementProperty>) getProperty("schemaRegistryProperties").getObjectValue()));
    } else {
      result.putAll(fromPropertyMappingToPropertiesMap(this.schemaRegistryProperties));
    }
    return result;
  }

  private String checkPropertyOrVariable(String textToCheck) {
    if (textToCheck.matches("\\$\\{__P\\(.*\\)}")) {
      return JMeterContextService.getContext().getProperties().getProperty(textToCheck.substring(6, textToCheck.length() - 2));
    } else if (textToCheck.matches("\\$\\{\\w*}")) {
      return JMeterContextService.getContext().getVariables().get(textToCheck.substring(2, textToCheck.length() - 1));
    } else {
      return textToCheck;
    }
  }

  private String getRegistryUrl() {
    String registryUrl = getPropertyAsString("schemaRegistryUrl");
    if (StringUtils.isBlank(registryUrl)) {
      registryUrl = this.schemaRegistryUrl;
    }
    return registryUrl;
  }

  private Map<String, String> fromTestElementToPropertiesMap(List<TestElementProperty> schemaProperties) {
    Map<String, String> propertiesMap = new HashMap<>();
    for (TestElementProperty property : schemaProperties) {
      PropertyMapping propertyMapping = (PropertyMapping) property.getObjectValue();
      propertiesMap.put(propertyMapping.getPropertyName(), checkPropertyOrVariable(propertyMapping.getPropertyValue()));
    }
    return propertiesMap;
  }

  private Map<String, String> fromPropertyMappingToPropertiesMap(List<PropertyMapping> schemaProperties) {
    Map<String, String> propertiesMap = new HashMap<>();
    for (PropertyMapping propertyMapping : schemaProperties) {
      propertiesMap.put(propertyMapping.getPropertyName(), checkPropertyOrVariable(propertyMapping.getPropertyValue()));
    }
    return propertiesMap;
  }
}
