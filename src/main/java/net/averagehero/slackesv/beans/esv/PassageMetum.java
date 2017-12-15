package net.averagehero.slackesv.beans.esv;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Example response JSON from ESV:
 *
 *
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
 ]
 */
public class PassageMetum {

    private String canonical;

    @SerializedName("chapter_start")
    private List<Integer> chapterStart = null;

    @SerializedName("chapter_end")
    private List<Integer> chapterEnd = null;

    @SerializedName("prev_verse")
    private Integer prevVerse;

    @SerializedName("next_verse")
    private Integer nextVerse;

    @SerializedName("prev_chapter")
    private List<Integer> prevChapter = null;

    @SerializedName("next_chapter")
    private List<Integer> nextChapter = null;

    public String getCanonical() {
        return canonical;
    }

    public void setCanonical(String canonical) {
        this.canonical = canonical;
    }

    public List<Integer> getChapterStart() {
        return chapterStart;
    }

    public void setChapterStart(List<Integer> chapterStart) {
        this.chapterStart = chapterStart;
    }

    public List<Integer> getChapterEnd() {
        return chapterEnd;
    }

    public void setChapterEnd(List<Integer> chapterEnd) {
        this.chapterEnd = chapterEnd;
    }

    public Integer getPrevVerse() {
        return prevVerse;
    }

    public void setPrevVerse(Integer prevVerse) {
        this.prevVerse = prevVerse;
    }

    public Integer getNextVerse() {
        return nextVerse;
    }

    public void setNextVerse(Integer nextVerse) {
        this.nextVerse = nextVerse;
    }

    public List<Integer> getPrevChapter() {
        return prevChapter;
    }

    public void setPrevChapter(List<Integer> prevChapter) {
        this.prevChapter = prevChapter;
    }

    public List<Integer> getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(List<Integer> nextChapter) {
        this.nextChapter = nextChapter;
    }

}
