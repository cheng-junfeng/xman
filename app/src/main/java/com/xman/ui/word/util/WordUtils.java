package com.xman.ui.word.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;

public class WordUtils {
    private final static String TAG = "WordUtils";
    private final static int BIG_IMAGE = 10000;
    private final static int SET_WIDTH = 30;

    public String htmlPath;
    private String docPath;
    private String picturePath;
    private int presentPicture = 0;
    private FileOutputStream output;

    private String htmlBegin = "<html><meta charset=\"utf-8\"><body>";
    private String htmlEnd = "</body></html>";
    private String tableBegin = "<table style=\"border-collapse:collapse\" border=1 bordercolor=\"black\">";
    private String tableEnd = "</table>";
    private String rowBegin = "<tr>", rowEnd = "</tr>";
    private String columnBegin = "<td>", columnEnd = "</td>";
    private String lineBegin = "<p>", lineEnd = "</p>";
    private String centerBegin = "<center>", centerEnd = "</center>";
    private String boldBegin = "<b>", boldEnd = "</b>";
    private String underlineBegin = "<u>", underlineEnd = "</u>";
    private String fontSizeTag = "<font size=\"%d\">";
    private String fontEnd = "</font>";
    private String spanColor = "<span style=\"color:%s;\">", spanEnd = "</span>";
    private String divRight = "<div align=\"right\">", divEnd = "</div>";
    private String imgBegin = "<img src=\"%s\" >";
    private String imgBeginBig = "<div><img src=\"%s\" width = \""+SET_WIDTH+"\" height = \"%d\" align=left></div>";;
    public WordUtils(String doc_name) {
        docPath = doc_name;
        htmlPath = FileUtil.createFile("html", FileUtil.getFileName(docPath) + ".html");
        Log.d(TAG, "htmlPath=" + htmlPath);
        try {
            output = new FileOutputStream(new File(htmlPath));
            presentPicture = 0;
            output.write(htmlBegin.getBytes());
            readDOCX();
            output.write(htmlEnd.getBytes());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readDOCX() {
        try {
            ZipFile docxFile = new ZipFile(new File(docPath));
            ZipEntry sharedStringXML = docxFile.getEntry("word/document.xml");
            InputStream inputStream = docxFile.getInputStream(sharedStringXML);
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(inputStream, "utf-8");
            boolean isTable = false; // 表格
            boolean isSize = false; // 文字大小
            boolean isColor = false; // 文字颜色
            boolean isCenter = false; // 居中对齐
            boolean isRight = false; // 靠右对齐
            boolean isItalic = false; // 斜体
            boolean isUnderline = false; // 下划线
            boolean isBold = false; // 加粗
            boolean isRegion = false; // 在那个区域中
            int pic_ndex = 1; // docx中的图片名从image1开始，所以索引从1开始
            int event_type = xmlParser.getEventType();
            while (event_type != XmlPullParser.END_DOCUMENT) {
                switch (event_type) {
                    case XmlPullParser.START_TAG: // 开始标签
                        String tagBegin = xmlParser.getName();
                        if (tagBegin.equalsIgnoreCase("r")) {
                            isRegion = true;
                        }
                        if (tagBegin.equalsIgnoreCase("jc")) { // 判断对齐方式
                            String align = xmlParser.getAttributeValue(0);
                            if (align.equals("center")) {
                                output.write(centerBegin.getBytes());
                                isCenter = true;
                            }
                            if (align.equals("right")) {
                                output.write(divRight.getBytes());
                                isRight = true;
                            }
                        }
                        if (tagBegin.equalsIgnoreCase("color")) { // 判断文字颜色
                            String color = xmlParser.getAttributeValue(0);
                            output.write(String.format(spanColor, color).getBytes());
                            isColor = true;
                        }
                        if (tagBegin.equalsIgnoreCase("sz")) { // 判断文字大小
                            if (isRegion == true) {
                                int size = getSize(Integer.valueOf(xmlParser.getAttributeValue(0)));
                                output.write(String.format(fontSizeTag, size).getBytes());
                                isSize = true;
                            }
                        }
                        if (tagBegin.equalsIgnoreCase("tbl")) { // 检测到表格
                            output.write(tableBegin.getBytes());
                            isTable = true;
                        } else if (tagBegin.equalsIgnoreCase("tr")) { // 表格行
                            output.write(rowBegin.getBytes());
                        } else if (tagBegin.equalsIgnoreCase("tc")) { // 表格列
                            output.write(columnBegin.getBytes());
                        }
                        if (tagBegin.equalsIgnoreCase("pic")) { // 检测到图片
                            ZipEntry pic_entry = FileUtil.getPicEntry(docxFile, pic_ndex);
                            if (pic_entry != null) {
                                byte[] pictureBytes = FileUtil.getPictureBytes(docxFile, pic_entry);
                                writeDocumentPicture(pictureBytes);
                            }
                            pic_ndex++; // 转换一张后，索引+1
                        }
                        if (tagBegin.equalsIgnoreCase("p") && !isTable) {// 检测到段落，如果在表格中就无视
                            output.write(lineBegin.getBytes());
                        }
                        if (tagBegin.equalsIgnoreCase("b")) { // 检测到加粗
                            isBold = true;
                        }
                        if (tagBegin.equalsIgnoreCase("u")) { // 检测到下划线
                            isUnderline = true;
                        }
                        // 检测到文本
                        if (tagBegin.equalsIgnoreCase("t")) {
                            if (isBold == true) { // 加粗
                                output.write(boldBegin.getBytes());
                            }
                            if (isUnderline == true) { // 检测到下划线，输入<u>
                                output.write(underlineBegin.getBytes());
                            }
                            String text = xmlParser.nextText();
                            output.write(text.getBytes()); // 写入文本
                            if (isUnderline == true) { // 输入下划线结束标签</u>
                                output.write(underlineEnd.getBytes());
                                isUnderline = false;
                            }
                            if (isBold == true) { // 输入加粗结束标签</b>
                                output.write(boldEnd.getBytes());
                                isBold = false;
                            }
                            if (isSize == true) { // 输入字体结束标签</font>
                                output.write(fontEnd.getBytes());
                                isSize = false;
                            }
                            if (isColor == true) { // 输入跨度结束标签</span>
                                output.write(spanEnd.getBytes());
                                isColor = false;
                            }
//                            if (isCenter == true) { // 输入居中结束标签</center>。要在段落结束之前再输入该标签，因为该标签会强制换行
//                                output.write(centerEnd.getBytes());
//                                isCenter = false;
//                            }
                            if (isRight == true) { // 输入区块结束标签</div>
                                output.write(divEnd.getBytes());
                                isRight = false;
                            }
                        }
                        break;
                    // 结束标签
                    case XmlPullParser.END_TAG:
                        String tagEnd = xmlParser.getName();
                        if (tagEnd.equalsIgnoreCase("tbl")) { // 输入表格结束标签</table>
                            output.write(tableEnd.getBytes());
                            isTable = false;
                        }
                        if (tagEnd.equalsIgnoreCase("tr")) { // 输入表格行结束标签</tr>
                            output.write(rowEnd.getBytes());
                        }
                        if (tagEnd.equalsIgnoreCase("tc")) { // 输入表格列结束标签</td>
                            output.write(columnEnd.getBytes());
                        }
                        if (tagEnd.equalsIgnoreCase("p")) { // 输入段落结束标签</p>，如果在表格中就无视
                            if (isTable == false) {
                                if (isCenter == true) { // 输入居中结束标签</center>
                                    output.write(centerEnd.getBytes());
                                    isCenter = false;
                                }
                                output.write(lineEnd.getBytes());
                            }
                        }
                        if (tagEnd.equalsIgnoreCase("r")) {
                            isRegion = false;
                        }
                        break;
                    default:
                        break;
                }
                event_type = xmlParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSize(int sizeType) {
        if (sizeType >= 1 && sizeType <= 8) {
            return 1;
        } else if (sizeType >= 9 && sizeType <= 11) {
            return 2;
        } else if (sizeType >= 12 && sizeType <= 14) {
            return 3;
        } else if (sizeType >= 15 ) {
            return 4;
        } else {
            return 3;
        }
    }

    public void writeDocumentPicture(byte[] pictureBytes) {
        int bigSize = pictureBytes.length;
        picturePath = FileUtil.createFile("html", FileUtil.getFileName(docPath) + presentPicture + ".jpg");
        FileUtil.writePicture(picturePath, pictureBytes);
        presentPicture++;
        String imageString = String.format(imgBegin, picturePath);
        if(bigSize > BIG_IMAGE){
            int height = getImageHeight(picturePath, SET_WIDTH);
            imageString = String.format(imgBeginBig, picturePath, height);
        }
        try {
            output.write(imageString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getImageHeight(String path, int width){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        return (width*options.outHeight)/options.outWidth;
    }
}
