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
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.notificacao.Notificacao;
import br.com.ant.system.notificacao.Notificacao.NotificacaoEnum;
import br.com.ant.system.notificacao.NotificationController;
import br.com.ant.system.util.AntSystemUtil;

/**
 * Implementação da logica de transição das formigas no percurso.
 * 
 * @author Jackson Sildu
 * 
 */
public class FormigaController {

	private Formiga				formiga;
	private PercursoController	percursoController;
	private ASAlgoritmo			algoritmo;

	public FormigaController(Formiga formiga, PercursoController percurso, ASAlgoritmo algoritmo) {
		this.formiga = formiga;
		this.percursoController = percurso;
		this.algoritmo = algoritmo;
	}

	/**
	 * Escolhe o caminho de acordo com as alternativas disponiveis
	 * 
	 * @param todasAlternativas
	 *            Caminhos que poderão ser tomados.
	 * @return
	 */
	public Caminho escolherPercurso() {

		// Recupera as alternativas para o trajeto de cada formiga
		List<Caminho> todasAlternativas = percursoController.getAlternativas(formiga.getLocalizacaoCidadeAtual());

		// Verifica se todas as cidades ja foram visitadas.
		formiga.setTodasVisitadas(percursoController.isTodasCidadesPercorrida(formiga));

		// Escolhendo o caminho destino
		Caminho caminhoEscolhido = algoritmo.escolherCaminho(formiga, todasAlternativas);

		// atualiza a localização atual da formiga e o estado da cidade.
		formiga.addCaminho(caminhoEscolhido);

		// Enviando mensagem de notificacao
		Notificacao notificaoFormiga = new Notificacao();
		notificaoFormiga.setObj(formiga);
		notificaoFormiga.setTipoNotificacao(NotificacaoEnum.FORMIGA);
		NotificationController.getInstance().addNotificacao(notificaoFormiga);

		return caminhoEscolhido;
	}

	/**
	 * Limpa os dados da formiga.
	 * 
	 * @param formiga
	 */
	public void clearFormiga() {
		AntSystemUtil.getIntance().logar("Limpando as informações da formiga");

		Cidade localizacaoAtual = percursoController.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(0, percursoController.getCidadesPercurso().size() - 1));
		// Cidade localizacaoAtual = formiga.getLocalizacaoCidadeInicial();
		AntSystemUtil.getIntance().logar("Nova Localizacao Inicial: " + localizacaoAtual.getNome());

		formiga.clear(localizacaoAtual);
	}

	public Formiga getFormiga() {
		return formiga;
	}

	/**
	 * Adiciona feromonio no trajeto percorrido pela formiga.
	 * 
	 * @param formiga
	 *            Formiga que percorreu o caminho
	 */
	public synchronized void adicionarFeromonioTrajeto() {
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
