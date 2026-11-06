package plagiarism;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class GUI extends JFrame {

    private final FileInputService fileManager;
    private final CodeTokenizer tokenizer;
    private final HashGenerator hashGenerator;
    private SimilarityComparator similarityService;

    private final DefaultListModel<Path> fileListModel;
    private final JList<Path> fileList;
    private final JTextArea resultsArea;
    private final JLabel statusLabel;
    private final JTextField thresholdField;

    private Path selectedFolder;

    public GUI() {
        fileManager = new FileInputService();
        tokenizer = new CodeTokenizer();
        hashGenerator = new HashGenerator(5);
        similarityService = new SimilarityComparator(0.75);

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        resultsArea = new JTextArea();
        statusLabel = new JLabel("Please select a folder.");
        thresholdField = new JTextField("75", 5);

        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Code Plagiarism Checker - SDN260S Group 02");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("CODE PLAGIARISM CHECKER", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        filePanel.setBorder(BorderFactory.createTitledBorder("Java Source Files"));
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        filePanel.add(new JScrollPane(fileList), BorderLayout.CENTER);

        JButton selectFolderButton = new JButton("Select Folder");
        selectFolderButton.addActionListener(e -> selectFolder());
        filePanel.add(selectFolderButton, BorderLayout.SOUTH);
        filePanel.setPreferredSize(new Dimension(350, 400));
        add(filePanel, BorderLayout.WEST);

        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Comparison Results"));
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        resultsArea.setText("Welcome to Code Plagiarism Checker!\n\n"
                + "1. Select a folder containing Java files.\n"
                + "2. Select exactly two files.\n"
                + "3. Set the similarity threshold (default 75%).\n"
                + "4. Click Compare Files.\n");
        resultPanel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        add(resultPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controls.add(new JLabel("Threshold (%):"));
        controls.add(thresholdField);

        JButton compareButton = new JButton("Compare Files");
        JButton exportButton = new JButton("Export Report");
        JButton clearButton = new JButton("Clear Results");

        compareButton.addActionListener(e -> compareFiles());
        exportButton.addActionListener(e -> exportReport());
        clearButton.addActionListener(e -> clearResults());

        controls.add(compareButton);
        controls.add(exportButton);
        controls.add(clearButton);
        bottomPanel.add(controls, BorderLayout.CENTER);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void selectFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Folder Containing Java Files");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path folder = chooser.getSelectedFile().toPath();
        List<SourceFile> sourceFiles = fileManager.readInput(folder);

        fileListModel.clear();
        selectedFolder = folder;

        for (SourceFile sourceFile : sourceFiles) {
            fileListModel.addElement(sourceFile.getPath());
        }

        StringBuilder message = new StringBuilder();
        message.append("Folder: ").append(folder)
                .append("\nJava files loaded: ").append(sourceFiles.size());

        if (!fileManager.getSkippedFiles().isEmpty()) {
            message.append("\n\nSkipped files:");
            for (String skipped : fileManager.getSkippedFiles()) {
                message.append("\n").append(skipped);
            }
        }
        if (!fileManager.getErrors().isEmpty()) {
            message.append("\n\nInput errors:");
            for (String error : fileManager.getErrors()) {
                message.append("\n").append(error);
            }
        }

        resultsArea.setText(message.toString());
        statusLabel.setText(sourceFiles.size() + " Java file(s) loaded.");
        if (sourceFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No readable Java files were found in the selected folder.",
                    "No Java Files", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void compareFiles() {
        if (selectedFolder == null) {
            JOptionPane.showMessageDialog(this, "Please select a folder first.");
            return;
        }

        List<Path> selectedFiles = fileList.getSelectedValuesList();
        if (selectedFiles.size() != 2) {
            JOptionPane.showMessageDialog(this,
                    "Please select exactly TWO files to compare.",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double threshold;
        try {
            threshold = Double.parseDouble(thresholdField.getText().trim()) / 100.0;
            if (!Double.isFinite(threshold) || threshold < 0 || threshold > 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid threshold from 0 to 100.",
                    "Invalid Threshold", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            similarityService.setThreshold(threshold);

            Path fileA = selectedFiles.get(0);
            Path fileB = selectedFiles.get(1);

            String contentA = Files.readString(fileA, StandardCharsets.UTF_8);
            String contentB = Files.readString(fileB, StandardCharsets.UTF_8);

            List<String> tokensA = tokenizer.tokenize(contentA);
            List<String> tokensB = tokenizer.tokenize(contentB);

            Set<Long> hashesA = hashGenerator.generateHashes(tokensA);
            Set<Long> hashesB = hashGenerator.generateHashes(tokensB);

            ComparisonResult<Double> result = similarityService.compare(
                    fileA.getFileName().toString(), hashesA,
                    fileB.getFileName().toString(), hashesB);

            double percentage = result.getScore() * 100.0;
            String decision = similarityService.isFlagged(result)
                    ? "FLAGGED FOR REVIEW" : "BELOW THRESHOLD";

            String report = "========== COMPARISON REPORT ==========\n\n"
                    + "File 1: " + result.getFileA() + "\n"
                    + "File 2: " + result.getFileB() + "\n\n"
                    + "Tokens in file 1: " + tokensA.size() + "\n"
                    + "Tokens in file 2: " + tokensB.size() + "\n"
                    + "Fingerprints in file 1: " + hashesA.size() + "\n"
                    + "Fingerprints in file 2: " + hashesB.size() + "\n\n"
                    + String.format("Similarity: %.2f%%%n", percentage)
                    + String.format("Review threshold: %.2f%%%n", threshold * 100)
                    + "Result: " + decision + "\n\n"
                    + "=======================================\n"
                    + "A high similarity score does not prove plagiarism.\n"
                    + "The result must be reviewed by a person.\n";

            resultsArea.setText(report);
            statusLabel.setText("Comparison completed successfully.");
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Comparison failed: " + ex.getMessage(),
                    "Comparison Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Comparison failed.");
        }
    }

    private void exportReport() {
        String report = resultsArea.getText();
        if (!report.contains("COMPARISON REPORT")) {
            JOptionPane.showMessageDialog(this,
                    "Please compare two files before exporting a report.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Comparison Report");
        chooser.setSelectedFile(new java.io.File("SimilarityReport.txt"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path destination = chooser.getSelectedFile().toPath();
            try {
                Files.writeString(destination, report, StandardCharsets.UTF_8);
                statusLabel.setText("Report saved: " + destination);
                JOptionPane.showMessageDialog(this, "Report exported successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not save report: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearResults() {
        resultsArea.setText("Select a folder and two Java files to begin comparison.");
        fileList.clearSelection();
        statusLabel.setText("Results cleared.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}
