package com.jcohy.checkstyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.puppycrawl.tools.checkstyle.AuditEventDefaultFormatter;
import com.puppycrawl.tools.checkstyle.AuditEventFormatter;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.Definitions;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: {@link AuditListener} 检查预期事件发生。
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:17:29
 * @since 1.0.0
 */
class AssertionsAuditListener implements AuditListener {

	static final AuditEventFormatter FORMATTER = new AuditEventDefaultFormatter();

	private final List<String> checks;

	private List<String> filenames = new ArrayList<>();

	private final StringBuilder message = new StringBuilder();

	private final Map<SeverityLevel, Integer> severityCounts = new TreeMap<>();

	AssertionsAuditListener(List<String> checks) {
		this.checks = checks;
	}

	@Override
	public void auditStarted(AuditEvent event) {
		recordLevel(event);
		recordLocalizedMessage(DefaultLogger.AUDIT_STARTED_MESSAGE);
		recordLevel(event);
	}

	@Override
	public void auditFinished(AuditEvent event) {
		recordLevel(event);
		recordLocalizedMessage(DefaultLogger.AUDIT_FINISHED_MESSAGE);
		recordMessage(this.severityCounts.toString());
		int errors = this.severityCounts.getOrDefault(SeverityLevel.ERROR, 0);
		recordMessage(errors + (errors == 1 ? " error" : " errors"));
		System.out.println(this.filenames);
		System.out.println(this.message);
		this.checks.forEach(this::runCheck);
	}

	private void runCheck(String check) {
		String description = this.filenames.toString();
		if (check.startsWith("+")) {
			assertThat(this.message.toString()).as(description).contains(check.substring(1));
		}
		else if (check.startsWith("-")) {
			assertThat(this.message.toString()).as(description).doesNotContain(check.substring(1));
		}
	}

	@Override
	public void fileStarted(AuditEvent event) {
		this.filenames.add(event.getFileName());
	}

	@Override
	public void fileFinished(AuditEvent event) {
	}

	@Override
	public void addError(AuditEvent event) {
		recordLevel(event);
		if (event.getSeverityLevel() != SeverityLevel.IGNORE) {
			recordMessage(FORMATTER.format(event));
		}
	}

	@Override
	public void addException(AuditEvent event, Throwable throwable) {
		recordLevel(event);
		recordLocalizedMessage(DefaultLogger.ADD_EXCEPTION_MESSAGE, event.getFileName());
	}

	private void recordLevel(AuditEvent event) {
		this.severityCounts.compute(event.getSeverityLevel(), (level, count) -> (count == null ? 1 : count + 1));
	}

	private void recordLocalizedMessage(String message, String... args) {
		recordMessage(new LocalizedMessage(0, Definitions.CHECKSTYLE_BUNDLE, message, args, null,
				LocalizedMessage.class, null).getMessage());
	}

	private void recordMessage(String message) {
		this.message.append(message);
		this.message.append("\n");
	}

}
