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
package br.com.ant.system.util;

/**
 * Classe utilitaria geral do projeto.
 * 
 * @author j.duarte
 * 
 */
public class AntSystemUtil {

	private static AntSystemUtil	instance;

	public static synchronized AntSystemUtil getIntance() {
		if (instance == null) {
			instance = new AntSystemUtil();
		}

		return instance;
	}

	private AntSystemUtil() {
	}

	/**
	 * Recupera um numero aleatorio entre o range informado (min/max).
	 * 
	 * @param min
	 *            Valor minimo que pode ser retornado
	 * @param max
	 *            Valor maximo que poderá ser retornado.
	 * @return
	 */
	public int getAleatorio(int min, int max) {
		int aleatorio = min + (int) ((max - min) * Math.random());
		return aleatorio;
	}
}
