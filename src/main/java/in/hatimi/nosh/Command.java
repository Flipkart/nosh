/*
 * Copyright (c) Flipkart Internet Private Limited - All Rights Reserved.
 *
 * Private and confidential
 *
 * This file cannot be copied and/or distributed, via any medium whatsoever,
 * without express permission from Flipkart Internet Private Limited. Failure to
 * comply shall be considered as unauthorized or illegal usage of this file.
 */

package in.hatimi.nosh;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author indroneel.das
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Command {

    String value() default "";
}
