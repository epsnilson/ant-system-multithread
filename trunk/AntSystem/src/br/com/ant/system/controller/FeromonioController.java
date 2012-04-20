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
import java.util.List;
import java.util.Set;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.notificacao.Notificacao;
import br.com.ant.system.notificacao.Notificacao.NotificacaoEnum;
import br.com.ant.system.notificacao.NotificationController;

/**
 * Classe responsaveis pelas acoes de cada formiga.
 * 
 * @author j.duarte
 * 
 */
public class FeromonioController {

	private ASAlgoritmo			algoritmo;
	private PercursoController	percursoController;

	public FeromonioController(ASAlgoritmo algoritmo, PercursoController percursoController) {
		this.algoritmo = algoritmo;
		this.percursoController = percursoController;
	}

	/**
	 * Adiciona feromonio no trajeto percorrido pela formiga.
	 * 
	 * @param formiga
	 *            Formiga que percorreu o caminho
	 */
	public synchronized void adicionarFeromonioTrajeto(Formiga formiga) {
		// Recupera o trajeto efetuado pela formiga
		List<Caminho> trajetosFormigas = formiga.getTrajetoCidades();
		Set<Caminho> caminhosAtualizados = new HashSet<Caminho>();

		for (Caminho c : trajetosFormigas) {

			if (caminhosAtualizados.contains(c)) {
				continue;
			} else {
				caminhosAtualizados.add(c);
			}

			// Recupera a nova quantidade de feromonio atualizado.
			double novaQntFeromonio = algoritmo.atualizarFeromonio(c.getFeromonio().getQntFeromonio(), formiga.getDistanciaPercorrida());
			c.getFeromonio().setQntFeromonio(novaQntFeromonio);

			Notificacao notificacao = new Notificacao();
			notificacao.setTipoNotificacao(NotificacaoEnum.FEROMONIO);
			notificacao.setObj(c);

			NotificationController.getInstance().addNotificacao(notificacao);

			// Setando nova quantidade de feromonio no caminho inverso.
			List<Caminho> caminhos = percursoController.getAlternativas(c.getCidadeDestino());
			for (Caminho caminhoInverso : caminhos) {
				if (caminhoInverso.getCidadeDestino().equals(c.getCidadeOrigem())) {
					caminhoInverso.getFeromonio().setQntFeromonio(novaQntFeromonio);
					Notificacao notificacaoInverso = new Notificacao();
					notificacaoInverso.setTipoNotificacao(NotificacaoEnum.FEROMONIO);
					notificacaoInverso.setObj(caminhoInverso);

					NotificationController.getInstance().addNotificacao(notificacaoInverso);
					break;
				}
			}
		}
	}

}
