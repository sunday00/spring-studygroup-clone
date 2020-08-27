package lec.spring.studygroupclone.helpers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.config.AppConfig;
import lec.spring.studygroupclone.dataMappers.Profile;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Converter {

    public static String b64ToFile(Account account, Profile profile) throws IOException{
        if( profile.getProfileImage() == null || profile.getProfileImage().equals("") ) return null;

        String profileImagePath = "/accounts/" + account.getId() + ".png";
        String[] str_imgs = profile.getProfileImage().split(",");

        if( !str_imgs[0].equals("data:image/png;base64") ) return profileImagePath;

        byte[] img = Base64.getDecoder().decode(str_imgs[1]);

        FileOutputStream fileOutputStream = new FileOutputStream(AppConfig.UPLOAD_PATH + profileImagePath);
        fileOutputStream.write(img);

        return profileImagePath;
    }

    public static String b64ToFile(Study study, String image) throws IOException{
        String studyImagePath = "/studies/" + study.getId() + ".png";
        String[] str_imgs = image.split(",");

        byte[] img = Base64.getDecoder().decode(str_imgs[1]);

        FileOutputStream fileOutputStream = new FileOutputStream(AppConfig.UPLOAD_PATH + studyImagePath);
        fileOutputStream.write(img);

        return studyImagePath;
    }
}
