package plagiarism;
import java.util.*;

public class SimilarityComparator {
 private double threshold; 
 public SimilarityComparator()
 {
     this(.75);
 } 
 public SimilarityComparator(double t){
     setThreshold(t);
 }
 public ComparisonResult<Double> compare(String a,Set<Long> ha,String b,Set<Long> hb){
     return new ComparisonResult<>(a,b,calculateSimilarity(ha,hb));
 }
 public double calculateSimilarity(Set<Long>a,Set<Long>b){
     if(a==null||b==null)throw new IllegalArgumentException("Fingerprint sets must not be null");
     if(a.isEmpty()||b.isEmpty())
         return 0;Set<Long> shared=new HashSet<>(a);shared.retainAll(b);
         int intersection=shared.size();
         long union=(long)a.size()+b.size()-intersection;
         return union==0?0:(double)intersection/union;
 }
 public boolean isFlagged(ComparisonResult<Double> r){
     if(r==null)throw new IllegalArgumentException("Result must not be null")
             ;return r.getScore()>=threshold;
 }
 public double getThreshold(){
     return threshold;
 } 
 public void setThreshold(double t){
     if(!Double.isFinite(t)||t<0||t>1)throw new IllegalArgumentException("Threshold must be between 0.0 and 1.0");
     threshold=t;
 }
 public Set<Long> getSharedFingerprints(Set<Long>a,Set<Long>b){
     if(a==null||b==null)throw new IllegalArgumentException("Fingerprint sets must not be null");
     Set<Long>s=new HashSet<>(a);s.retainAll(b);return s;}
}
