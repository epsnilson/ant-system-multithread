/**
 *  This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
