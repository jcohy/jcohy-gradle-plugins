package com.jcohy.checkstyle.filter;

import java.lang.reflect.Field;
import java.util.Objects;

import com.puppycrawl.tools.checkstyle.TreeWalkerAuditEvent;
import com.puppycrawl.tools.checkstyle.TreeWalkerFilter;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: {@link TreeWalkerFilter} that can used to relax the {@code 'this.'} requirement when
 * referring to an outer class from an inner class.
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:17:09
 * @since 1.0.0
 */
public class RequiresOuterThisFilter implements TreeWalkerFilter {

	private static final Field ARGS_FIELD = getArgsField();

	@Override
	public boolean accept(TreeWalkerAuditEvent event) {
		LocalizedMessage message = event.getLocalizedMessage();
		if ("require.this.variable".equals(message.getKey())) {
			Object[] args = getArgs(message);
			String prefex = (args.length > 1 ? Objects.toString(args[1]) : null);
			if (prefex != null && prefex.length() > 0) {
				return false;
			}
		}
		return true;
	}

	private Object[] getArgs(LocalizedMessage message) {
		if (ARGS_FIELD == null) {
			throw new IllegalStateException("Unable to extract message args");
		}
		try {
			return (Object[]) ARGS_FIELD.get(message);
		}
		catch (Exception ex) {
			return null;
		}
	}

	private static Field getArgsField() {
		try {
			Field field = LocalizedMessage.class.getDeclaredField("args");
			field.setAccessible(true);
			return field;
		}
		catch (Exception ex) {
			return null;
		}
	}
}
