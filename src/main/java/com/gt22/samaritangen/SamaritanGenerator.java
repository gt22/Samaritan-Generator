package com.gt22.samaritangen;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SamaritanGenerator {

	public static final Font SAMARITAN_FONT;

	static {
		try {
			SAMARITAN_FONT = Font.createFont(Font.TRUETYPE_FONT, SamaritanGenerator.class.getResourceAsStream("/samaritan.ttf")).deriveFont(Font.PLAIN, 54);
		} catch (FontFormatException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static WritableImage[] generateSamaritanTypingMessage(String msg, SamaritanColor color) {
		String[] words = msg.split("\\s");
		StringBuilder builder = new StringBuilder();
		return MiscUtils.ArrayUtils.map(words, w -> {
			BufferedImage ret = createSamaritanImage(builder.append(w).toString(), color);
			builder.append(' ');
			return SwingFXUtils.toFXImage(ret, null);
		}, WritableImage[]::new);
	}

	public static WritableImage[] generateSamaritanGif(String msg, SamaritanColor color, boolean offset) {
		String[] words = msg.split("\\s");
		return MiscUtils.ArrayUtils.map(words, (w, i) -> SwingFXUtils.toFXImage(createSamaritanImage(w, color, offset && i != 0), null), WritableImage[]::new);
	}

	public static BufferedImage createSamaritanImage(String msg, SamaritanColor color) {
		return createSamaritanImage(msg, color, false);
	}

	public static BufferedImage createSamaritanImage(String msg, SamaritanColor color, boolean useOffset) {
		//Create image
		BufferedImage samaritan = color.getBg();
		BufferedImage ret = new BufferedImage(samaritan.getWidth(), samaritan.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = ret.createGraphics();

		//Draw background
		g.drawImage(samaritan, 0, 0, samaritan.getWidth(), samaritan.getHeight(), null);

		//Prepare font
		g.setPaint(color.getFontColor());
		g.setFont(SAMARITAN_FONT);
		FontMetrics m = g.getFontMetrics();
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		//Compute positions
		int width = m.stringWidth(msg);
		int x = (int) ((samaritan.getWidth() / 2f) - (width / 2f) + getOffset(msg.length(), useOffset, m.stringWidth("X")));
		int y = 502;
		int lineX = x - 10;
		int lineY = 515;
		int lineEnd = x + width + 5;

		//Draw text
		g.drawString(msg, x, y);

		//Draw line
		g.drawLine(lineX, lineY, lineEnd, lineY);
		g.drawLine(lineX, lineY + 1, lineEnd, lineY + 1);

		return ret;
	}

	private static int getOffset(int msgLength, boolean useOffset, int letterWidth) {
		if(useOffset && msgLength > 2) { //Offset is not used for 2 or 1 letters
			if(msgLength % 2 == 0) { //If message length is even
				return (((msgLength / 2) - 2) * letterWidth) + (letterWidth / 2); //Position to center of second letter
				//COMMANDS
				// ^
			} else {
				return ((msgLength / 2) * letterWidth); //Position to center of first letter
				//ARE
				//^
			}
		}
		return 0;
	}

	public enum SamaritanColor {
		BLACK("Black", "sam_black.png", Color.WHITE),
		WHITE("White", "sam_white.png", Color.BLACK);

		private final String locale;
		private final BufferedImage bg;
		private final Color fontColor;

		SamaritanColor(String locale, String bg, Color fontColor) {
			this.locale = locale;
			try {
				this.bg = ImageIO.read(SamaritanGenerator.class.getResourceAsStream("/" + bg));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			this.fontColor = fontColor;
		}

		public String getLocale() {
			return locale;
		}

		public BufferedImage getBg() {
			return bg;
		}

		public Color getFontColor() {
			return fontColor;
		}

		@Override
		public String toString() {
			return getLocale();
		}
	}

}
