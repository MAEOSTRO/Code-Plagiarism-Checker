package plagiarism;
import java.util.*;
/** Tokenizes source code while ignoring whitespace and comments. */
public class CodeTokenizer {
 private static final String[] OPS={">>>=","<<=",">>=",">>>","...","==","!=","<=",">=","++","--","&&","||","+=","-=","*=","/=","%=","&=","|=","^=","<<",">>","->","::"};
 private static final String SYMBOLS="(){}[];,.@=><!~?:+-*/&|^%";
 public List<String> tokenize(String s){
  if(s==null)throw new IllegalArgumentException("Source code must not be null.");
  List<String> out=new ArrayList<>(); int i=0;
  while(i<s.length()){char c=s.charAt(i);
   if(Character.isWhitespace(c)){i++;continue;}
   if(s.startsWith("//",i)){i+=2;while(i<s.length()&&s.charAt(i)!='\n'&&s.charAt(i)!='\r')i++;continue;}
   if(s.startsWith("/*",i)){int e=s.indexOf("*/",i+2);if(e<0)throw new IllegalArgumentException("Unclosed block comment at "+i);i=e+2;continue;}
   if(c=='"'||c=='\''){int start=i++;boolean closed=false;while(i<s.length()){char q=s.charAt(i);if(q=='\\'){i=Math.min(i+2,s.length());continue;}if(q==c){i++;closed=true;break;}if(q=='\n'||q=='\r')break;i++;}if(!closed)throw new IllegalArgumentException("Unclosed quoted literal at "+start);out.add(s.substring(start,i));continue;}
   if(Character.isJavaIdentifierStart(c)){int st=i++;while(i<s.length()&&Character.isJavaIdentifierPart(s.charAt(i)))i++;out.add(s.substring(st,i));continue;}
   if(Character.isDigit(c)){int st=i++;while(i<s.length()&&(Character.isDigit(s.charAt(i))||s.charAt(i)=='.'))i++;out.add(s.substring(st,i));continue;}
   boolean matched=false;for(String op:OPS)if(s.startsWith(op,i)){out.add(op);i+=op.length();matched=true;break;}if(matched)continue;
   if(SYMBOLS.indexOf(c)>=0){out.add(String.valueOf(c));i++;continue;}
   throw new IllegalArgumentException("Unsupported character at position "+i+": "+c);
  }return out;
 }
}
