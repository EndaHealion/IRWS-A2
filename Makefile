
.PHONY: build run clean

.DEFAULT_GOAL := all

all: build run

build:
	mvn install

run:
	java -Xmx4g -cp target/apple_sauce-1.0-jar-with-dependencies.jar apple_sauce.Main

clean:
	mvn clean

eval:
	./trec_eval-9.0.7/trec_eval qrels.assignment2.part1 output/eval_results.txt