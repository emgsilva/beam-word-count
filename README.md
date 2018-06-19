# Apache Beam Word Count / Hello World (with subtitles)

This project contains my notes on small experiments done with Apache Beam word count examples (based on https://beam.apache.org/get-started/wordcount-example/).

## 1) Minimal Word Count
[src/main/java/org/apache/beam/examples/MinimalWordCount.java](./src/main/java/org/apache/beam/examples/MinimalWordCount.java)

- Defines simple processing pipeline, explaining the basic elements of an Apache Beam processing pipeline
- Summary:

  - `PipelineOptions` object: defines the options that are to be used when building an Apache Beam processing pipeline. These can be passed as command-line arguments or obtained from properties files, etc.;
  - `Pipeline` object: this is the actual pipeline object where we can apply different `transform` operations;
  - `PCollection`: collection used to hold the input/output data of `transform` operations;
  - transform operations: these are the "operations" to be performed on the data (stored as `PCollection`). There are different types of "transforms", example: `Count`; `Filter`; and many other predefined transforms. However, we can also write in-line anonymous functions (or have dedicated functions - all subclasses of `DoFn`) and execute them in a `ParDo` (Parallel Processing, which is similar to the “Map” phase of a Map/Shuffle/Reduce-style algorithm).
  - `.apply(<tranform>)`: the `.apply` method enables to define the chain of "transform" elements of the pipeline.

## 2) Word Count
[src/main/java/org/apache/beam/examples/WordCount.java](./src/main/java/org/apache/beam/examples/WordCount.java)

- Adds several interesting constructs, practices and patterns to write Beam data processing pipelines.
- Summary:

  - `ParDo` running explicitly defined "functions"/transforms (as `DoFn`): ParDo can execute anonymous functions defined inline. However, it is recommended to define these functions explicitly. The advantages of this are:

    - 1) functions become available for unit testing (testability);
    - 2) functions are be reused on multiple ParDo (reuse);
    - 3) makes code of ParDo and pipelines much easier to read (readability).

  - Composite Transforms (`PTransform`): some transforms can "encapsule" multiple smaller transforms/operations. We can either place all those small transforms on the pipeline definition, or instead create a new "composite transform", which basically includes the multiple small transforms. This can be defined using (extending) the `PTransform` class. The advantages of this are:

    - 1) encapsulation of complex behaviors into composite transforms;
    - 2) reuse of basic transforms across different steps of the pipeline.

  - Unit tests:

    - Beam applications (as many data-driven applications/jobs) may be difficult to unit test (on local machine). Nevertheless, and especially if the Beam pipeline code is arranged in a modular manner (with the patterns presented on the previous points), we can create unit tests per "transformation". These unit tests enable us to be more confident on the code we are building, and they can be run very quickly. All this without having to go into long-running tests using external resources - these are also good to have, but can be made on a later stage. Beam proives
    - `TestPipeline`, which creates a Pipeline to be used on tests, where we can "apply" the "transforms" we are interested on testing. Example [src/test/java/org/apache/beam/examples/WordCountTest.java](src/test/java/org/apache/beam/examples/WordCountTest.java)
    - `PAssert` enables assertion on the contents of a `PCollection` incorporated into the pipeline. `PAssert` call must precede the call to `Pipeline.run`. `PAssert` can perform different types of asserts, namely: `PAssert.that(output).containsInAnyOrder(T... expectedElements)`, which checks that the output contains the provided elements; `PAssert.that(words).satisfies((org.apache.beam.sdk.transforms.SerializableFunction<Iterable<T>, Void> checkerFn)`, where checkerFn is a function which can contain one of more assertions that can be applied to the output (`Iterable`).
