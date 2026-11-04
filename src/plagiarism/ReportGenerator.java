package plagiarism;
import java.util.*;
/** Prints a console report of all pairwise comparison results. */
public class ReportGenerator {
 public void generateReport(List<ComparisonResult<Double>> results,SimilarityComparator comparator){
  System.out.println("\n=== SIMILARITY REPORT ===");int flagged=0;
  for(ComparisonResult<Double> r:results){boolean f=comparator.isFlagged(r);if(f)flagged++;System.out.printf("%s vs %s: %.2f%% - %s%n",r.getFileA(),r.getFileB(),r.getScore()*100,f?"FLAGGED FOR REVIEW":"Below threshold");}
  System.out.println("Pairs compared: "+results.size());System.out.printf("Threshold: %.0f%%%n",comparator.getThreshold()*100);System.out.println("Pairs flagged: "+flagged);System.out.println("A flag is not proof of plagiarism; a person must review the code.");
 }
}
