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

import java.text.DecimalFormat;

/**
 * Classe utilitaria para manipulacao de valores numericos
 * 
 * @author Jackson Sildu
 * 
 */
public class NumberUtil {

	// Tolerancia para testes utilizando Double, indica a precisao do calculo
	private static final double	TOLERANCIA_IGUALDADE_DOUBLE	= 0.009;
	private static NumberUtil	INSTANCE;

	private DecimalFormat		df;

	private NumberUtil() {
		df = new DecimalFormat();
		df.setMaximumFractionDigits(10);
		df.setMinimumFractionDigits(10);

	}

	/**
	 * Recupera uma unica instancia de NumberUtil.
	 * 
	 * @return Instancia unica de NumberUtil
	 */
	public static NumberUtil getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NumberUtil();
		}
		return INSTANCE;
	}

	/**
	 * Verifica se dois Double sao iguais, considerando uma tolerancia especificada
	 * 
	 * @param d1
	 * @param d2
	 * @return Indicativo de igualdade
	 */
	public boolean equalsDouble(Double d1, Double d2) {
		return (Math.abs(d1 - d2) < TOLERANCIA_IGUALDADE_DOUBLE);
	}

	public String doubleToString(double d) {
		return df.format(d);
	}
}
