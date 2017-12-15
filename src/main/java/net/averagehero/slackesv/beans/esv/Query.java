package net.averagehero.slackesv.beans.esv;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/* Example response JSON from ESV:
{
   "query":"Genesis 1:1,John 1:1",
   "canonical":"Genesis 1:1; John 1:1",
   "parsed":[
      [
         1001001,
         1001001
      ],
      [
         43001001,
         43001001
      ]
   ],
   "passage_meta":[...],
   "passages":[
      "\nGenesis 1:1\n\n\nThe Creation of the World\n\n  [1] In the beginning, God created the heavens and the earth. (ESV)",
      "\nJohn 1:1\n\n\nThe Word Became Flesh\n\n  [1] In the beginning was the Word, and the Word was with God, and the Word was God. (ESV)"
   ]
}
*/
public class Query {

    private String query;

    private String canonical;

    private List<List<Integer>> parsed = null;

    @SerializedName("passage_meta")
    private List<PassageMetum> passageMeta = null;

    private List<String> passages = null;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCanonical() {
        return canonical;
    }

    public void setCanonical(String canonical) {
        this.canonical = canonical;
    }

    public List<List<Integer>> getParsed() {
        return parsed;
    }

    public void setParsed(List<List<Integer>> parsed) {
        this.parsed = parsed;
    }

    public List<PassageMetum> getPassageMeta() {
        return passageMeta;
    }

    public void setPassageMeta(List<PassageMetum> passageMeta) {
        this.passageMeta = passageMeta;
    }

    public List<String> getPassages() {
        return passages;
    }

    public void setPassages(List<String> passages) {
        this.passages = passages;
    }


}
