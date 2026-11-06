# Code Plagiarism Checker

**SDN260S – Software Development Project | Group 02**

## 1. Project Overview

The Code Plagiarism Checker is a Java application that analyses multiple Java source files and identifies similarities between student submissions. It can be run as a console application (`CLI_Menu`) or through a graphical window (`GUI`).

The application uses code tokenization, fingerprint generation and similarity comparison to identify potentially similar code.

Its purpose is to assist with identifying submissions that may require further academic review.

## 2. Features

- Loads Java source files from a file or a submission folder (including sub-folders).
- Skips empty, unreadable and non-Java files and reports them instead of stopping.
- Tokenizes source code, ignoring whitespace and comments.
- Generates code fingerprints by hashing every group of 5 consecutive tokens (k-gram hashing with SHA-256).
- Compares submissions using Jaccard similarity (shared fingerprints divided by all distinct fingerprints).
- Calculates similarity percentages between student submissions.
- Uses a configurable similarity threshold (75% by default).
- Flags submissions that meet or exceed the selected threshold.
- Console version: compares every pair of files and prints a similarity report.
- GUI version: select a folder, choose two files, compare them, export the report to a text file and clear the results.

## 3. Technologies Used

- **Programming language:** Java
- **IDE:** Apache NetBeans
- **Application type:** Console application and Swing GUI
- **Core techniques:** Tokenization, k-gram hashing and Jaccard similarity comparison

## 4. System Design

The system is built as a pipeline. Each class does one job and produces an output that is passed to the next stage. The two entry points, `CLI_Menu` (console) and `GUI` (window), act as coordinators: they create the pipeline classes, call them in order, and pass each stage's output to the next. The pipeline classes do not call each other, so any stage can be changed without touching the others.

```
CLI_Menu / GUI
      |  path and threshold
      v
FileInputService  --records-->  errors / skippedFiles lists
      |  List<SourceFile>
      v
CodeTokenizer
      |  List<String> tokens
      v
HashGenerator
      |  Set<Long> fingerprints
      v
SimilarityComparator
      |  ComparisonResult<Double> for each pair
      v
ReportGenerator (console)   /   GUI results area and text export
```

### UML class diagram

![UML class diagram](docs/UML_Class.png)

### Classes

| Class | Responsibility |
|---|---|
| `CLI_Menu` | Console entry point: reads the path and threshold, compares every pair of files and starts the report |
| `GUI` | Graphical entry point (extends `JFrame`): select a folder, choose two files, set the threshold, compare, export and clear |
| `FileInputService` | Finds and reads Java files in a file or folder, skips invalid ones and records problems |
| `SourceFile` | Holds one source file: path, name, content and size |
| `CodeTokenizer` | Breaks source code into tokens, ignoring whitespace and comments |
| `HashGenerator` | Turns a token list into a set of k-gram hashes |
| `SimilarityComparator` | Compares two hash sets, calculates the similarity and decides whether a pair is flagged |
| `ComparisonResult<T extends Number>` | Stores the result for one pair of files |
| `ReportGenerator` | Prints the console report for all results |

### Relationships

| From | Relationship | To | Multiplicity |
|---|---|---|---|
| `CLI_Menu`, `GUI` | uses | `FileInputService`, `CodeTokenizer`, `HashGenerator`, `SimilarityComparator` | 1 to 1 |
| `CLI_Menu` | uses | `ReportGenerator` | 1 to 1 |
| `FileInputService` | creates | `SourceFile` | 1 to 0..* |
| `SimilarityComparator` | creates | `ComparisonResult` | 1 to 0..* |
| `ReportGenerator` | uses | `ComparisonResult` | 1 to 0..* |

### Design decisions and limits

- Tokens are compared instead of raw text, so spacing, line breaks and comments do not hide copied code.
- The threshold is kept in `SimilarityComparator` and can be changed with `setThreshold`.
- A bad file is recorded in the `errors` or `skippedFiles` list and never stops a scan.
- Variable names are kept as written, so renaming variables lowers the score.
- The console version compares every pair of files, which is n(n-1)/2 comparisons for n files. The GUI compares the two files the user selects.
- The console report lists pairs in the order they were compared, not sorted by score.

## 5. Project Structure

```
Code-Plagiarism-Checker/
│
├── src/
│   └── plagiarism/
│       ├── CLI_Menu.java
│       ├── GUI.java
│       ├── FileInputService.java
│       ├── SourceFile.java
│       ├── CodeTokenizer.java
│       ├── HashGenerator.java
│       ├── SimilarityComparator.java
│       ├── ComparisonResult.java
│       └── ReportGenerator.java
│
├── sample-submissions/
│   ├── StudentA.java
│   ├── StudentB.java
│   └── StudentC.java
│
├── docs/
│   └── UML_Class.png
│
├── nbproject/
├── build.xml
├── manifest.mf
└── README.md
```

## 6. How to Run the Application

### Using Apache NetBeans

1. Open Apache NetBeans.
2. Select File → Open Project.
3. Open the Code-Plagiarism-Checker project.
4. Set the main class to `plagiarism.CLI_Menu` (console) or `plagiarism.GUI` (window).
5. Clean and Build the project.
6. Run the application.
7. Console: enter the full path to the `sample-submissions` folder when prompted, then press Enter to use the default threshold of 75%.
8. GUI: click Select Folder, select exactly two files, set the threshold and click Compare Files.

### Using the command line

From the project root directory, compile the Java files:

```
javac -d out src/plagiarism/*.java
```

Run the console version:

```
java -cp out plagiarism.CLI_Menu sample-submissions 75
```

Run the GUI version:

```
java -cp out plagiarism.GUI
```

## 7. Sample Testing

The project includes three sample Java submissions: `StudentA.java`, `StudentB.java` and `StudentC.java`.

During testing, the application successfully loaded all three files, performed tokenization and fingerprint generation, and generated a similarity report.

### Observed test results

| Comparison | Similarity | Result |
|---|---|---|
| StudentA vs StudentB | 76% | Flagged for review |
| StudentA vs StudentC | 0% | Below threshold |
| StudentB vs StudentC | 0% | Below threshold |

Default similarity threshold: 75%

## 8. Important Note

A high similarity percentage does not automatically prove plagiarism. It indicates that submissions may contain similar code and should be reviewed further by an appropriate academic reviewer.

The application is intended to support academic integrity checks and does not replace human assessment.

## 9. Project Purpose

This project demonstrates the application of Java programming concepts, file handling, string processing, hashing techniques and similarity comparison in developing a practical software solution.

---

SDN260S – Group 02 | Code Plagiarism Checker
