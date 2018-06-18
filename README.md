# Apache Beam Word Count / Hello World (with subtitles)

This project contains notes on small experiments done with Apache Beam word count example (based on https://beam.apache.org/get-started/wordcount-example/).

## 1) Minimal Word Count
[src/main/java/org/apache/beam/examples/MinimalWordCount.java](./src/main/java/org/apache/beam/examples/MinimalWordCount.java)

- Defines simple processing pipeline, explaining the basic elements of an Apache Beam processing pipeline
- Interesting elements:

  - `PipelineOptions` object: defines the options that are to be used when building an Apache Beam processing pipeline. These can be passed as command-line arguments or obtained from properties files, etc.;
  - `Pipeline` object: this is the actual pipeline object where we can apply different `transform` operations;
  - `PCollection`: collection used to hold the input/output data of `transform` operations;
  - transform operations: these are the "operations" to be performed on the data (stored as `PCollection`). There are different types of "transforms", example: `Count`; `Filter`; and many other predefined transforms. However, we can also write in-line anonymous functions and execute them in a `ParDo` (Parallel Processing, which is similar to the “Map” phase of a Map/Shuffle/Reduce-style algorithm).
  - `.apply(<tranform>)`: the `.apply` method enables to define the chain of "transform" elements of the pipeline.
