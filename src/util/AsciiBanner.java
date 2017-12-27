package util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * generates ascii banner art.
 * @author jdowd
 *
 */
public class AsciiBanner {
	/**
	 * the default size of the font.
	 */
	private final int DEFAULT_FONT_SIZE = 13;

	/**
	 * @param banner the text of the banner
	 * @return a list of TableBuilders with the messages
	 * @throws IOException if there is an IO exception
	 */
	public List<TableBuilder> getBanner(String banner) throws IOException {
		int fontSize = DEFAULT_FONT_SIZE;
		String font = "Verdana";
		return getBanner(banner, font, fontSize);
	}

	/**
	 * @param banner the text of the banner
	 * @param font the font to user for the banner
	 * @return a list of TableBuilders with the messages
	 * @throws IOException if there is an IO exception
	 */
	public List<TableBuilder> getBanner(String banner, String font) throws IOException {
		int fontSize = DEFAULT_FONT_SIZE;
		return getBanner(banner, font, fontSize);
	}

	/**
	 * @param banner the text of the banner
	 * @param font the font to user for the banner
	 * @param pFontSize the size of the font to use
	 * @return a list of TableBuilders with the messages
	 * @throws IOException if there is an IO exception
	 */
	public List<TableBuilder> getBanner(String banner, String font, int pFontSize) throws IOException {
		List<TableBuilder> outMessages = new ArrayList<TableBuilder>();
		int fontSize = pFontSize;
		
		int height;
		
		//can only use even heights
		if ( (fontSize & 1) == 0 ) { 
			//even... 
			height = fontSize;
		} else { 
			//odd... 
			height = fontSize+1;
		}
		
		
		int width = banner.length() * height;
		// 
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();

		//set the font
		g.setFont(new Font(font, Font.PLAIN, fontSize));
		Graphics2D graphics = (Graphics2D) g;
		
		//draw the graphic
		graphics.drawString(banner, (1), (height - 1));

		//look at all the pixels - increment y by two to print nice
		for (int y = 0; y < height; y = y + 2) {

			StringBuilder sb = new StringBuilder();
			// for each pixel by width
			for (int x = 0; x < width; x++) {
				int rgb1 = image.getRGB(x, y);
				int rgb2 = image.getRGB(x, y + 1);

				if (rgb1 == -16777216 && rgb2 == -16777216) {
					sb.append(" ");

				} else if (rgb1 == -1 && rgb2 == -1) {
					sb.append("$");

				} else if (rgb1 == -1 && rgb2 != -1) {

					sb.append("*");
				} else if (rgb2 == -1 && rgb1 != -1) {

					sb.append("$");
				} else if (rgb1 != -1 && rgb2 != -1) {
					sb.append("*");
				}
			}
			if (!sb.toString().trim().isEmpty()) {

				outMessages.add(new TableBuilder(sb.toString().replaceFirst("\\s++$", "")));

			} 

		}
		return outMessages;
	}

}
