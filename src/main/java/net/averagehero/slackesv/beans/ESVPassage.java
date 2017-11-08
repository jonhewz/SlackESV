package net.averagehero.slackesv.beans;


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
   "passage_meta":[
      {
         "canonical":"Genesis 1:1",
         "chapter_start":[
            1001001,
            1001031
         ],
         "chapter_end":[
            1001001,
            1001031
         ],
         "prev_verse":null,
         "next_verse":1001002,
         "prev_chapter":null,
         "next_chapter":[
            1002001,
            1002025
         ]
      },
      {
         "canonical":"John 1:1",
         "chapter_start":[
            43001001,
            43001051
         ],
         "chapter_end":[
            43001001,
            43001051
         ],
         "prev_verse":42024053,
         "next_verse":43001002,
         "prev_chapter":[
            42024001,
            42024053
         ],
         "next_chapter":[
            43002001,
            43002025
         ]
      }
   ],
   "passages":[
      "\nGenesis 1:1\n\n\nThe Creation of the World\n\n  [1] In the beginning, God created the heavens and the earth. (ESV)",
      "\nJohn 1:1\n\n\nThe Word Became Flesh\n\n  [1] In the beginning was the Word, and the Word was with God, and the Word was God. (ESV)"
   ]
}
*/
public class ESVPassage {

    private final String[] passages;

    public ESVPassage(String[] passages) {
        this.passages = passages;
    }

    public String[] getPassages() {
        return passages;
    }

    @Override
    public String toString() {
        String rv = "";
        for (String passage : getPassages()) {
            rv += passage + "\n";
        }
        return rv;
    }


}
