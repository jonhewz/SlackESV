package net.averagehero.slackesv.beans.esv;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private List<Integer> chapterStart = null;
    private List<Integer> chapterEnd = null;
    private Integer prevVerse;
    private Integer nextVerse;
    private List<Integer> prevChapter = null;
    private List<Integer> nextChapter = null;

    @JsonProperty("canonical")
    public String getCanonical() {
        return canonical;
    }

    public void setCanonical(String canonical) {
        this.canonical = canonical;
    }

    @JsonProperty("chapter_start")
    public List<Integer> getChapterStart() {
        return chapterStart;
    }

    public void setChapterStart(List<Integer> chapterStart) {
        this.chapterStart = chapterStart;
    }

    @JsonProperty("chapter_end")
    public List<Integer> getChapterEnd() {
        return chapterEnd;
    }

    public void setChapterEnd(List<Integer> chapterEnd) {
        this.chapterEnd = chapterEnd;
    }

    @JsonProperty("prev_verse")
    public Integer getPrevVerse() {
        return prevVerse;
    }

    public void setPrevVerse(Integer prevVerse) {
        this.prevVerse = prevVerse;
    }

    @JsonProperty("next_verse")
    public Integer getNextVerse() {
        return nextVerse;
    }

    public void setNextVerse(Integer nextVerse) {
        this.nextVerse = nextVerse;
    }

    @JsonProperty("prev_chapter")
    public List<Integer> getPrevChapter() {
        return prevChapter;
    }

    public void setPrevChapter(List<Integer> prevChapter) {
        this.prevChapter = prevChapter;
    }

    @JsonProperty("next_chapter")
    public List<Integer> getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(List<Integer> nextChapter) {
        this.nextChapter = nextChapter;
    }

}
