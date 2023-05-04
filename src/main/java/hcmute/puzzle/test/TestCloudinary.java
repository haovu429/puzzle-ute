package hcmute.puzzle.test;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import hcmute.puzzle.configuration.FreemarkerConfiguration;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.services.FilesStorageService;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCloudinary {

  @Autowired FilesStorageService filesStorageService;

  @Autowired private static FreemarkerConfiguration freeMarkerConfiguration;
  public static void main(String[] args) throws IOException {

    //Configuration freeMakerConfiguration = FreeMakerTemplateUtils.getFreeMakerConfiguration();
//    Configuration freeMakerConfig= freeMarkerConfiguration.freemarkerConfig().getConfiguration();
//    Template temp = freeMakerConfig.getTemplate("forgot_password.html");
    // testDetectImageTagHtml();
    // lib();
    // testDeleteManyFileCloudinary();
    testCloudinary();

  }

  public void testDeleteManyFileCloudinary() {
    List<String> deteleFilePublicIds = new ArrayList<>();
    deteleFilePublicIds.add("puzzle_ute/user/blog/KhiDenTrang.jpg2023-04-16T23:53:51_blog_image");
    deteleFilePublicIds.add("puzzle_ute/user/blog/KhiDenTrang.jpg2023-04-16T23:53:55_blog_image");
    deteleFilePublicIds.add("puzzle_ute/user/blog/KhiDenTrang.jpg2023-04-16T23:53:57_blog_image");
    filesStorageService.deleteMultiFile(
        deteleFilePublicIds, UserEntity.builder().email("haodeptry@gmail.com").build());
  }

  static void testDetectDeletedSrc() {
    String string =
        "<img src='path/to/image/name.jpg' />\n"
            + "<img src='path/to/image/3name.jpg'  alt=\"name here\"/><p>test card 22</p>\n"
            + "<img  src=\"path/to/image/name.jpg\"/><p>test card 22</p>\n"
            + "<img   src=\"http://media.cheggcdn.com/media%2Fd08%2Fd087a4d3-8e4e-4e42-8cf0-2b03238a33ef%2Fimage\"/><p>test card 22</p>\n"
            + "<img   src='http://media.cheggcdn.com/media%2Fd08%2Fd087a4d3-8e4e-4e42-8cf0-2b03238a33ef%2Fimage'/><p>test card 22</p>";

    String string2 =
        "<img src='path/to/image/name.jpg' />\n"
            + "<img src='path/to/image/name2.jpg'  alt=\"name here\"/><p>test card 22</p>\n"
            + "<img  src=\"path/to/image/name.jpg\"/><p>test card 22</p>\n"
            + "<img   src=\"http://media.cheggcdn.com/media%2Fd08%2Fd087a4d3-8e4e-4e42-8cf0-2b03238a33ef%2Fimage\"/><p>test card 22</p>\n"
            + "<img   src='http://media.cheggcdn.com/media%2Fd08%2Fd087a4d3-8e4e-4e42-8cf0-2b03238a33ef%2Fimage'/><p>test card 22</p>";
    List<String> oldSrcs = detectedImageSrcList(string);
    List<String> newSrcs = detectedImageSrcList(string2);
    newSrcs.add("Hao dep try");
    System.out.println("\n---Before!----");
    for (String src : oldSrcs) {
      System.out.println(src);
    }

    oldSrcs.addAll(newSrcs);
    List<String> mergeLists = oldSrcs.stream().collect(Collectors.toSet()).stream().toList();
    System.out.println("\n---Merge!----");
    for (String src : mergeLists) {
      System.out.println(src);
    }

    List<String> deletedImageSrcs = getDeletedImageSrcs(oldSrcs, newSrcs);
    System.out.println("\n---get deleted src!----");
    for (String src : deletedImageSrcs) {
      System.out.println(src);
    }
  }

  static List<String> getDeletedImageSrcs(List<String> oldList, List<String> newList) {
    List<String> deletedImageSrcs =
        oldList.stream().filter(item -> !newList.contains(item)).toList();
    return deletedImageSrcs;
  }

  static void testDetectImageTagHtml() {
    String content = "Hao dep try";

    String htmlFragment =
        "<img src='http://img01.ibnlive.in/ibnlive/uploads/2015/11/Videocon-Delite.gif' width='90' height='62'>Videocon Mobile Phones has launched three new Android smartphones - Z55 Delite, Z45 Dazzle, and Z45 Amaze with prices starting at Rs 4,599.";

    //    Pattern pattern =
    //            Pattern.compile( ".*(<img\\s+.*src\\s*=\\s*'([^']+)'.*>).*" );

    Pattern pattern = Pattern.compile(".*(<img\\s+.*src\\s*=\\s*'([^']+)'.*>).*");
    Matcher matcher = patternImg2.matcher(htmlFragment);
    if (matcher.matches()) {
      String match = matcher.group(1);
      String match1 = matcher.group(2);

      // match.replaceAll("'","");
      System.out.println(match);
      System.out.println(match1);
      // System.out.println(match2);

      String newString = htmlFragment.replaceAll(match, "");
      System.out.println(newString);
    }

    //    String html = "<img src='fsdf'/>";
    //    System.out.println(IsContainImg(html));

    //    Pattern p=null;
    //    Matcher m= null;
    //    String word0= null;
    //    String word1= null;
    //
    //    p= Pattern.compile(".*<img[^>]*src=\"([^\"]*)",Pattern.CASE_INSENSITIVE);
    //    m= p.matcher(txt);
    //    while (m.find())
    //    {
    //      word0=m.group(1);
    //      System.out.println(word0.toString());
    //    }

  }

  static final Pattern patternImg =
      Pattern.compile("<img(.+?)src=\"(.+?)\"(.+?)(onload=\"(.+?)\")?([^\"]+?)>");

  static final Pattern patternImg2 = Pattern.compile("\"<img.*?src=[\"|'](.*?)[\"|']\"gm");

  public static boolean IsContainImg(String html) {
    Matcher m = patternImg2.matcher(html);
    while (m.find()) {
      return true;
    }
    return false;
  }

  public static void lib() {
    final String regex = "<img.*?src=[\"|'](.*?)[\"|']";
    final String string =
        "<img src='path/to/image/name.jpg' />\n"
            + "<img src='path/to/image/name.jpg'  alt=\"name here\"/><p>test card 22</p>\n"
            + "<img  src=\"path/to/image/name.jpg\"/><p>test card 22</p>\n"
            + "<img   src=\"http://media.cheggcdn.com/media%2Fd08%2Fd087a4d3-8e4e-4e42-8cf0-2b03238a33ef%2Fimage\"/><p>test card 22</p>\n"
            + "<img   src='http://media.cheggcdn.com/media%2Fd08%2Fd087a4d3-8e4e-4e42-8cf0-2b03238a33ef%2Fimage'/><p>test card 22</p>";

    String htmlFragment =
        "<img src='http://img01.ibnlive.in/ibnlive/uploads/2015/11/Videocon-Delite.gif' width='90' height='62'>Videocon Mobile Phones has launched three new Android smartphones - Z55 Delite, Z45 Dazzle, and Z45 Amaze with prices starting at Rs 4,599.";
    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(string);

    while (matcher.find()) {
      System.out.println("Full match: " + matcher.group(0));

      for (int i = 1; i <= matcher.groupCount(); i++) {
        System.out.println("Group " + i + ": " + matcher.group(i));
      }
    }
  }

  static List<String> detectedImageSrcList(String html) {
    List<String> imageSrcList = new ArrayList<>();
    final String regex = "<img.*?src=[\"|'](.*?)[\"|']";
    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(html);
    while (matcher.find()) {
      // System.out.println("Full match: " + matcher.group(0));

      for (int i = 1; i <= matcher.groupCount(); i++) {
        imageSrcList.add(matcher.group(i));
        // System.out.println("Group " + i + ": " + matcher.group(i));
      }
    }

    return imageSrcList;
  }

  static void testCloudinary() {
    // Set your Cloudinary credentials

    // Dotenv dotenv = Dotenv.load();
    Cloudinary cloudinary = new Cloudinary(System.getenv("CLOUDINARY_URL"));
    cloudinary.config.secure = true;
    System.out.println("Cloud name: " + cloudinary.config.cloudName);

    try {
      // Upload the image
      Map params1 =
          ObjectUtils.asMap(
              "use_filename", true,
              "unique_filename", false,
              "overwrite", true);

      System.out.println(
          cloudinary
              .uploader()
              .upload(
                  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==",
                  params1));

      // Get the asset details
      Map params2 = ObjectUtils.asMap("quality_analysis", true);

      Map result = cloudinary.api().resource("coffee_cup", params2);

      String url = result.get("url").toString();

      String publicId = result.get("public_id").toString();

      System.out.println("url: " + url + " - public id: " + publicId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
