package com.clickntap.tool.doc;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SmartPdf {

    private Document doc;
    private ByteArrayOutputStream data;
    private Map<String, Font> fonts;

    public void open() throws Exception {
        fonts = new HashMap<String, Font>();
        doc = new Document(PageSize.A4, 80, 80, 80, 80);
        data = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, data);
        doc.open();
    }

    public void close() throws Exception {
        doc.close();
    }

    public void addStyle(String name, int family, int size, int style, int r, int g, int b) throws DocumentException, IOException {
        BaseFont bf = BaseFont.createFont("/Users/tmendici/Library/Fonts/Sommet Rounded Light.otf", "", BaseFont.NOT_EMBEDDED);
        fonts.put(name, new Font(bf, size, style));
    }

    public void newPage() throws Exception {
        doc.newPage();
    }

    public void newLine() throws Exception {
        doc.add(Chunk.NEWLINE);
    }

    public Chapter newChapter(Paragraph p) throws Exception {
        Chapter chapter = new ChapterAutoNumber(p);
        return chapter;
    }

    public Paragraph newParagraph(String text) throws Exception {
        return new Paragraph(text, normalFont());
    }

    public Paragraph newParagraph() throws Exception {
        Paragraph p = new Paragraph();
        p.setFont(normalFont());
        return p;
    }

    private Font normalFont() {
        return fonts.get("normal");
    }

    public Paragraph newParagraph(String text, String style) throws Exception {
        return new Paragraph(text, fonts.get(style));
    }

    public Phrase newPhrase(String text) {
        return new Phrase(text, normalFont());
    }

    public Phrase newPhrase(String text, String style) {
        return new Phrase(text, fonts.get(style));
    }

    public PdfPTable newTable(int cols) throws BadElementException {
        PdfPTable table = new PdfPTable(cols);
        return table;
    }

    public PdfPCell newCell(Phrase text) throws BadElementException {
        PdfPCell cell = new PdfPCell(text);
        return cell;
    }

    public List newList(boolean numbered, int points) throws BadElementException {
        List list = new List(numbered, points);
        return list;
    }

    public ListItem newListItem(String text) throws BadElementException {
        ListItem item = new ListItem();
        item.setFont(normalFont());
        item.add(text);
        return item;
    }

    public Color newColor(int r, int g, int b) throws BadElementException {
        return new Color(r, g, b);
    }

    public Chunk newImage(String url) throws Exception {
        return new Chunk(Image.getInstance(url), 0, 0);
    }

    public void add(Element element) throws DocumentException {
        doc.add(element);
    }

    public void copyTo(OutputStream out) throws Exception {
        out.write(data.toByteArray());
    }
}
