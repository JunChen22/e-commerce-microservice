package com.itsthatjun.ecommerce.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

// This plugin is used to enhance the generated model classes with Lombok annotations and Spring Data annotations.
// The plugin adds the following annotations to the model classes:
public class ModelAnnotationEnhancerPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true; // Always valid
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("org.springframework.data.relational.core.mapping.Table");

        topLevelClass.addImportedType("lombok.Getter");
        topLevelClass.addImportedType("lombok.Setter");

        // Add Lombok annotations
        topLevelClass.addAnnotation("@Getter");
        topLevelClass.addAnnotation("@Setter");

        // Add @Table annotation
        topLevelClass.addAnnotation("@Table(\"" + introspectedTable.getFullyQualifiedTable() + "\")");
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        // Check if the field is an identity field
        if (introspectedColumn.isIdentity()) {
            field.addAnnotation("@Id");
            // Add import statement for @Id annotation
            topLevelClass.addImportedType("org.springframework.data.annotation.Id");
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }
}