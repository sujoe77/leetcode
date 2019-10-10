package redis.ch07.entity;

public class WordScore implements Comparable<WordScore> {
    public final String word;
    public final long score;

    public WordScore(String word, long score) {
        this.word = word;
        this.score = score;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WordScore)) {
            return false;
        }
        WordScore t2 = (WordScore) other;
        return this.word.equals(t2.word) && this.score == t2.score;
    }

    @Override
    public int compareTo(WordScore other) {
        if (this.word.equals(other.word)) {
            long diff = this.score - other.score;
            return diff < 0 ? -1 : diff > 0 ? 1 : 0;
        }
        return this.word.compareTo(other.word);
    }

    @Override
    public String toString() {
        return word + '=' + score;
    }
}
