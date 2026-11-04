package plagiarism;
import java.nio.file.Path; import java.util.*;
/** Console entry point connecting file input, tokenization, hashing, comparison and reporting. */
public class CLI_Menu {
 public static void main(String[] args){
  Scanner in=new Scanner(System.in);System.out.println("========================================\n CODE PLAGIARISM CHECKER - SDN260S Group 02\n========================================");
  String path;double threshold=.75;
  if(args.length>0){path=args[0];if(args.length>1)try{threshold=Double.parseDouble(args[1]);}catch(NumberFormatException e){System.out.println("Using 75% threshold.");}}
  else{System.out.print("Enter path to Java file or submissions folder: ");path=in.nextLine().trim();System.out.print("Threshold percentage (Enter for 75): ");String t=in.nextLine().trim();if(!t.isEmpty())try{threshold=Double.parseDouble(t)/100;}catch(NumberFormatException e){System.out.println("Invalid threshold; using 75%.");threshold=.75;}}
  if(threshold>1&&threshold<=100)threshold/=100;if(!Double.isFinite(threshold)||threshold<0||threshold>1){System.out.println("Threshold must be between 0 and 100.");in.close();return;}
  FileInputService loader=new FileInputService();List<SourceFile> files=loader.readInput(Path.of(path));
  System.out.println("\n[1] FILE INPUT\nJava files loaded: "+files.size());files.forEach(f->System.out.println(" - "+f));loader.getSkippedFiles().forEach(System.out::println);loader.getErrors().forEach(System.out::println);
  if(files.size()<2){System.out.println("At least two readable Java files are required.");in.close();return;}
  CodeTokenizer tokenizer=new CodeTokenizer();HashGenerator generator=new HashGenerator(5);SimilarityComparator comparator=new SimilarityComparator(threshold);Map<SourceFile,Set<Long>> fingerprints=new LinkedHashMap<>();
  System.out.println("\n[2] TOKENIZATION AND FINGERPRINTING");
  for(SourceFile f:files)try{List<String> tokens=tokenizer.tokenize(f.getContent());Set<Long> hashes=generator.generateHashes(tokens);fingerprints.put(f,hashes);System.out.println(f.getFileName()+": "+tokens.size()+" tokens; "+hashes.size()+" fingerprints");}catch(IllegalArgumentException e){System.out.println("Could not process "+f.getFileName()+": "+e.getMessage());}
  List<SourceFile> usable=new ArrayList<>(fingerprints.keySet());List<ComparisonResult<Double>> results=new ArrayList<>();
  for(int i=0;i<usable.size();i++)for(int j=i+1;j<usable.size();j++){SourceFile a=usable.get(i),b=usable.get(j);results.add(comparator.compare(a.getFileName(),fingerprints.get(a),b.getFileName(),fingerprints.get(b)));}
  new ReportGenerator().generateReport(results,comparator);in.close();
 }
}
