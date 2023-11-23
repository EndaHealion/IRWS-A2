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
Before building and running, you must first place the dataset folder in ```src/main/resources/``` directory. When you download it from the Google Drive it will be called ```Assignment 2.zip```, but it should be unzipped and moved to a directory called ```dataset```. Inside this ```dataset``` folder should be the following folders: ```dtds```, ```fbis```, ```fr94```, ```ft```, ```latimes```.

This folder has not been added to the GitHub repo because of size limitations.

However, you can also do this *automatically* by following the instructions in the [```All in one```](#all-in-one) or [```Before Running```](#before-running) section below.

For example, part of the tree structure should look like this:
```bash
.
├── output
├── resources
│   ├── assets
│   └── dataset
│       ├── dtds
│       ├── fbis
│       ├── fr94
│       ├── ft
│       └── latimes
├── src
│   └── main
│       └── java
│           └── apple_sauce
│               ├── eNums
│               ├── models
└──             └── parsers
```

### All in one
To run this program all in one, you may try the following command:
```bash
chmod +x start.sh
./start.sh
```
This will download all the needed dependency, dataset, build the program and run it.

However, you still need to download the ```trec_eval``` from Github and run it manually. Please follow the instructions in the [```Trec Eval```](#trec-eval) section below.

### Before Running

Fo manually run the program, please follow the instructions below.

You need to download and unzip the dataset from the Google Drive. The link is here: 
```bash
https://drive.google.com/file/d/17KpMCaE34eLvdiTINqj1lmxSBSu8BtDP
```
To download it from the command line, firstly you need to install ```pip```,```gdown``` and ```unzip```:
```bash
sudo apt install python3-pip
pip install gdown
sudo apt install unzip
```
If ```gdown``` is not added to ```PATH```, then you need to source it:
```bash
echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

Then run the following command:

```bash
chmod +x dataset.sh
./dataset.sh
```
This will download the dataset and unzip it into the correct directory.

Also, before running ```make```, make sure you've installed ```maven```:
```bash
sudo apt install maven
```

### Commands
There is a Makefile which has the following options:

```bash
make clean
make build
make run
make (This runs "clean", "build" and then "run" in order)
```

### Trec Eval
To run trec eval, you must first run the program and generate the results file. Then you can run the following command:

Download trec_eval from Github
```bash
git clone https://github.com/usnistgov/trec_eval.git
```

Go to the trec_eval directory
```bash
cd trec_eval
```

Run the following command
```bash
./trec_eval ../qrels.assignment2.part1 ../output/[AnalyzerTypeName]_[SimilarityName]_eval_results.txt
```

