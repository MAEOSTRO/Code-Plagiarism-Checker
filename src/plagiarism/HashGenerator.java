package plagiarism;
import java.nio.charset.StandardCharsets; import java.security.*; import java.util.*;
/** Creates hash fingerprints for consecutive groups of tokens. */
public class HashGenerator {
 private final int k; public HashGenerator(){this(5);} public HashGenerator(int k){if(k<1)throw new IllegalArgumentException("k must be at least 1");this.k=k;}
 public Set<Long> generateHashes(List<String> tokens){Set<Long> out=new HashSet<>();if(tokens==null||tokens.isEmpty())return out;if(tokens.size()<k){out.add(hash(String.join(" ",tokens)));return out;}for(int i=0;i<=tokens.size()-k;i++)out.add(hash(String.join(" ",tokens.subList(i,i+k))));return out;}
 private long hash(String text){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));long v=0;for(int i=0;i<8;i++)v=(v<<8)|(b[i]&255L);return v;}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
}
