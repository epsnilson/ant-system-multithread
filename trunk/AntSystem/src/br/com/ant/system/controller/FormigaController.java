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

import java.util.List;

import org.apache.log4j.Logger;

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

	private Logger				logger	= Logger.getLogger(this.getClass());

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
		Notificacao notificao = new Notificacao();
		notificao.setObj(caminhoEscolhido);
		notificao.setTipoNotificacao(NotificacaoEnum.CAMINHO);
		NotificationController.getInstance().addNotificacao(notificao);

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
		logger.debug("Limpando as informações da formiga");

		Cidade localizacaoAtual = percursoController.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(0, percursoController.getCidadesPercurso().size() - 1));
		logger.debug("Nova Localizacao Inicial: " + localizacaoAtual.getNome());

		formiga.clear(localizacaoAtual);
	}

	public Formiga getFormiga() {
		return formiga;
	}
}
