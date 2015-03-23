package com.clickntap.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.xml.bind.DatatypeConverter;

public class ImageUtils {

	public static BufferedImage rotate(BufferedImage img, double angle) {
		double sin = Math.abs(Math.sin(Math.toRadians(angle))), cos = Math.abs(Math.cos(Math.toRadians(angle)));
		int w = img.getWidth(null), h = img.getHeight(null);
		int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(h * cos + w * sin);
		BufferedImage bimg = createImage(neww, newh);
		Graphics2D g = bimg.createGraphics();
		g.translate((neww - w) / 2, (newh - h) / 2);
		g.rotate(Math.toRadians(angle), w / 2, h / 2);
		g.drawRenderedImage(img, null);
		g.dispose();
		return bimg;
	}

	public static Image mask(BufferedImage img, Color color) {
		BufferedImage bimg = createImage(img.getWidth(null), img.getHeight(null));
		Graphics2D g = bimg.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		for (int y = 0; y < bimg.getHeight(); y++) {
			for (int x = 0; x < bimg.getWidth(); x++) {
				int col = bimg.getRGB(x, y);
				if (col == color.getRGB()) {
					bimg.setRGB(x, y, col & 0x00ffffff);
				}
			}
		}
		return bimg;
	}

	public static BufferedImage base64ToImage(String base64) throws Exception {
		BufferedImage image = null;
		base64 = base64.substring(base64.indexOf(",") + 1);
		byte[] data = DatatypeConverter.parseBase64Binary(base64);
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		image = ImageIO.read(in);
		in.close();
		return image;
	}

	public static boolean hasAlpha(Image image) throws Exception {
		if (image instanceof BufferedImage) {
			BufferedImage bufferedImage = (BufferedImage) image;
			return bufferedImage.getColorModel().hasAlpha();
		}
		PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
		pixelGrabber.grabPixels();
		ColorModel colorModel = pixelGrabber.getColorModel();
		return colorModel.hasAlpha();
	}

	public static BufferedImage cropRect(BufferedImage bufferedImage) {
		return cropRect(bufferedImage, 1.0f, true);
	}

	public static BufferedImage cropRect(BufferedImage bufferedImage, float ratio) {
		return cropRect(bufferedImage, ratio, true);
	}

