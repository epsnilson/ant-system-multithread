package br.com.ant.system.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ant.system.model.Caminho;

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

			mapProbabilidade.put(c, probabilidadeCaminho);
			somaProbabilidades += probabilidadeCaminho;
		}

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

			if (melhorProbabilidade == 0 || probabilidade > melhorProbabilidade) {
				melhorProbabilidade = probabilidade;
				melhorCaminho = c;
			}

		}

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
		double quantidadeFeromonioDepositada = quantidadeFeromonioIncrementado / distanciaPercurso;

		double novaQntFeromonio = this.evaporacaoFeromonio(qntFeromonioAtual) + quantidadeFeromonioDepositada;

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
