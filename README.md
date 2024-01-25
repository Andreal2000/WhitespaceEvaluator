# Whitespace Evaluator

![GitHub Actions Workflow Status](https://github.com/Andreal2000/WhitespaceEvaluator/actions/workflows/scala.yml/badge.svg)

Whitespace Evaluator is an interpreter for the [Whitespace programming language](https://esolangs.org/wiki/Whitespace) written in [Scala 3](https://scala-lang.org/). The interpreter utilizes [scala parser combinators](https://github.com/scala/scala-parser-combinators) for parsing Whitespace code and is built using [sbt](https://www.scala-sbt.org/index.html) (Simple Build Tool).

## What is Whitespace?

Whitespace is an esoteric programming language where the only meaningful characters are space, tab, and newline. All other characters are ignored, making the code visually resemble whitespace. Programs in Whitespace are defined by sequences of these whitespace characters, and they are executed based on the lengths of these sequences.

## Features

- **Whitespace Interpreter**: Parses and interprets Whitespace code.
- **Parser Combinators**: Utilizes Scala 3's parser combinators for stability and easy to understand code.
- **SBT Build:** Uses SBT as the building tool for the project.
- **CI/CD Testing:** CI/CD pipeline ensures continuous testing of the interpreter.

## Requirements

Ensure you have the following prerequisites installed before using the Whitespace Evaluator:

- [Scala 3](https://www.scala-lang.org/download/)
- [SBT (Scala Build Tool)](https://www.scala-sbt.org/download/)

## Installation

1. Clone this repository to your local machine:

   ```bash
   git clone https://github.com/Andreal2000/WhitespaceEvaluator.git
   ```

2. Navigate to the project directory:

   ```bash
   cd WhitespaceEvaluator
   ```

3. Build the project using SBT:

   ```bash
   sbt compile
   ```

## Usage

To execute a whitespace program, provide the program as input to the interpreter:

```bash
sbt "run path/to/your/whitespace-program.ws"
```

To execute multiple whitespace programs in succession, provide the paths to the programs as input to the interpreter:

```bash
sbt "run path/to/first/whitespace-program.ws path/to/second/whitespace-program.ws ... path/to/nth/whitespace-program.ws"
```

This command will execute each whitespace program in the specified order. Adjust the file paths accordingly to include all the whitespace programs you want to run.

## Testing

This project includes unit tests to ensure the correctness of the interpreter. To run the tests, use the following command:

```bash
sbt test
```

The CI/CD pipeline also automatically runs these tests to ensure continuous integration.
