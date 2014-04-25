package models;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.Url;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.db.ebean.Model;
import service.FileExtensionConverter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * Created by julien on 25/04/14.
 */

@Entity
public class Picture extends Model {

    static Cloudinary cloudinary = new Cloudinary("cloudinary://418129178764873:UYwfbVCuX5ekdIdq5W3a0e4E5Hs@dutfewtn5");

    @Id
    public UUID id;

    @Column
    public String contentType;

    @Column
    public String version;


    private static Finder<UUID, Picture> finder = new Finder<UUID, Picture>(
            UUID.class, Picture.class);

    public static Picture findById(final String id) {
        if (id == null) {
            return null;
        }
        final UUID uuid = UUID.fromString(id);

        return finder.byId(uuid);
    }


    public static void updatePicture(UUID id, final File file, final String contentType){
        Picture currentPicture = findById(id.toString());
        if (currentPicture != null){
            try {
                cloudinary.uploader().destroy(id.toString(), Cloudinary.emptyMap());

                Map params = Cloudinary.asMap(
                        "public_id", id.toString(),
                        "invalidate", true
                );
                Map uploadResult = cloudinary.uploader().upload(file, params);
                Logger.debug("Just published a picture on Cloudinary with id " + id.toString());

                currentPicture.contentType = contentType;
                currentPicture.version = uploadResult.get("version").toString();
                currentPicture.save();
            } catch (Exception e) {
                Logger.error("Unable to post Picture");
            }
        }
    }

    public static Picture save(final File file, final String contentType) {
        Picture result = new Picture();
        result.contentType = contentType;
        result.save();
        try {
            Map params = Cloudinary.asMap("public_id", result.id.toString());
            Map uploadResult = cloudinary.uploader().upload(file, params);
            result.version = uploadResult.get("version").toString();
            Logger.debug("Just published a picture on Cloudinary with id " + result.id.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Unable to post Picture");
        }
        result.save();
        return result;
    }

    public void delete() {
        try {
            cloudinary.uploader().destroy(id.toString(), Cloudinary.emptyMap());
        } catch (Exception e) {
            Logger.error("Unable to delete picture from Cloudinary");
        }
    }

    public String extension(){
        FileExtensionConverter converter = new FileExtensionConverter();
        return converter.ToExtensionType(contentType);
    }
    public static String extension(Picture p){
        FileExtensionConverter converter = new FileExtensionConverter();
        return converter.ToExtensionType(p.contentType);
    }

    private Url url_version(){
        if(version!=null && !version.isEmpty()){
            return cloudinary.url().version(version).secure(true);
        }else{
            return cloudinary.url().secure(true);
        }
    }

    public String url(){
        return url_version().generate(this.id+"") + "." + extension();
    }

    public static String url(Picture p){
        return p.url_version().generate(p.id + "." + extension(p));
    }

    public String url_h(int h){
        return url_version().transformation(new Transformation().height(h)).generate(this.id + "." + extension());
    }
    public String url_w(int w){
        return url_version().transformation(new Transformation().width(w)).generate(this.id + "." + extension());
    }

    public String transformation(final Integer width, final Integer height, final String transformation){
        return url_version().transformation(new Transformation().height(height).width(width).crop("fill")).generate(this.id + "." + extension());
    }


    public static Picture fetchPictureFromUrl(final String url) {
        if (url!=null) {
            try {
                File file=null;
                FileUtils.copyURLToFile(new URL(url), file);
                String contentType = new javax.activation.MimetypesFileTypeMap().getContentType(file);
                long fileSize = file.length();
                if((contentType.equals("image/gif") || contentType.equals("image/jpeg") || contentType.equals("image/png")) && (fileSize < 700000)) {
                    return Picture.save(file, contentType);
                }
            } catch (IOException e) {
                Logger.info(e.getMessage());
            }

        }

        return null;
    }

}