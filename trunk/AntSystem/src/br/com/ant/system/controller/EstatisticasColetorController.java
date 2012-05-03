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
package br.com.ant.system.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EstatisticasColetorController {
	private static Set<EstatisticaColetor>			mapEstatisticas	= new HashSet<EstatisticaColetor>();
	private static EstatisticaColetor				estatisticasColetor;

	private static EstatisticasColetorController	instance;

	public static EstatisticasColetorController getInstance() {
		if (instance == null) {
			instance = new EstatisticasColetorController();
		}

		return instance;
	}

	private EstatisticasColetorController() {
	}

	public static EstatisticaColetor getEstatisticaColetor() {
		return estatisticasColetor;
	}

	public static synchronized EstatisticaColetor newEstatisticaColetorInstance(int id) {
		estatisticasColetor = new EstatisticaColetor(id);
		mapEstatisticas.add(estatisticasColetor);

		return estatisticasColetor;
	}

	public static void clear() {
		mapEstatisticas.clear();
	}

	public static long getTempoTotal() {
		long tempo = 0;
		for (Iterator<EstatisticaColetor> it = mapEstatisticas.iterator(); it.hasNext();) {
			EstatisticaColetor e = (EstatisticaColetor) it.next();
			tempo += e.getTempoExecucao();
		}

		return tempo;
	}

	public static long getTempoMedio() {
		long tempo = 0;
		for (Iterator<EstatisticaColetor> it = mapEstatisticas.iterator(); it.hasNext();) {
			EstatisticaColetor e = (EstatisticaColetor) it.next();
			tempo += e.getTempoExecucao();
		}

		tempo = tempo / mapEstatisticas.size();

		return tempo;
	}

	public static Set<EstatisticaColetor> getMapEstatisticas() {
		return mapEstatisticas;
	}
}
