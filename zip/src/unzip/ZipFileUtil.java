package unzip;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * 压缩文件工具类
 * @author dyeed
 */
public class ZipFileUtil {
    public static final String DIRECTORY_TYPE = "1";
    public static final String FILE_TYPE = "2";
    
    /**
     * 不解压压缩文件，获取其中的所有文件（夹）信息
     * @param o String（zip文件的绝对路径） 或 File对象
     * @return {文件序号 = {文件类型=文件夹或文件, 文件名=压缩文件中的路径+文件名}}
     * eg:{1 = {type = 2, name = SXZYY/index.html}}
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, String>> readContent(Object o) {
        Map<String, Map<String, String>> content = new HashMap<>();
        Map<String, String> f;
        ZipFile zf = null;
        try {
            if (o instanceof String) {
                zf = new ZipFile((String) o);
            } else if (o instanceof File) {
                zf = new ZipFile((File) o);
            } else {
                throw new IllegalArgumentException("The argument type is not illegal.Must give String(AbsoluteFilePath) OR File.");
            }
            int num = 0;
            for (Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zf.entries(); entries.hasMoreElements(); ) {
                num++;
                f = new HashMap<>(2);
                ZipEntry ze = entries.nextElement();
                String name = ze.getName();
                if (ze.isDirectory()) {
                    f.put("type", DIRECTORY_TYPE);
                } else {
                    f.put("type", FILE_TYPE);
                }
                f.put("name", name);
                content.put(String.valueOf(num), f);
            }
        } catch (ZipException e) {
            System.out.println("INFO: The file format is not a common zip.");
        } catch (IOException e) {
            System.out.println("WARNING: IOException occurred.");
        } catch (SecurityException e) {
            System.out.println("WARNING: The file is not accessible.");
        } catch (NullPointerException e) {
            System.out.println("WARNING: NullPointerException. May due to ZipEntry has no Size.");
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException e) {
                    System.out.println("WARNING: IOException occurred when close file.");
                }
            }
        }
        return content;
    }
    
    public static void main(String[] args) {
        try {
            Map<String, Map<String, String>> fMap = readContent("C:\\Users\\dyeed\\Desktop\\demo.zip");
            System.out.println(fMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
