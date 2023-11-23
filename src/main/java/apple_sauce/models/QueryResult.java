package apple_sauce.models;

public class QueryResult {
    private String topicNumber;
    private String docNumber;
    private int rank;
    private float score;
    private String analyzerName;

    // 构造函数
    public QueryResult(String topicNumber, String docNumber, int rank, float score, String analyzerName) {
        this.topicNumber = topicNumber;
        this.docNumber = docNumber;
        this.rank = rank;
        this.score = score;
        this.analyzerName = analyzerName;
    }

    // Getter 和 Setter 方法
    public String getTopicNumber() {
        return topicNumber;
    }

    public void setTopicNumber(String topicNumber) {
        this.topicNumber = topicNumber;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getAnalyzerName() {
        return analyzerName;
    }

    public void setAnalyzerName(String analyzerName) {
        this.analyzerName = analyzerName;
    }

    @Override
    public String toString() {
        return topicNumber + " 0 " + docNumber + " " + rank + " " + score + " " + analyzerName;
    }
}
