/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * © 2018 Feedzai, Strictly Confidential
 */

package com.feedzai.commons.sample;

/**
 * The quick brown fox jumps over the lazy dog.
 * @author Marco Jorge (marco.jorge@feedzai.com)
 */
public class Sample {
    private final String value;

    /**
     * The quick brown fox jumps over the lazy dog.
     * @param value foo
     */
    public Sample(final String value) {
        this.value = value;
    }

    /**
     * The quick brown fox jumps over the lazy dog.
     * @return bar
     */
    public String getValue() {
        return value;
    }
}
