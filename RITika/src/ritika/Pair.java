package ritika;

// Data structure that implements a pair formed by Integer and String,
// used in RITika.java in order to store the number of ocurrences of a
// token in the document and its rank in the list, ordered by the times
// the token appeared
public class Pair<Integer,String> {
    
    private Integer left;
    private String right;
    public Pair(Integer l, String r){
        this.left = l;
        this.right = r;
    }
    public Integer getL(){ return left; }
    public String getR(){ return right; }
    public void setL(Integer l){ this.left = l; }
    public void setR(String r){ this.right = r; }

}
