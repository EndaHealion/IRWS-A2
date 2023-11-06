# IRWS-A2


## Dependencies
Current list of dependencies:
- Java 21
- Maven
- Makefile
- Lucene (8.6.3)
    - lucene-core
    - lucene-queryparser
    - lucene-analyzers-common


## Building & Running

### Important
Before building and running, you must first place the dataset folder in src/main/resources/ directory. When you download it from the Google Drive it will be called "Assignment 2", but it should be renmaed to "dataset". Inside this "dataset" folder should be the following folders: "dtds", "fbis", "fr94", "ft", "latimes".
This folder has not been added to the GitHub repo because of size limitations.

### Commands
There is a Makefile which has the following options:

```
make clean
make build
make run
make (This runs "clean", "build" and then "run" in order)
```
