import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        String srcFolder = "C:/Users/perep/Pictures/oldPhoto";
        String dstFolder = "C:/Users/perep/Pictures/newPhoto";
        int targetSize = 600;
        File srcDir = new File(srcFolder);

        long start = System.currentTimeMillis();

        File[] files = srcDir.listFiles();
        int countOfCores = Runtime.getRuntime().availableProcessors();

        assert files != null;
        int filesInBucket = files.length/countOfCores;
        int end = filesInBucket;
        int begin=0;
        File[][] portion = new File[countOfCores][end];
        for (int i = 0;i < countOfCores; i++){
            if (i==countOfCores-1 && files.length % 2!=0){
                end+=1;
            }
            portion[i] = Arrays.copyOfRange(files,begin, end);
            begin=end;
            end=end+filesInBucket;
        }
        for (File[] c: portion){
            Thread thread = new Thread(()->{
                for (File picture:c){
                    resizeImage(picture.getPath(),dstFolder+"/"+picture.getName(), targetSize);
                }
            });
            thread.start();
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - start));
    }
    public static void resizeImage(String originalFilePath, String targetFilePath, int targetSize) {
        try {
            File sourceFile = new File(originalFilePath);
            BufferedImage originalImage = ImageIO.read(sourceFile);

            BufferedImage resizedImage = Scalr.resize(originalImage, targetSize);

            File resizedFile = new File(targetFilePath);
            ImageIO.write(resizedImage, "jpg", resizedFile);

            originalImage.flush();
            resizedImage.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
