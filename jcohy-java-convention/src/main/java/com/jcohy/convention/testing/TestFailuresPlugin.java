package com.jcohy.convention.testing;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestListener;
import org.gradle.api.tasks.testing.TestResult;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 此插件用于记录测试失败并在构建结束时生成报告。
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/11:15:42
 * @since 0.0.5.1
 */
public class TestFailuresPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Provider<TestResultsOverview> testResultsOverview = project.getGradle().getSharedServices()
                .registerIfAbsent("testResultsOverview", TestResultsOverview.class, (spec) -> {
                
                });
        project.getTasks().withType(Test.class, test ->
                test.addTestListener(new FailureRecordingTestListener(testResultsOverview, test))
        );
    }
    
    private final class FailureRecordingTestListener implements TestListener {
        
        private final List<TestDescriptor> failures = new ArrayList<>();
        
        private final Provider<TestResultsOverview> testResultsOverview;
        
        private final Test test;
        
        private FailureRecordingTestListener(Provider<TestResultsOverview> testResultOverview, Test test) {
            this.testResultsOverview = testResultOverview;
            this.test = test;
        }
        
        @Override
        public void beforeSuite(TestDescriptor suite) {
        
        }
        
        @Override
        public void afterSuite(TestDescriptor suite, TestResult result) {
            if (!this.failures.isEmpty()) {
                this.testResultsOverview.get().addFailures(this.test, this.failures);
            }
        }
        
        @Override
        public void beforeTest(TestDescriptor testDescriptor) {
        
        }
        
        @Override
        public void afterTest(TestDescriptor testDescriptor, TestResult result) {
            if (result.getFailedTestCount() > 0) {
                this.failures.add(testDescriptor);
            }
        }
    }
}
