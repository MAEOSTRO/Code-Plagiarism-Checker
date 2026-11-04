SDN260S Integrated Console Prototype
1. Extract ZIP.
2. In NetBeans, create a Java project and package named plagiarism.
3. Add all Java files from src/plagiarism.
4. Set plagiarism.CLI_Menu as Main Class.
5. Run and enter the full path to sample-submissions, then threshold 75.
Terminal:
javac -d out src/plagiarism/*.java
java -cp out plagiarism.CLI_Menu sample-submissions 75

This is console-only for the assignment. Your Swing codeprocessordemo.Main is deliberately not the entry point because the assignment asks for no GUI. The tokenizer logic from your submitted code has been placed in the shared plagiarism package.
Fixes: removed stray 'a' in SourceFile; all classes share package plagiarism; threshold is compared as a 0-1 fraction, not divided by 100 twice.
