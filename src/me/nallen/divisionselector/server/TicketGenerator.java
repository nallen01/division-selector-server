package me.nallen.divisionselector.server;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class TicketGenerator {
	private static final PDFont BOLD_FONT = PDType1Font.HELVETICA_BOLD;
	private static final PDFont NORMAL_FONT = PDType1Font.HELVETICA;
	
	private static final double TITLE_FONT_SIZE = 0.12;
	private static final double TEXT_FONT_SIZE = 0.08;
	private static final float QR_CODE_SIZE = 0.8f;
	private static final float QR_CODE_CENTER_POSITION = 0.7f;
	private static final float NUMBER_CENTER_POSITION = 0.4f;
	private static final float NAME_CENTER_POSITION = 0.25f;
	private static final float SCHOOL_CENTER_POSITION = 0.15f;
	private static final float PADDING = 0.05f;
	
	public static void createTicketPDF(String outputFile, int per_page_power) {
		int per_row = 0;
		float page_width = 0;
		float page_height = 0;
		float ticket_width = 0;
		float ticket_height = 0;
		int step = per_page_power / 2;
		int per_page = (int) Math.pow(2, per_page_power);
		
		if(per_page_power % 2 == 1) {
			per_row = (int) Math.pow(2, step+1);
			page_width = PDPage.PAGE_SIZE_A4.getHeight();
			page_height = PDPage.PAGE_SIZE_A4.getWidth();
			ticket_width = page_width / per_row;
			ticket_height = (float) (page_height / Math.pow(2, step));
		}
		else {
			per_row = (int) Math.pow(2, step);
			page_width = PDPage.PAGE_SIZE_A4.getWidth();
			page_height = PDPage.PAGE_SIZE_A4.getHeight();
			ticket_width = page_width / per_row;
			ticket_height = (float) (page_height / Math.pow(2, step));
		}
		
		int titleFontSize = (int) (TITLE_FONT_SIZE * ticket_width);
		int textFontSize = (int) (TEXT_FONT_SIZE * ticket_width);
		
		try {
			PDDocument doc = new PDDocument();
			
			int count = 0;
			PDPage page = null;
			PDPageContentStream pageContentStream = null;
			float currentX = 0;
			float currentY = 0;
			
			for(Team team : DivisionSelectorServer.divisionData.getAllTeams()) {
				if(count % per_page == 0) {
					page = new PDPage(PDPage.PAGE_SIZE_A4);
					if(per_page_power % 2 == 1)
						page.setRotation(90);
					doc.addPage(page);
					
					if(pageContentStream != null) {
						pageContentStream.close();
					}
					
					pageContentStream = new PDPageContentStream(doc, page);
					currentY = page_height;
					
					if(per_page_power % 2 == 1)
						pageContentStream.concatenate2CTM(0, 1, -1, 0, page_height, 0);
				}
				
				if(count % per_row == 0) {
					currentX = 0;
					currentY -= ticket_height;
				}
				
				// Draw the borders
				pageContentStream.drawLine(currentX + ticket_width, currentY, currentX + ticket_width, currentY + ticket_height);
				pageContentStream.drawLine(currentX, currentY, currentX + ticket_width, currentY);
				
				// Add the QR Code
				BitMatrix qr = createQRCode(team.number);
				drawQRCode(qr, QR_CODE_SIZE * ticket_width, pageContentStream, currentX + (ticket_width / 2), currentY + (QR_CODE_CENTER_POSITION * ticket_height));
				
				// Add the team number and other information
				float padding_amount = PADDING * ticket_width;
				
				int numberFontSize = getFontSizeThatFits(titleFontSize, BOLD_FONT, team.number, ticket_width - 2 * padding_amount);
				int nameFontSize = getFontSizeThatFits(textFontSize, NORMAL_FONT, team.name, ticket_width - 2 * padding_amount);
				int schoolFontSize = getFontSizeThatFits(textFontSize, NORMAL_FONT, team.school, ticket_width - 2 * padding_amount);
				
				centerText(team.number, BOLD_FONT, numberFontSize, pageContentStream, currentX + (ticket_width / 2), currentY + (NUMBER_CENTER_POSITION * ticket_height));
				centerText(team.name, NORMAL_FONT, nameFontSize, pageContentStream, currentX + (ticket_width / 2), currentY + (NAME_CENTER_POSITION * ticket_height));
				centerText(team.school, NORMAL_FONT, schoolFontSize, pageContentStream, currentX + (ticket_width / 2), currentY + (SCHOOL_CENTER_POSITION * ticket_height));
					
				currentX += ticket_width;
				count++;
			}
			
			if(pageContentStream != null) {
				pageContentStream.close();
			}
			
			doc.save(outputFile);
			doc.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void centerText(String text, PDFont font, int size, PDPageContentStream pageContentStream, float x, float y) throws IOException {
		float textWidth = font.getStringWidth(text) / 1000 * size;
		float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * size;
		
		pageContentStream.beginText();
		pageContentStream.setFont(font, size);
		pageContentStream.moveTextPositionByAmount(x - (textWidth / 2), y - (textHeight / 2));
		pageContentStream.drawString(text);
		pageContentStream.endText();
	}
	
	private static int getFontSizeThatFits(int max, PDFont font, String text, float maxWidth) throws IOException {
		int size = max;
		
		float textWidth;
		do {
			textWidth = font.getStringWidth(text) / 1000 * size;
			size -= 1;
		}
		while(textWidth >= maxWidth);
		
		return size + 1;
	}
	
	private static BitMatrix createQRCode(String data) throws WriterException {
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix matrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200, hintMap);
		
		return matrix;
	}
	
	private static void drawQRCode(BitMatrix matrix, float size, PDPageContentStream pageContentStream, float x, float y) throws IOException {
		int width = matrix.getWidth();
		float size_per_dot = size / width;
		
		float offset = size / 2;
		
		for(int i=0; i<width; i++) {
			for(int j=0; j<width; j++) {
				if(matrix.get(i, j)) {
					pageContentStream.fillRect(x - offset + (i * size_per_dot), y + offset - (j * size_per_dot), size_per_dot, size_per_dot);
				}
			}
		}
	}
}
