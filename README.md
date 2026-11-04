Code Plagiarism Checker



SDN260S – Software Development Project

Group 02



1\. Project Overview



The Code Plagiarism Checker is a Java-based console application designed to analyse multiple Java source files and identify similarities between student submissions.



The application uses code tokenization, fingerprint generation and similarity comparison to identify potentially similar code.



Its purpose is to assist with identifying submissions that may require further academic review.



2\. Features



\* Loads multiple Java source files from a submission folder.

\* Tokenizes source code for analysis.

\* Generates code fingerprints using a rolling-hash approach.

\* Compares submissions using Jaccard similarity.

\* Calculates similarity percentages between student submissions.

\* Uses a configurable similarity threshold.

\* Flags submissions that meet or exceed the selected threshold.

\* Generates a similarity report in the console.



3\. Technologies Used



\* Programming Language: Java

\* IDE: Apache NetBeans

\* Application Type: Console-based application

\* Core Techniques: String matching, tokenization, hashing and similarity comparison.



4\. Project Structure



Code-Plagiarism-Checker/

│

├── src/

│   └── plagiarism/

│       └── Java source files

│

├── sample-submission/

│   ├── StudentA.java

│   ├── StudentB.java

│   └── StudentC.java

│

├── nbproject/

├── build/

├── dist/

├── lib/

├── build.xml

├── manifest.mf

└── README.md



5\. How to Run the Application



Using Apache NetBeans



1\. Open Apache NetBeans.

2\. Select File → Open Project.

3\. Open the Code-Plagiarism-Checker project.

4\. Ensure the main class is set to plagiarism.CLI\_Menu.

5\. Clean and Build the project.

6\. Run the application.

7\. Enter the full path to the sample-submission folder when prompted.

8\. Press Enter to use the default similarity threshold of 75%.



Using the Command Line



From the project root directory, compile the Java files:



javac -d out src/plagiarism/\*.java



Run the application:



java -cp out plagiarism.CLI\_Menu sample-submission 75



6\. Sample Testing



The project includes three sample Java submissions:



\* StudentA.java

\* StudentB.java

\* StudentC.java



During testing, the application successfully loaded all three files, performed tokenization and fingerprint generation, and generated a similarity report.



Observed Test Results



Comparison	Similarity	Result

StudentA vs StudentB	76%	Flagged for review

StudentA vs StudentC	0%	Below threshold

StudentB vs StudentC	0%	Below threshold



Default similarity threshold: 75%



7\. Important Note



A high similarity percentage does not automatically prove plagiarism. It indicates that submissions may contain similar code and should be reviewed further by an appropriate academic reviewer.



The application is intended to support academic integrity checks and does not replace human assessment.



8\. Project Purpose



This project demonstrates the application of Java programming concepts, file handling, string processing, hashing techniques and similarity comparison in developing a practical software solution.







SDN260S – Group 02

Code Plagiarism Checker

