all:
	@echo "Compiling..."
	javac -cp json-simple-1.1.1.jar *.java -d .

run: all
# windows needs a semicolon
ifeq ($(OS),win)
		@echo "Running on windows ..."
		java -cp "json-simple-1.1.1.jar;." Main
# everyone else likes a colon
else
		@echo "Running ..."
		java -cp "json-simple-1.1.1.jar:." Main
endif

test: all run
	@echo "Cleaning ..."
	rm -rf *.class

clean:
	rm -rf *.class
