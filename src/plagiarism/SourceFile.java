package plagiarism;
import java.nio.file.Path;
/** Stores source-code text and file metadata. */
public class SourceFile {
 private final Path path; private final String fileName, content; private final long size;
 public SourceFile(Path path,String fileName,String content,long size){this.path=path;this.fileName=fileName;this.content=content;this.size=size;}
 public Path getPath(){return path;} public String getFileName(){return fileName;} public String getContent(){return content;} public long getSize(){return size;}
 public String toString(){return fileName+" ("+size+" bytes)";}
}
