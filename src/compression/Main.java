package compression;

public class Main {

    public static void main(String[] args) {
        LZ77 lz = new LZ77();
        String input = "input.txt";
        String tagsOutput = "TagsOutput.txt";
        String bytesOutput = "BytesOutput.txt";
        String output = "output.txt";

        lz.compress(input, tagsOutput, bytesOutput);

        lz.decompress(bytesOutput, output);
    }
}
