/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.kloadgen.config.valuefileserialized;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;

import net.coru.kloadgen.model.FieldValueMapping;
import net.coru.kloadgen.property.editor.FileSubjectPropertyEditor;
import net.coru.kloadgen.property.editor.NameStrategyPropertyEditor;
import net.coru.kloadgen.property.editor.SchemaConverterPropertyEditor;
import net.coru.kloadgen.property.editor.SchemaTypePropertyEditor;
import net.coru.kloadgen.property.editor.ValueSerializerPropertyEditor;
import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TableEditor;
import org.apache.jmeter.testbeans.gui.TypeEditor;

public class ValueFileSerializedConfigElementBeanInfo extends BeanInfoSupport {

  private static final String VALUE_SUBJECT_NAME = "valueSubjectName";

  private static final String VALUE_SCHEMA_PROPERTIES = "valueSchemaProperties";

  private static final String VALUE_SCHEMA_DEFINITION = "valueSchemaDefinition";

  private static final String VALUE_SCHEMA_TYPE = "valueSchemaType";

  private static final String VALUE_SERIALIZER_PROPERTY = "valueSerializerConfiguration";

  private static final String VALUE_NAME_STRATEGY = "valueNameStrategy";

  public ValueFileSerializedConfigElementBeanInfo() {

    super(ValueFileSerializedConfigElement.class);

    createPropertyGroup("file_serialized_load_generator", new String[]{
        VALUE_NAME_STRATEGY, VALUE_SERIALIZER_PROPERTY, VALUE_SCHEMA_TYPE, VALUE_SUBJECT_NAME, VALUE_SCHEMA_PROPERTIES, VALUE_SCHEMA_DEFINITION
    });

    PropertyDescriptor nameStrategyPropertyProps = property(VALUE_NAME_STRATEGY);
    nameStrategyPropertyProps.setPropertyEditorClass(NameStrategyPropertyEditor.class);
    nameStrategyPropertyProps.setValue(NOT_UNDEFINED, Boolean.TRUE);
    nameStrategyPropertyProps.setValue(DEFAULT, "");
    nameStrategyPropertyProps.setValue(NOT_EXPRESSION, Boolean.FALSE);

    PropertyDescriptor serializerPropertyProps = property(VALUE_SERIALIZER_PROPERTY);
    serializerPropertyProps.setPropertyEditorClass(ValueSerializerPropertyEditor.class);
    serializerPropertyProps.setValue(NOT_UNDEFINED, Boolean.TRUE);
    serializerPropertyProps.setValue(DEFAULT, "");
    serializerPropertyProps.setValue(NOT_EXPRESSION, Boolean.FALSE);

    PropertyDescriptor subjectNameProps = property(VALUE_SUBJECT_NAME);
    subjectNameProps.setPropertyEditorClass(FileSubjectPropertyEditor.class);
    subjectNameProps.setValue(NOT_UNDEFINED, Boolean.TRUE);
    subjectNameProps.setValue(DEFAULT, "");
    subjectNameProps.setValue(NOT_EXPRESSION, Boolean.FALSE);

    PropertyDescriptor schemaType = property(VALUE_SCHEMA_TYPE);
    schemaType.setPropertyEditorClass(SchemaTypePropertyEditor.class);
    schemaType.setValue(NOT_UNDEFINED, Boolean.TRUE);
    schemaType.setValue(DEFAULT, "");
    schemaType.setValue(NOT_EXPRESSION, Boolean.FALSE);

    PropertyDescriptor avroSchemaProps = property(VALUE_SCHEMA_DEFINITION);
    avroSchemaProps.setPropertyEditorClass(SchemaConverterPropertyEditor.class);
    avroSchemaProps.setValue(NOT_UNDEFINED, Boolean.TRUE);
    avroSchemaProps.setValue(DEFAULT, "");
    avroSchemaProps.setValue(NOT_EXPRESSION, Boolean.FALSE);

    TypeEditor tableEditor = TypeEditor.TableEditor;
    PropertyDescriptor tableProperties = property(VALUE_SCHEMA_PROPERTIES, tableEditor);
    tableProperties.setValue(TableEditor.CLASSNAME, FieldValueMapping.class.getName());
    tableProperties.setValue(TableEditor.HEADERS,
                             new String[]{
                                 "Field Name",
                                 "Field Type",
                                 "Field Length",
                                 "Field Values List"
                             });
    tableProperties.setValue(TableEditor.OBJECT_PROPERTIES,
                             new String[]{
                                 FieldValueMapping.FIELD_NAME,
                                 FieldValueMapping.FIELD_TYPE,
                                 FieldValueMapping.VALUE_LENGTH,
                                 FieldValueMapping.FIELD_VALUES_LIST
                             });
    tableProperties.setValue(DEFAULT, new ArrayList<>());
    tableProperties.setValue(NOT_UNDEFINED, Boolean.TRUE);
  }
}
