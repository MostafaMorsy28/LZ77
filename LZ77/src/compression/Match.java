package compression;

public class Match {
    private int position;
    private int length;

    Match() {
        position = 0;
        length = 0;
    }

    void setPosition(int p) {
        position = p;
    }

    void setLength(int l) {
        length = l;
    }

    int getPosition() {
        return position;
    }

    int getLength() {
        return length;
    }
}
