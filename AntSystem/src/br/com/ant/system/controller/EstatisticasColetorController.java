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
