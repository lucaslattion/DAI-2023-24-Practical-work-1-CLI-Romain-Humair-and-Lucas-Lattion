# DAI-2023-24-Practical-work-1-CLI

* Lucas Lattion
* Romain Humair

# CLI
Our CLI can count words from a text file.
By default, the output file will contain all words and their number of appearances in input file.
Input file is limited to .txt or .md files.
Only one input file can be processed at a time.

Is it possible to activate case-sensitive search, change text encoding, filter words to count or hightlight them   


# How to build the  CLI-

first, clone the repository.

Then, you have two possibilities to build the jar file:
1. IntelliJ
   2. select "Maven Package as JAR file" on top right of IntelliJ
   3. click "Run" button
2. command line in the project folder
   3. ```mvn dependency:resolve clean compile package```

   
JAR file will be generated the /target/ folder


# How to use the CLI

minimum parameter are inputFile and outputFile

```java -jar Search-CLI-1.0-SNAPSHOT.jar <inputFile> <outputFile> -c```

Words to count or to highlight can be filtered with the option ```-w``` following by the words.
Check the [Examples](#Examples) for more details.

## Help
```
<inputFile>                                     Input file path
<outputFile>                                    Output file path
-c, --case-sensitive                            Enable case sensitivity
-ei, --input-encoding=<inputEncoding>           Input character encoding (default: UTF-8)
-eo, --output-encoding=<outputEncoding>         Output character encoding (default: UTF-8)
-h, --highlight                                 Highlight words in Markdown format
-w, --words=<filterWords>[,<filterWords>...]    List of words to filter/count
```

<h1 id="Examples">
  Examples
</h1>

You can find [input files](/src/test/) to test the CLI, the files example are under "/src/test/".

the file [inputText.txt](/src/test/inputText.txt) contain a text (UTF-8) to test the CLI.
```
This Is An Input Text Used As A Test. The Format Is In Camel Case.
So It Does A good Test To Transform All In Lowercase Or Upercase.
I add the following line to perform a test to count words in a text with mixed upercase and lowercase words. 
```

## Word count
You can test the CLI with this example : ```java -jar Search-CLI-1.0-SNAPSHOT.jar src/test/inputText.txt src/test/output.txt -w This,Test,FoLLoWing```

feel free to change the 3 filter word ```This,Test,FoLLoWing```.

Here are the result without the ```-c``` option :
```
following: 1
test: 3
this: 1
```

And with the ```-c``` option :
```
FoLLoWing: 0
Test: 2
This: 1
```

## Word highlight
You can also highlight word in a markdown format with the option ```-h```. Filter Words  set with ```-w``` will be highlighted in a markdown output file (.md).
```java -jar Search-CLI-1.0-SNAPSHOT.jar inputText.txt output.txt -w This,Test,FoLLoWing -h```

```
**This** Is An Input Text Used As A **Test**. The Format Is In Camel Case.
So It Does A good **Test** To Transform All In Lowercase Or Upercase.
I add the **following** line to perform a **test** to count words in a text with mixed upercase and lowercase words.
```

## Encoding

There are 2 others input files, both with an UTF-16 encoding: (UTF-16LE)
* [inputTextUTF16LE.txt](/src/test/inputTextUTF16LE.txt)
* [inputTextUTF16LEKorean.txt](/src/test/inputTextUTF16LEKorean.txt)

Here an example with the text in Korean : ```java -jar Search-CLI-1.0-SNAPSHOT.jar src/test/inputTextUTF16LEKorean.txt src/test/output.txt -ei UTF-16LE -eo UTF-16LE```

```
in: 1
is: 1
korean: 1
text: 1
각: 1
갖게: 1
것은: 2
것입니다: 1
관련되어: 1
...
```

### wrong encoding example
This is a example of what could happen if the input encoding is not correctly set : ```java -jar Search-CLI-1.0-SNAPSHOT.jar src/test/inputTextUTF16LE.txt src/test/output.txt -w UTF-16,encoding -h```.
the original text is encoded with UTF-16LE but has been read with the default UTF-8 encoding.

As the input encoding is not correct, the file is unreadable and the output file (.md) is then also unreadable.
```
��T
```




