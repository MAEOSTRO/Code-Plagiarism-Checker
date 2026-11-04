package plagiarism;
import java.io.IOException; import java.nio.charset.StandardCharsets; import java.nio.file.*; import java.util.*; import java.util.stream.Stream;
/** Loads Java files from a file or directory and records input problems. */
public class FileInputService {
 private final List<String> errors=new ArrayList<>(), skippedFiles=new ArrayList<>();
 public List<SourceFile> readInput(Path inputPath){
  if(inputPath==null||!Files.exists(inputPath)){errors.add("Input path does not exist: "+inputPath);return new ArrayList<>();}
  if(Files.isDirectory(inputPath))return scanDirectory(inputPath);
  if(!isValidJavaFile(inputPath)){skippedFiles.add("Skipped non-Java or unreadable file: "+inputPath);return new ArrayList<>();}
  try{return List.of(readJavaFile(inputPath));}catch(IOException e){errors.add("Error reading "+inputPath+": "+e.getMessage());return new ArrayList<>();}
 }
 private List<SourceFile> scanDirectory(Path root){
  List<SourceFile> found=new ArrayList<>();
  try(Stream<Path> paths=Files.walk(root)){paths.filter(Files::isRegularFile).filter(this::isValidJavaFile).forEach(path->{try{found.add(readJavaFile(path));}catch(IOException e){errors.add("Error reading "+path+": "+e.getMessage());}});}
  catch(IOException e){errors.add("Error scanning directory "+root+": "+e.getMessage());} return found;
 }
 private boolean isValidJavaFile(Path path){return path.getFileName().toString().toLowerCase().endsWith(".java")&&Files.isReadable(path)&&!Files.isDirectory(path);}
 private SourceFile readJavaFile(Path path)throws IOException{long size=Files.size(path);if(size==0){skippedFiles.add("Skipped empty file: "+path);throw new IOException("File is empty.");}return new SourceFile(path,path.getFileName().toString(),Files.readString(path,StandardCharsets.UTF_8),size);}
 public List<String> getErrors(){return errors;} public List<String> getSkippedFiles(){return skippedFiles;}
}
