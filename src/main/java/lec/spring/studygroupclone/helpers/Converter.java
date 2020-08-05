package lec.spring.studygroupclone.helpers;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class Converter {

    public static boolean b64ToFile(String base64, String target) throws IOException{

        String data = base64.split(",")[1];

        byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);

        BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(imageBytes));

        ImageIO.write(bufImg, "jpg", new File(target));

        return true;
    }
}
