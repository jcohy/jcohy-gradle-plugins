package io.github.jcohy.gradle.testing;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gradle.api.DefaultTask;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.tooling.events.FinishEvent;
import org.gradle.tooling.events.OperationCompletionListener;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:  {@link BuildService} 提供构建中所有测试失败的概述.
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/11:15:46
 * @since 0.0.5.1
 */
public abstract class TestResultsOverview
        implements BuildService<BuildServiceParameters.None>, OperationCompletionListener, AutoCloseable {

    private final Map<Test, List<TestFailure>> testFailures = new TreeMap<>(Comparator.comparing(DefaultTask::getPath));

    private final Object monitor = new Object();

    void addFailures(Test test, List<TestDescriptor> failureDescriptors) {
        List<TestFailure> testFailures = failureDescriptors.stream().map(TestFailure::new).sorted()
                .collect(Collectors.toList());
        synchronized (this.monitor) {
            this.testFailures.put(test, testFailures);
        }
    }

    @Override
    public void onFinish(FinishEvent event) {
        // OperationCompletionListener is implemented to defer close until the build ends
    }

    @Override
    public void close() {
        synchronized (this.monitor) {
            if (this.testFailures.isEmpty()) {
                return;
            }
            System.err.println();
            System.err.println("Found test failures in " + this.testFailures.size() + " test task"
                    + ((this.testFailures.size() == 1) ? ":" : "s:"));
            this.testFailures.forEach((task, failures) -> {
                System.err.println();
                System.err.println(task.getPath());
                failures.forEach((failure) -> System.err
                        .println("    " + failure.descriptor.getClassName() + " > " + failure.descriptor.getName()));
            });
        }
    }

	private record TestFailure(TestDescriptor descriptor) implements Comparable<TestFailure> {
		@Override
		public int compareTo(TestFailure other) {
			int comparison = this.descriptor.getClassName().compareTo(Objects.requireNonNull(other.descriptor.getClassName()));
			if (comparison == 0) {
				comparison = this.descriptor.getName().compareTo(other.descriptor.getName());
			}
			return comparison;
		}
	}

}
