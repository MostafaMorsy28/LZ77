package compression;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream {

    private OutputStream outputStream;
    private int currentByte;
    private int numberOfBitsInCurrentByte;
    int totalNumberOfBits;

    public BitOutputStream(OutputStream out) {
        outputStream = out;
        currentByte = 0;
        numberOfBitsInCurrentByte = 0;
        totalNumberOfBits = 0;
    }

    public void write(boolean b) {
        try {
            currentByte = currentByte << 1 | (b ? 1 : 0);
            ++numberOfBitsInCurrentByte;
            ++totalNumberOfBits;
            if (numberOfBitsInCurrentByte == 8) {
                outputStream.write(currentByte);
                numberOfBitsInCurrentByte = 0;
                currentByte = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte b) {
        int n = (int) b;
        for (int i = 0; i < 8; ++i) {
            write(((n>>i) & 1) == 1);
        }
    }

    public void close() throws IOException {
        while (numberOfBitsInCurrentByte != 0) {
            write(Boolean.FALSE);
        }
        outputStream.close();
    }

    public int getTotalNumberOfBits() {
        return totalNumberOfBits;
    }
}
