/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * © 2018 Feedzai, Strictly Confidential
 */

package com.feedzai.commons.sample;
import org.junit.Assert;
import org.junit.Test;

public class SampleTest {
    /**
     * Test that sample returns the init value.
     */
    @Test
    public void testValue() {
        final String foo = "foo";
        final Sample sample = new Sample(foo);

        Assert.assertEquals("Sample should return init value", foo, sample.getValue());
    }
}
