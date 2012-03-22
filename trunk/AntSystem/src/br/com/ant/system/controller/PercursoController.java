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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;

/**
 * Classe responsavel a controlar o ambiente do percurso
 * 
 * @author j.duarte
 * 
 */
public class PercursoController {

	private List<Cidade>				cidadesPercurso		= new ArrayList<Cidade>();
	private List<Caminho>				caminhosDisponiveis	= new ArrayList<Caminho>();
	private Map<Cidade, List<Caminho>>	mapPercurso			= new HashMap<Cidade, List<Caminho>>();

	/**
	 * Adiciona um novo caminho ao percurso.
	 * 
	 * @param cidadeOrigem
	 *            Cidade de origem do caminho.
	 * @param cidadeDestino
	 *            Cidade de destino da formiga.
	 * @param distancia
	 *            Distancia (custo) do percurso entre as cidades (origem e destino).
	 */
	public void addCaminho(Caminho caminho) {
		Caminho caminhoInverso = new Caminho(caminho.getCidadeDestino(), caminho.getCidadeOrigem(), caminho.getDistancia());

		this.caminhosDisponiveis.add(caminho);

		// Adiciona o caminho inverso. (Matriz identidade)
		this.caminhosDisponiveis.add(caminhoInverso);

		if (!cidadesPercurso.contains(caminho.getCidadeOrigem())) {
			cidadesPercurso.add(caminho.getCidadeOrigem());
		}

		if (!cidadesPercurso.contains(caminho.getCidadeDestino())) {
			cidadesPercurso.add(caminho.getCidadeDestino());
		}

		this.addtoMapPercurso(caminho.getCidadeOrigem(), caminho);
		this.addtoMapPercurso(caminho.getCidadeDestino(), caminhoInverso);
	}

	/**
	 * Adiciona o caminho ao mapa de caminhos.
	 * 
	 * @param cidadeOrigem
	 * @param caminho
	 */
	private void addtoMapPercurso(Cidade cidadeOrigem, Caminho caminho) {
		List<Caminho> caminhos;
		if (mapPercurso.containsKey(cidadeOrigem)) {
			caminhos = mapPercurso.get(cidadeOrigem);
			caminhos.add(caminho);
		} else {
			caminhos = new ArrayList<Caminho>();
			caminhos.add(caminho);
			mapPercurso.put(cidadeOrigem, caminhos);
		}
	}

	public List<Cidade> getCidadesPercurso() {
		return cidadesPercurso;
	}

	public Map<Cidade, List<Caminho>> getMapPercurso() {
		return mapPercurso;
	}

	public List<Caminho> getAlternativas(Cidade cidade) {
		return mapPercurso.get(cidade);
	}

	/**
	 * Verifica se a formiga percorreu todas as formigas e retornou ao destino.
	 * 
	 * @param formiga
	 * @return
	 */
	public boolean isFinalizouPercurso(Formiga formiga) {
		boolean terminado = false;
		if (isTodasCidadesPercorrida(formiga) && formiga.getLocalizacaoCidadeAtual().equals(formiga.getLocalizacaoCidadeInicial())) {
			terminado = true;
		}

		return terminado;
	}

	/**
	 * Verifica se todas as cidades já forma visitadas.
	 * 
	 * @param formiga
	 * @return
	 */
	public boolean isTodasCidadesPercorrida(Formiga formiga) {
		boolean visitados = false;
		if (cidadesPercurso.size() == formiga.getCidadesVisitadas().size()) {
			visitados = true;
		}

		return visitados;
	}

	public List<Caminho> getCaminhosDisponiveis() {
		return caminhosDisponiveis;
	}

}
