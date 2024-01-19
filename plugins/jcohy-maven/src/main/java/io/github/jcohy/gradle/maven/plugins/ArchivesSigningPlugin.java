//package io.github.jcohy.gradle.maven;
//
//import org.gradle.api.Action;
//import org.gradle.api.Plugin;
//import org.gradle.api.Project;
//import org.gradle.api.plugins.ExtraPropertiesExtension;
//import org.gradle.plugins.signing.SigningPlugin;
//
///**
// * 描述: .
// * <p>
// * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
// * </p>
// *
// * @author jiac
// * @version 2022.04.0 2023/3/21:11:27
// * @since 2022.04.0
// */
//public class ArchivesSigningPlugin implements Plugin<Project> {
//
//    @Override
//    public void apply(Project project) {
//        project.getPluginManager().apply(SigningPlugin.class);
//
//        project.getPlugins().withType(SigningPlugin.class).all(signingPlugin -> {
//            project.hasProperty()
//        });
//    }
//
//
//    public String getExtraProperties(Project project,String property){
//        ExtraPropertiesExtension extra = project.getExtensions().getExtraProperties();
//        return extra.has(property) ? (String) extra.get(property) : "";
//    }
//}
