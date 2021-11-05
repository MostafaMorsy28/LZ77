package compression;

public class Tag {
    private int position;
    private int length;
    private char next;

    void setPosition(int p) {
        position = p;
    }

    void setLength(int l) {
        length = l;
    }

    void setNext(char c) {
        next = c;
    }

    int getPosition() {
        return position;
    }

    int getLength() {
        return length;
    }

    char getNext() {
        return next;
    }
}
