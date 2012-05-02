package org.jgdk.ui.util;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentHelper {
	public static void setText(final Document document, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					document.remove(0, document.getLength());
					document.insertString(0, text, null);
				} catch (BadLocationException e) {
				}
			}
		});
	}
	
	public static String getText(Document document) {
		String text = null;
		try {
			text = document.getText(0, document.getLength());
		} catch (BadLocationException e) {
		}
		return text;
	}
}
