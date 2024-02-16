/*
 * Copyright (c) 2009-2024 Yegor Bugayenko
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the rultor.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.rultor.profiles;

import com.jcabi.matchers.XhtmlMatchers;
import com.rultor.spi.Profile;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for ${@link YamlXML}.
 *
 * @since 1.0
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
final class YamlXMLTest {

    /**
     * YamlXML can parse.
     */
    @Test
    void parsesYamlConfig() {
        MatcherAssert.assertThat(
            new YamlXML("a: test\nb: 'hello'\nc:\n  - one\nd:\n  f: e").get(),
            XhtmlMatchers.hasXPaths(
                "/p/entry[@key='a' and .='test']",
                "/p/entry[@key='b' and .='hello']",
                "/p/entry[@key='c']/item[.='one']",
                "/p/entry[@key='d']/entry[@key='f' and .='e']"
            )
        );
    }

    /**
     * YamlXML can parse a broken text.
     */
    @Test
    void parsesYamlConfigWhenBroken() {
        MatcherAssert.assertThat(
            new YamlXML("a: alpha\nb:\nc:\n  - beta").get(),
            XhtmlMatchers.hasXPaths(
                "/p/entry[@key='a' and .='alpha']",
                "/p/entry[@key='b' and .='']",
                "/p/entry[@key='c']/item[.='beta']"
            )
        );
    }

    /**
     * YamlXML can parse a broken text and throw.
     * @param yaml Text to check
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "thre\n\t\\/\u0000",
        "first: \"привет \\/\t\r\""
    })
    void parsesBrokenConfigsAndThrows(final String yaml) {
        Assertions.assertThrows(
            Profile.ConfigException.class,
            () -> new YamlXML(yaml).get()
        );
    }

    /**
     * YamlXML can parse a text with spec symbols after |-.
     */
    @Test
    void parsesGroupContent() {
        MatcherAssert.assertThat(
            new YamlXML("a: alpha\nb: |-\n  echo \"<>some(text);\"").get(),
            XhtmlMatchers.hasXPaths(
                "/p/entry[@key='a' and .='alpha']",
                "/p/entry[@key='b' and .='echo \"<>some(text);\"']"
            )
        );
    }

    /**
     * YamlXML can parse a multiline text after |-.
     */
    @Test
    void parsesMultilineContent() {
        MatcherAssert.assertThat(
            new YamlXML("a: alpha\nb: |-\n  echo \\\n  \"some(text);\"").get(),
            XhtmlMatchers.hasXPaths(
                "/p/entry[@key='a' and .='alpha']",
                "/p/entry[@key='b' and .='echo \\\n\"some(text);\"']"
            )
        );
    }

}
