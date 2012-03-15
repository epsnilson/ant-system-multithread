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
package br.com.ant.system.algoritmo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.util.NumberUtil;

/**
 * Classe responsavel por efetuar os calculos de acordo com o especificado pelo algoritmo AS (Ant
 * System)
 * 
 * @author Jackson Sildu
 * 
 */
public class ASAlgoritmo {

	/*
	 * Quantidade de feromonio incrementado por uma formiga. Dorico recomenda valores entre 0 e 1.
	 */
	private double	quantidadeFeromonioIncrementado	= 1.0;
	/*
	 * Dorigo recomenda que a taxa de evaporação com 0.5
	 */
	private double	taxaEvaporacaoFeromonio			= 0.5;
	/*
	 * Dorigo recomenda que a importancia do feromonio fique em torno de 1.
	 */
	private double	pesoFeromonio					= 1;
	/*
	 * Dorigo recomenda que a importancia da visibilidade do cidade fique em torno de 5.
	 */
	private double	pesoVisibilidade				= 5;

	private Logger	logger							= Logger.getLogger(this.getClass());

	/**
	 * Escolhe o Melhor caminho apartir dos caminhos disponiveis e do algoritmo proposto pelo
	 * algoritmo padrão do AS(Ant System)
	 * 
	 * @param caminhosDisponiveis
	 * @return
	 */
	public Caminho escolherCaminho(List<Caminho> caminhosDisponiveis) {
		Map<Caminho, Double> mapProbabilidade = new HashMap<Caminho, Double>();
		double somaProbabilidades = 0;

		for (Caminho c : caminhosDisponiveis) {
			double probabilidadeCaminho = this.calcularProbabilidadeCaminho(c);
			logger.debug("Custo caminho " + c.getCidadeOrigem().getNome() + " ate " + c.getCidadeDestino().getNome() + " ==> " + NumberUtil.getInstance().doubleToString(probabilidadeCaminho));

			mapProbabilidade.put(c, probabilidadeCaminho);
			somaProbabilidades += probabilidadeCaminho;
		}

		logger.debug("Soma de custos: " + NumberUtil.getInstance().doubleToString(somaProbabilidades));

		return buscarMelhorCaminho(mapProbabilidade, somaProbabilidades);

	}

	/**
	 * Busca o melhor caminho apartir de suas probabilidades
	 * 
	 * @param mapProbabilidade
	 *            Mapa Contendo as probilidades de cada caminho.
	 * @param somaProbabilidades
	 *            Soma de probabilidades de todos os caminhos
	 * @return Retorna o caminho escolhido.
	 */
	private Caminho buscarMelhorCaminho(Map<Caminho, Double> mapProbabilidade, double somaProbabilidades) {
		double melhorProbabilidade = 0;
		Caminho melhorCaminho = null;
		for (Caminho c : mapProbabilidade.keySet()) {
			double probabilidade = mapProbabilidade.get(c) / somaProbabilidades;
			logger.debug("Probabilidade " + c.getCidadeOrigem().getNome() + " ate " + c.getCidadeDestino().getNome() + " ==> " + NumberUtil.getInstance().doubleToString(probabilidade));

			if (melhorProbabilidade == 0 || probabilidade > melhorProbabilidade) {
				melhorProbabilidade = probabilidade;
				melhorCaminho = c;
			}
		}

		logger.info("Caminho escolhido: " + melhorCaminho.toString());

		return melhorCaminho;
	}

	/**
	 * Calcula a probabilidade de um caminho.
	 * 
	 * @param c
	 *            Caminho a ser calculado.
	 * @return Retorna o valor da probabilidade de escolha do caminho.
	 */
	private double calcularProbabilidadeCaminho(Caminho c) {
		double visibilidadeCidade = 1 / c.getDistancia();
		double probabilidadeCaminho = (Math.pow(c.getFeromonio().getQntFeromonio(), pesoFeromonio) * Math.pow(visibilidadeCidade, pesoVisibilidade));

		return probabilidadeCaminho;
	}

	/**
	 * Efetua o calculo de evaporação do feromonio
	 * 
	 * @param qntFeromonioAtual
	 *            Quantidade atual de feromonio presente no caminho
	 * @return
	 */
	private double evaporacaoFeromonio(double qntFeromonioAtual) {
		double novaQntFeromonio = (1 - taxaEvaporacaoFeromonio) * qntFeromonioAtual;
		logger.debug("Quantidade feromonio apos evaporacao: " + novaQntFeromonio);

		return novaQntFeromonio;
	}

	/**
	 * Atualiza a quantidade de feromonio do caminho.
	 * 
	 * @param qntFeromonioAtual
	 *            Quantidade de feromonio atual,
	 * @param distanciaPercurso
	 *            Distancia total da viagem percorrida pela formiga.
	 * @return Retorna a quantidade do novo feromonio
	 */
	public double atualizarFeromonio(double qntFeromonioAtual, double distanciaPercurso) {
		logger.debug("Atualizando quantidade de feromonio...");
		logger.debug("Quanidade de Feromonio atual: " + NumberUtil.getInstance().doubleToString(qntFeromonioAtual));
		logger.debug("Distancia percorrida pela formiga: " + NumberUtil.getInstance().doubleToString(distanciaPercurso));

		double quantidadeFeromonioDepositada = quantidadeFeromonioIncrementado / distanciaPercurso;

		double novaQntFeromonio = this.evaporacaoFeromonio(qntFeromonioAtual) + quantidadeFeromonioDepositada;

		logger.debug("Nova quantidade de feromonio gerado: " + NumberUtil.getInstance().doubleToString(novaQntFeromonio));

		return novaQntFeromonio;
	}

	public double getPesoFeromonio() {
		return pesoFeromonio;
	}

	/**
	 * Informa a importacia da visibilidade entre uma cidadeOrigem e a cidadeDestino
	 * 
	 * @param pesoFeromonio
	 */
	public void setPesoFeromonio(double pesoFeromonio) {
		this.pesoFeromonio = pesoFeromonio;
	}

	public double getPesoVisibilidade() {
		return pesoVisibilidade;
	}

	/**
	 * Informa a importacia da visibilidade entre uma cidadeOrigem e a cidadeDestino.
	 * 
	 * @param pesoVisibilidade
	 */
	public void setPesoVisibilidade(double pesoVisibilidade) {
		this.pesoVisibilidade = pesoVisibilidade;
	}

	/**
	 * Inicializa a quantidade de feromonio no caminho.
	 * 
	 * @param caminhos
	 *            Colecao com todos os caminhos.
	 * @param qntTotalCidades
	 *            Quantidade total de cidaes.
	 */
	public void inicializarFeromonio(List<Caminho> caminhos, int qntTotalCidades) {
		for (Caminho c : caminhos) {
			double feromonioInicial = 1 / (qntTotalCidades * c.getDistancia());
			c.getFeromonio().setQntFeromonio(feromonioInicial);
		}
	}

}
