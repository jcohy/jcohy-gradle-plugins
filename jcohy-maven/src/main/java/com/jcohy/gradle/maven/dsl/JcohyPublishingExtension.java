package com.jcohy.gradle.maven.dsl;

import org.gradle.api.Action;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.publish.PublicationContainer;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/10/17:17:07
 * @since 2022.0.1
 */
public class JcohyPublishingExtension {

    public static final String JCOHY_MAVEN_PUBLISHING_EXTENSION_NAME = "jcohyPublishing";

    private boolean enable = false;

    private final RepositoryHandler repositories;

    private final PublicationContainer publications;

    public JcohyPublishingExtension(RepositoryHandler repositories, PublicationContainer publications) {
        this.repositories = repositories;
        this.publications = publications;
    }

    public void repositories(Action<? super RepositoryHandler> configure) {
        configure.execute(repositories);
    }

    public void publications(Action<? super PublicationContainer> configure) {
        configure.execute(publications);
    }

    public void enable(Action<Boolean> configure) {
        configure.execute(enable);
    }
}
