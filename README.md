# Monkey Business

A poor Scumm v5 script extractor ...
The back story to this project is covered in my blog post ["A deep dive into the SCUMM bytecode"](https://tonick.net/p/2021/03/a-deep-dive-into-the-scumm-bytecode/)

## Requirements

* Java 11+ (Tested with Temurin 21)
* Maven (Tested with Maven 3.9.5)

## Build

```
mvn clean test
```

A successful build will leave you with a `monkeybusiness.jar` in the `target` directory.

**A note on testing:**
For obvious reasons the project does not contain any game files.
Nontheless testing against them makes a lot of sense.
So JUnit assumes a file named `monkey.001` in `src/test/resource` but will skip certain tests if the file is not available.
This will not affect other unit-tests.


## Run

```
java -jar monkeybusiness.jar "path/to/your/monkey.001" 
```