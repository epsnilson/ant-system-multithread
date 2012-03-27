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
package br.com.ant.system.app;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.lang.SystemUtils;

import br.com.ant.system.view.ColoniaFormigasView;

import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;

public class AntSystemApp {

	public static void main(String[] args) {

		try {
			if (SystemUtils.IS_OS_WINDOWS) {
				UIManager.setLookAndFeel(WindowsClassicLookAndFeel.class.getName());
			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			
			ColoniaFormigasView frame = new ColoniaFormigasView();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices = env.getScreenDevices();

			DisplayMode mode = devices[0].getDisplayMode();
			int height = mode.getHeight();
			int width = mode.getWidth();

			frame.setSize(width, height - 30);
			frame.setVisible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
