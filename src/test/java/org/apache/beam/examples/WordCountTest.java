/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.examples;

import org.apache.beam.examples.WordCount.CountWords;
import org.apache.beam.examples.WordCount.FormatAsTextFn;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests of WordCount.
 */
@RunWith(JUnit4.class)
public class WordCountTest {

    // Set up Test data (input and output/expected)
    private static final String[] WORDS_ARRAY = new String[]{
            "hi there", "hi", "hi sue bob",
            "hi sue", "", "bob hi"};
    private static final String[] WORDS_USED = new String[]{
            "hi", "there", "sue", "bob"};
    private static final List<String> WORDS_USED_LIST = Arrays.asList(WORDS_USED);
    private static final List<String> WORDS = Arrays.asList(WORDS_ARRAY);
    private static final String[] COUNTS_ARRAY = new String[]{
            "hi: 5", "there: 1", "sue: 2", "bob: 2"};

    // Creates a Test Pipeline (can define different PipelineOptions)
    // By using @Rule, we get the TestPipeline "refreshed" between each unit test (@Test)
    @Rule
    public TestPipeline p = TestPipeline.create();

    /**
     * Example test that tests a PTransform by using an in-memory input and inspecting the output.
     */
    @Test
    @Category(ValidatesRunner.class)
    public void testCountWords() {
        // define input PCollection in the testing pipeline
        PCollection<String> input = p.apply(Create.of(WORDS).withCoder(StringUtf8Coder.of()));
        // apply the CountWords Composite Transform to the "input" PCollection
        PCollection<String> output = input.apply(new CountWords())
                .apply(MapElements.via(new FormatAsTextFn()));
        // asserts that the produced "output" PCollection contains the expected output
        PAssert.that(output).containsInAnyOrder(COUNTS_ARRAY);
        // blocks and waits for the pipeline to finish
        p.run().waitUntilFinish();
    }

    /**
     * Test FormatAsTexFn.
     */
    @Test
    @Category(ValidatesRunner.class)
    public void testExtractWordsFn() {
        // define input PCollection in the testing pipeline
        PCollection<String> input = p.apply(Create.of(WORDS).withCoder(StringUtf8Coder.of()));
        // Convert words of text into individual words.
        PCollection<String> words = input.apply(
                ParDo.of(new WordCount.ExtractWordsFn()));
        // method 1: asserts that the tokenized words contain the expected output words
        PAssert.that(words).satisfies((SerializableFunction<Iterable<String>, Void>) inIt -> {
            // checks if the WORDS_USED contains all the words extracted
            inIt.forEach(in ->
                    assertTrue(WORDS_USED_LIST.contains(in))
            );
            return null;
        });
        // method 2: define the SerializableFunction as a separate function (useful if used in multiple places)
        PAssert.that(words).satisfies(new VerifyWords(WORDS_USED_LIST));
        // blocks and waits for the pipeline to finish
        p.run().waitUntilFinish();
    }

    /**
     * Auxiliary class to verify if words exist
     */
    private static class VerifyWords implements SerializableFunction<Iterable<String>, Void> {
        private final List<String> originalWords;

        private VerifyWords(List<String> originalWorlds) {
            this.originalWords = originalWorlds;
        }

        @Override
        public Void apply(Iterable<String> actualIter) {
            for (String s : actualIter)
                assertTrue(originalWords.contains(s));
            return null;
        }
    }

}
