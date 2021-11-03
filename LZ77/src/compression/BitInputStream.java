package compression;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {

    private InputStream inputStream;

    private int currentByte;

    private int numberOfBitsInCurrentByte;

    private boolean isEndOfInput;

    BitInputStream(InputStream in) {
        inputStream = in;
        currentByte = 0;
        isEndOfInput = false;
        numberOfBitsInCurrentByte = 0;
    }

    public int read() throws IOException {
        if (isEndOfInput)
            return -1;
        if (numberOfBitsInCurrentByte == 0) {
            currentByte = inputStream.read();
            numberOfBitsInCurrentByte = 8;
            if (currentByte == -1) {
                isEndOfInput = false;
                return -1;
            }
        }
        --numberOfBitsInCurrentByte;
        return (currentByte >> numberOfBitsInCurrentByte) & 1;
    }

    public int read(int bits) throws IOException {
        int input = 0;
        int b;
        for (int i = 0; i < bits; ++i) {
            b = read();
            if (b == -1) {
                return -1;
            }
            input |= (b << i);
        }
        return input;
    }

}
