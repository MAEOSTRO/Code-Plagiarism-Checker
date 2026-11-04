package plagiarism;
/** Stores the file pair and their similarity score as a fraction (0.0 to 1.0). */
public class ComparisonResult<T extends Number> {
 private final String fileA,fileB; private final T score;
 public ComparisonResult(String a,String b,T score){fileA=a;fileB=b;this.score=score;}
 public String getFileA(){return fileA;} public String getFileB(){return fileB;} public T getScore(){return score;}
}
