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
	private boolean					logar	= false;

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

	public void logar(String text) {
		if (logar) {
			System.out.println(text);
		}
	}

	public String horaFormatada(long total) {
		String horas = null;

		String hS = null;
		String mS;
		String sS;

		long h = total / (1000 * 60 * 60);
		total -= h * (1000 * 60 * 60);
		long m = total / (1000 * 60);
		total -= m * (1000 * 60);
		long s = total / 1000;
		total -= s * 1000;

		if (h < 10) {
			hS = "0" + h;
		} else {
			hS = String.valueOf(h);
		}

		if (m < 10) {
			mS = "0" + m;
		} else {
			mS = String.valueOf(m);
		}

		if (s < 10) {
			sS = "0" + s;
		} else {
			sS = String.valueOf(s);
		}

		horas = hS + ":" + mS + ":" + sS;

		return horas;
	}
}
