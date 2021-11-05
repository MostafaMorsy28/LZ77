package compression;

import java.io.*;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class LZ77 {

    private int searchBufferBits;
    private int lookAheadBufferBits;

    public LZ77() {
        searchBufferBits = 5;
        lookAheadBufferBits = 3;
    }

    public LZ77(int searchBufferSize, int lookAheadBufferSize) {
        searchBufferBits = searchBufferSize;
        lookAheadBufferBits = lookAheadBufferSize;
    }

    public void compress(String input, String tagsOutput, String bytesOutput) {
        try {
            File file = new File(input);
            byte[] data = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(data);
            fileInputStream.close();

            FileWriter fileWriter = new FileWriter(tagsOutput); // write in tagsOutput file
            fileWriter.write("<Position, Length, Next Character>\n");

            BitOutputStream bitOutputStream = new BitOutputStream(new FileOutputStream(bytesOutput));

            int pos = 0;
            while (pos < data.length) {
                Match match = getMaximumMatch(pos, data);
                Tag tag = new Tag();
                if (match == null) {
                    tag.setPosition(0);
                    tag.setLength(0);
                    tag.setNext((char) data[pos]);
                    ++pos;
                } else {
                    tag.setPosition(match.getPosition());
                    tag.setLength(match.getLength());
                    pos += match.getLength();
                    if (pos < data.length) {
                        tag.setNext((char) data[pos]);
                    } else {
                        tag.setNext((char) 0);
                    }
                    ++pos;
                }
                int tagPosition = tag.getPosition();
                int tagLength = tag.getLength();
                char tagNext = tag.getNext();
                for (int i = searchBufferBits - 1; i >= 0; --i) {
                    bitOutputStream.write(((tagPosition >> i) & 1) == 1);
                }

                for (int i = lookAheadBufferBits - 1; i >= 0; --i) {
                    bitOutputStream.write(((tagLength >> i) & 1) == 1);
                }

                bitOutputStream.write((byte) tagNext);

                if ((byte) tag.getNext() == 0) {
                    fileWriter.write("<" + tag.getPosition() + ", " + tag.getLength() + ", null>\n");
                } else {
                    fileWriter.write("<" + tag.getPosition() + ", " + tag.getLength() + ", " + tag.getNext() + ">\n");
                }
            }

            fileWriter.close();
            bitOutputStream.close();
            ;

            System.out.println("Orginal size: " + data.length * 8 + " bits");
            System.out.println("Compressed size: " + bitOutputStream.getTotalNumberOfBits() + " bits");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Match getMaximumMatch(int currentPosition, byte[] data) {
        Match ret = new Match();
        int start = Math.max(0, currentPosition - (1 << searchBufferBits));
        int ed = Math.min(data.length, currentPosition + (1 << lookAheadBufferBits));
        int sz = currentPosition - start;
        for (int j = currentPosition + 1; j < ed; ++j) {
            byte[] bytesToCheck = Arrays.copyOfRange(data, currentPosition, j);
            for (int i = start; i < currentPosition; ++i) {
                int repeat = (bytesToCheck.length) / sz;
                int remaining = (bytesToCheck.length) % sz;
                byte[] tempData = new byte[bytesToCheck.length];
                for (int f = 0; f < repeat; ++f) {
                    int startIndex = f * sz;
                    System.arraycopy(data, i, tempData, startIndex, sz);
                }
                System.arraycopy(data, i, tempData, repeat * sz, remaining);
                if (Arrays.equals(tempData, bytesToCheck)) {
                    ret.setLength(j - currentPosition);
                    ret.setPosition(currentPosition - i);
                }
            }
        }
        if (ret.getLength() > 0) {
            return ret;
        }
        return null;
    }

    public void decompress(String bytesInput, String output) {
        try {
            BitInputStream bitInputStream = new BitInputStream(new FileInputStream(bytesInput));

            int position;
            int length;
            char next;
            String outputString = "";

            while (true) {
                position = bitInputStream.read(searchBufferBits);
                length = bitInputStream.read(lookAheadBufferBits);
                if (position == -1 || length == -1) {
                    break;
                }
                next = (char) bitInputStream.read(8);
                int retPosition = outputString.length() - position;
                int lastPosition = outputString.length();
                int counter = 0, pointer = retPosition;

                while (counter < length) {
                    outputString += outputString.charAt(pointer);
                    ++pointer;
                    if (pointer == lastPosition) {
                        pointer = retPosition;
                    }
                    ++counter;
                }

                if ((byte) next != 0) {
                    outputString += next;
                }
            }
            FileWriter fileWriter = new FileWriter(output);
            fileWriter.write(outputString);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
