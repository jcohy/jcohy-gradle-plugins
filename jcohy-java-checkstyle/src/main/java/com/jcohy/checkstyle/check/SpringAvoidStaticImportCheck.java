package com.jcohy.checkstyle.check;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.checks.imports.AvoidStarImportCheck;
import com.puppycrawl.tools.checkstyle.checks.imports.AvoidStaticImportCheck;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 检查没有静态导入  {@link AvoidStarImportCheck}. 添加需要排除的静态导入
 * <a href="https://checkstyle.sourceforge.io/config_imports.html#AvoidStaticImport">CheckStyle AvoidStaticImport</a>
 * @author jiac
 * @version 1.0.0 2021/6/21:15:13
 * @since 1.0.0
 */
public class SpringAvoidStaticImportCheck extends AvoidStaticImportCheck {
    
    private static final Set<String> ALWAYS_EXCLUDED;
    
    static {
        Set<String> excludes = new LinkedHashSet<>();
        excludes.add("io.restassured.RestAssured.*");
        excludes.add("org.assertj.core.api.Assertions.*");
        excludes.add("org.assertj.core.api.Assumptions.*");
        excludes.add("org.assertj.core.api.HamcrestCondition.*");
        excludes.add("org.awaitility.Awaitility.*");
        excludes.add("org.hamcrest.CoreMatchers.*");
        excludes.add("org.hamcrest.Matchers.*");
        excludes.add("org.junit.Assert.*");
        excludes.add("org.junit.Assume.*");
        excludes.add("org.junit.internal.matchers.ThrowableMessageMatcher.*");
        excludes.add("org.junit.jupiter.api.Assertions.*");
        excludes.add("org.junit.jupiter.api.Assumptions.*");
        excludes.add("org.mockito.ArgumentMatchers.*");
        excludes.add("org.mockito.BDDMockito.*");
        excludes.add("org.mockito.Matchers.*");
        excludes.add("org.mockito.AdditionalMatchers.*");
        excludes.add("org.mockito.Mockito.*");
        excludes.add("org.springframework.boot.configurationprocessor.ConfigurationMetadataMatchers.*");
        excludes.add("org.springframework.boot.configurationprocessor.TestCompiler.*");
        excludes.add("org.springframework.boot.test.autoconfigure.AutoConfigurationImportedCondition.*");
        excludes.add("org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo");
        excludes.add("org.springframework.restdocs.headers.HeaderDocumentation.*");
        excludes.add("org.springframework.restdocs.hypermedia.HypermediaDocumentation.*");
        excludes.add("org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*");
        excludes.add("org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*");
        excludes.add("org.springframework.restdocs.operation.preprocess.Preprocessors.*");
        excludes.add("org.springframework.restdocs.payload.PayloadDocumentation.*");
        excludes.add("org.springframework.restdocs.request.RequestDocumentation.*");
        excludes.add("org.springframework.restdocs.restassured3.operation.preprocess.RestAssuredPreprocessors.*");
        excludes.add("org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*");
        excludes.add("org.springframework.restdocs.snippet.Attributes.*");
        excludes.add("org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*");
        excludes.add("org.springframework.security.config.Customizer.*");
        excludes.add("org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*");
        excludes.add("org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*");
        excludes.add("org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*");
        excludes.add("org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*");
        excludes.add("org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*");
        excludes.add("org.springframework.test.web.client.ExpectedCount.*");
        excludes.add("org.springframework.test.web.client.match.MockRestRequestMatchers.*");
        excludes.add("org.springframework.test.web.client.response.MockRestResponseCreators.*");
        excludes.add("org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*");
        excludes.add("org.springframework.test.web.servlet.result.MockMvcResultHandlers.*");
        excludes.add("org.springframework.test.web.servlet.result.MockMvcResultMatchers.*");
        excludes.add("org.springframework.test.web.servlet.setup.MockMvcBuilders.*");
        excludes.add("org.springframework.web.reactive.function.BodyInserters.*");
        excludes.add("org.springframework.web.reactive.function.server.RequestPredicates.*");
        excludes.add("org.springframework.web.reactive.function.server.RouterFunctions.*");
        excludes.add("org.springframework.ws.test.client.RequestMatchers.*");
        excludes.add("org.springframework.ws.test.client.ResponseCreators.*");
        ALWAYS_EXCLUDED = Collections.unmodifiableSet(excludes);
    }
    
    public SpringAvoidStaticImportCheck() {
        setExcludes(ALWAYS_EXCLUDED.toArray(new String[0]));
    }
    
    @Override
    public void setExcludes(String... excludes) {
        Set<String> merged = new LinkedHashSet<>(ALWAYS_EXCLUDED);
        merged.addAll(Arrays.asList(excludes));
        super.setExcludes(merged.toArray(new String[0]));
    }
    
}
