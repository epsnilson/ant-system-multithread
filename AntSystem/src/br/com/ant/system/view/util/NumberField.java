package br.com.ant.system.view.util;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.commons.lang.StringUtils;

public class NumberField extends JTextField {

	private static final long	serialVersionUID	= -3338123483288644474L;

	public NumberField() {
		this.setDocument(new NumberFieldFilter(5));
	}

	public void setLength(int length) {
		this.setDocument(new NumberFieldFilter(length));
	}

	class NumberFieldFilter extends PlainDocument {
		private static final long	serialVersionUID	= 1L;

		private Integer				lenght				= null;

		public NumberFieldFilter(int lenght) {

			this.lenght = lenght;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

			if (lenght == null) {
				if (StringUtils.isNumeric(str.trim())) {
					super.insertString(offs, str, a);
				}
			} else {
				int inputLenght = getLength() + 1;
				int strLenght = str.length();
				if (inputLenght <= lenght && strLenght <= lenght) {
					if (StringUtils.isNumeric(str.trim())) {
						super.insertString(offs, str, a);
					}

					if (inputLenght == lenght) {
						transferFocus();
						return;
					}
				} else {

					if (getText(0, getLength()).length() >= lenght) {
						setText(getText(0, getLength()).substring(0, lenght));
					} else {
						setText(str.substring(0, lenght));
					}
					transferFocus();
					return;
				}
			}
		}
	}

}
