package com.little.galaxy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface LockNeeded {
	/** Identifies the lock that is needed.  It is recommended that the
	  * lock be specified as either a field name or a method that is
	  * suitable for obtaining the lock object.  For example,
	  * <code>@LockNeeded("myReentrantLock")</code> or
	  * <code>@LockNeeded("GlobalLocks.getGlobalReadLock()")</code>.
	  */
	String value();
}
