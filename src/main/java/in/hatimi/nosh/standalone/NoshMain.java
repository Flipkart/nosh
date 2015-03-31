/*
 * Copyright (c) Flipkart Internet Private Limited - All Rights Reserved.
 *
 * Private and confidential
 *
 * This file cannot be copied and/or distributed, via any medium whatsoever,
 * without express permission from Flipkart Internet Private Limited. Failure to
 * comply shall be considered as unauthorized or illegal usage of this file.
 */

package in.hatimi.nosh.standalone;

import in.hatimi.nosh.support.Nosh;

/**
 * @author indroneel.das
 *
 */

public class NoshMain {

    public static void main(String[] args) {
        new Nosh().prepare();
    }
}
