# Monkey Business

A poor Scumm v5 script extractor ...
The back story to this project is covered in my blog post ["A deep dive into the SCUMM bytecode"](https://tonick.net/p/2021/03/a-deep-dive-into-the-scumm-bytecode/)

## Requirements

* Java 21 (Tested with Temurin 21)
* Maven (Tested with Maven 3.9.5)

## Build

```
mvn clean test
mvn package
```

A successful build will leave you with a `monkeybusiness.jar` in the `target` directory.

### A note on testing
For obvious reasons the project does not contain any game files.
Nontheless testing with them makes a lot of sense.
(And to be honest: You'll need them anyway if you want to do anyting with this project...)
So JUnit assumes a file named `monkey.001` in `src/test/resources` but will skip certain tests if the file is not available.
This will not affect other unit-tests.

Furthermore there is a special "test" called `MonkeyBusinessTest#createRegressiveTestFiles`.
It will extract all available opcodes from a provided game file to create special one-opcode-scripts in `src/test/resources/opcodes`.
This can come in handy for testing special opcdes in isolation.

## Run

```
java -jar monkeybusiness.jar "path/to/your/monkey.001" 
```

## Troubleshooting

* **Somehow it's not reading my game files:** Make sure that it's a SCUMM V5 file. Older game versions like the EGA version are SCUMM V4 which is very different from V5.