	public static BufferedImage createImage(int width, int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	public static BufferedImage letterbox(BufferedImage bufferedImage, int width, int height) throws Exception {
		return letterbox(bufferedImage, width, height, Color.BLACK);
	}

	public static BufferedImage letterbox(BufferedImage bufferedImage, int width, int height, Color backColor) throws Exception {
		int imgWidth = width;
		int imgHeight = height;

		BufferedImage tempImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = tempImg.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (backColor != null) {
			graphics2D.setBackground(backColor);
			graphics2D.clearRect(0, 0, width, height);
		}
		double constrainRatio = (double) width / (double) height;
		double imageRatio = (double) bufferedImage.getWidth() / (double) bufferedImage.getHeight();
		if (constrainRatio < imageRatio)
			imgHeight = (int) (width / imageRatio);
		else
			imgWidth = (int) (height * imageRatio);

		bufferedImage = ImageUtils.scale(bufferedImage, imgWidth, imgHeight);
		graphics2D.drawImage(bufferedImage, (width - imgWidth) / 2, (height - imgHeight) / 2, imgWidth, imgHeight, null);
		graphics2D.dispose();
		bufferedImage = tempImg;

		return bufferedImage;
	}

	public static BufferedImage drawImage(BufferedImage img1, BufferedImage img2, int x, int y, int w, int h) {
		Graphics2D graphics2D = img1.createGraphics();
		graphics2D.drawImage(img2, x, y, w, h, null);
		graphics2D.dispose();
		return img1;
	}

	public static BufferedImage drawImage(BufferedImage img1, BufferedImage img2) {
		Graphics2D graphics2D = img1.createGraphics();
		graphics2D.drawImage(img2, (img1.getWidth() - img2.getWidth()) / 2, (img1.getHeight() - img2.getHeight()) / 2, img2.getWidth(), img2.getHeight(), null);
		graphics2D.dispose();
		return img1;
	}

	public static BufferedImage drawImage(BufferedImage img1, String file) throws Exception {
		return drawImage(img1, open(file));
	}

	public static BufferedImage drawImage(BufferedImage img1, String file, int x, int y, int w, int h) throws Exception {
		return drawImage(img1, open(file), x, y, w, h);
	}

	public static BufferedImage drawImage(BufferedImage img1, File file) throws Exception {
		return drawImage(img1, open(file));
	}

	public static BufferedImage drawSubImage(BufferedImage image, String file, int x, int y, int w, int h) throws IOException {
		return drawSubImage(image, open(file), x, y, w, h);
	}

	public static BufferedImage drawSubImage(BufferedImage img1, BufferedImage img2, int x, int y, int w, int h) {
		Graphics2D graphics2D = img1.createGraphics();
		if (x < 0) {
			x = -x;
			w += x;
		}
		if (y < 0) {
			y = -y;
			h += y;
		}
		graphics2D.drawImage(img2.getSubimage(x, y, w, h), 0, 0, w, h, null);
		graphics2D.dispose();
		return img1;
	}

	public static BufferedImage expand(BufferedImage img, int w, int h) {
		BufferedImage newImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = newImg.createGraphics();
		graphics2D.drawImage(img, (w - img.getWidth()) / 2, (h - img.getHeight()) / 2, img.getWidth(), img.getHeight(), null);
		graphics2D.dispose();
		return newImg;
	}

	public static BufferedImage expand(BufferedImage img, int w, int h, int x, int y) {
		BufferedImage newImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = newImg.createGraphics();
		graphics2D.drawImage(img, x, y, img.getWidth(), img.getHeight(), null);
		graphics2D.dispose();
		return newImg;
	}

	public static void fillRect(BufferedImage image, Color color) {
		Graphics2D g2d = image.createGraphics();
		g2d.setBackground(color);
		g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2d.dispose();
	}

	public static void fillRect(BufferedImage image, Color color, int x, int y, int w, int h) {
		Graphics2D g2d = image.createGraphics();
		g2d.setBackground(color);
		g2d.clearRect(x, y, w, h);
		g2d.dispose();
	}

	public static BufferedImage cropRect(BufferedImage bufferedImage, float ratio, boolean centered) {
		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();
		float imageRatio = (float) imageWidth / imageHeight;
		if (ratio > imageRatio) {
			int offset = 0;
			int h = (int) (imageHeight * imageRatio / ratio);
			if (centered) {
				offset = (imageHeight - h) / 2;
			}
			return ImageUtils.subImage(bufferedImage, 0, offset, imageWidth, h);
		} else {
			int offset = 0;
			int w = (int) (imageWidth * ratio / imageRatio);
			if (centered) {
				offset = (imageWidth - w) / 2;
			}
			return ImageUtils.subImage(bufferedImage, offset, 0, w, imageHeight);
		}
	}

	public static BufferedImage cropBottom(BufferedImage bufferedImage, int percentage) {
		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();
		return ImageUtils.subImage(bufferedImage, 0, 0, imageWidth, imageHeight - imageHeight * percentage / 100);
	}

	public static BufferedImage crop(BufferedImage bufferedImage, int w, int h) {
		return ImageUtils.subImage(bufferedImage, 0, 0, w, h);
	}

	public static BufferedImage subImage(BufferedImage bufferedImage, int x1, int y1, int x2, int y2) {
		return bufferedImage.getSubimage(x1, y1, x2, y2);
	}

	public static BufferedImage centeredSubImage(BufferedImage bufferedImage, int w, int h) {
		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();
		return subImage(bufferedImage, (imageWidth - w) / 2, (imageHeight - h) / 2, w, h);
	}

	public static BufferedImage scaleHeight(BufferedImage bufferedImage, int h) throws Exception {
		if (h == bufferedImage.getHeight())
			return bufferedImage;
		int w = (bufferedImage.getWidth() * h) / (bufferedImage.getHeight());
		return scale(bufferedImage, w, h);
	}

	public static BufferedImage scaleWidth(BufferedImage bufferedImage, int w) throws Exception {
		if (w == bufferedImage.getWidth())
			return bufferedImage;
		int h = (bufferedImage.getHeight() * w) / (bufferedImage.getWidth());
		return scale(bufferedImage, w, h);
	}

	public static BufferedImage scale(BufferedImage bufferedImage, int w, int h) throws Exception {
		BufferedImage tempImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = tempImg.createGraphics();
		graphics2D.drawImage(bufferedImage.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
		graphics2D.dispose();
		return tempImg;
	}

	public static BufferedImage scale(BufferedImage bufferedImage, float percent) throws Exception {
		int x = (int) ((float) bufferedImage.getWidth() * percent);
		int y = (int) ((float) bufferedImage.getHeight() * percent);
		return scale(bufferedImage, x, y);
	}

	public static void saveAs(BufferedImage bufferedImage, File file) throws IOException {
		String format = file.getName().substring(file.getName().lastIndexOf('.') + 1);
		saveAs(bufferedImage, format, new FileOutputStream(file));
	}

	public static void saveAs(BufferedImage bufferedImage, String fileName) throws IOException {
		saveAs(bufferedImage, new File(fileName));
	}

	public static void saveAs(BufferedImage bufferedImage, String format, OutputStream out) throws IOException {
		ImageIO.write(bufferedImage, format, out);
		out.close();
	}

	public static BufferedImage open(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists())
			throw new IOException(" file '" + file.getCanonicalPath() + "' not found");
		return ImageIO.read(file);
	}

	public static BufferedImage open(File file) throws IOException {
		return ImageIO.read(new FileInputStream(file));
	}

	public static BufferedImage open(InputStream in) throws IOException {
		return ImageIO.read(in);
	}

	public static BufferedImage open(URL url) throws IOException {
		return ImageIO.read(url);
	}

	public static void saveAsJpeg(BufferedImage image, int quality, String dest) throws Exception {
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
		saveAsJpeg(image, quality, out);
		out.close();
	}

	public static void saveAsJpeg(BufferedImage image, int quality, OutputStream out) throws Exception {
		BufferedImage jpeg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = jpeg.createGraphics();
		graphics2D.drawImage(image, 0, 0, null);
		graphics2D.dispose();
		quality = Math.max(0, Math.min(quality, 100));
		ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName("jpeg").next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality((float) quality / 100.0f);
		writer.setOutput(ImageIO.createImageOutputStream(out));
		writer.write(null, new IIOImage(jpeg, null, null), iwp);
		writer.dispose();
	}

	public static Rectangle2D getTextBound(BufferedImage image, String s, Font font) {
		Graphics2D graphics2D = image.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (font != null)
			graphics2D.setFont(font);
		FontMetrics fm = graphics2D.getFontMetrics();
		Rectangle2D r2d = fm.getStringBounds(s, graphics2D);
		return r2d;
	}

	public static BufferedImage drawText(BufferedImage image, String s, int x, int y, int width, int height, float linespace, Font font, Color color) {
		Graphics2D graphics2D = image.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (font != null)
			graphics2D.setFont(font);
		FontMetrics fm = graphics2D.getFontMetrics();
		Rectangle2D r2d = fm.getStringBounds(s, graphics2D);
		if (r2d.getHeight() > height)
			return image;
		int rowh = (int) (r2d.getHeight() * linespace);
		String[] ss = s.split("\\s");
		Vector<String> rows = new Vector();
		String ws = ConstUtils.EMPTY;
		String cs = ConstUtils.EMPTY;
		int ww = 0;
		boolean dotted = false;
		for (int i = 0; i < ss.length && rows.size() * rowh <= height && !dotted; i++) {
			cs = ws;
			if (ww > 0)
				ws = ws.concat(" ").concat(ss[i]);
			else
				ws = ss[i];
			r2d = fm.getStringBounds(ws, graphics2D);
			if (r2d.getWidth() >= width && ((rows.size() + 2) * rowh > height || ww == 0))
				for (int ix = ws.length(); !dotted && ix > 1; ix--) {
					ws = ws.substring(0, ix - 1).concat("...");
					r2d = fm.getStringBounds(ws, graphics2D);
					if (r2d.getWidth() <= width) {
						rows.add(ws);
						dotted = true;
					}
				}
			else if (r2d.getWidth() == width) {
				rows.add(ws);
				ww = 0;
			} else if (r2d.getWidth() > width) {
				rows.add(cs);
				ww = 0;
				--i;
			} else
				ww++;
		}
		if (!dotted && ww > 0 && rows.size() * rowh <= height)
			rows.add(ws);
		if (color != null)
			graphics2D.setColor(color);
		for (int i = 0; i < rows.size(); i++)
			graphics2D.drawString(rows.get(i), x, (i * rowh) + y + fm.getAscent());
		return image;
	}
}
