package com.little.galaxy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface LockUsed {
	/** Identifies the lock that is implicitly used.  It is recommended
	  * that the lock be specified as either a field name or a method
	  * that is suitable for obtaining the lock object.  For example,
	  * <code>@LockUsed("myReentrantLock")</code> or
	  * <code>@LockUsed("GlobalLocks.getGlobalReadLock()")</code>.
	  * It should match the string specified by {@link LockNeeded}
	  * annotations for the same lock.
	  */
	String[] value();
}
