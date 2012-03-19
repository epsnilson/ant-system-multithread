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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
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

		if (todasAlternativas.size() != 1) {
			// TODO: Verificar este procedimento.
			Caminho caminhoInverso = formiga.getUltimoCaminho();
			todasAlternativas.remove(caminhoInverso);
		}

		// Verifica quais caminhos nao foram visitados.
		List<Caminho> caminhosDisponiveis = new ArrayList<Caminho>();
		for (Caminho c : todasAlternativas) {
			if (!formiga.isCidadeVisitada(c.getCidadeDestino())) {
				caminhosDisponiveis.add(c);
			}
		}

		Caminho caminhoEscolhido;
		/*
		 * Ira escolher um caminho de uma cidade ainda nao visitada, ou sera escolhida uma cidade ja
		 * visitada se caso já tiver visitado todas as cidades.
		 */
		if (!caminhosDisponiveis.isEmpty()) {
			caminhoEscolhido = algoritmo.escolherCaminho(caminhosDisponiveis);
		} else {
			caminhoEscolhido = algoritmo.escolherCaminho(todasAlternativas);
		}

		// atualiza a localização atual da formiga e o estado da cidade.
		formiga.addCaminho(caminhoEscolhido);

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
