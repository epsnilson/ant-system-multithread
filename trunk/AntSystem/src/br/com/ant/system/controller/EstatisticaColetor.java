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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Estatistica;
import br.com.ant.system.model.Formiga;

/**
 * Classe responsavel pela coletas de estaticas da execucao do algoritmo.
 * 
 * @author j.duarte
 * 
 */
public class EstatisticaColetor {

	private List<Caminho>		melhorCaminho	= new LinkedList<Caminho>();
	int							id;
	private double				menorCaminhoPercorrido;
	private long				tempoGastoMelhorCaminho;

	private int					numeroIteracoes;
	private int					qntSolucoesEncotradas;

	private long				horarioInicial;
	private long				horarioFinal;

	private List<Estatistica>	estatisticas	= new ArrayList<Estatistica>();
	Logger						logger			= Logger.getLogger(this.getClass());

	public EstatisticaColetor(int id) {
		this.id = id;
	}

	public synchronized void coletarEstatisticas(Formiga formiga, int iteracao) {
		// this.addEstatistica(formiga, iteracao);
		qntSolucoesEncotradas++;

		if (menorCaminhoPercorrido == 0 || formiga.getDistanciaPercorrida() < menorCaminhoPercorrido) {
			menorCaminhoPercorrido = formiga.getDistanciaPercorrida();

			// Copiando a o caminho percorrido para o melhor caminho.
			melhorCaminho.clear();
			melhorCaminho.addAll(formiga.getTrajetoCidades());

			// Setando o menor tempo.
			tempoGastoMelhorCaminho = (formiga.getTempoFinal() - formiga.getTempoInicial());
		}
	}

	private void addEstatistica(Formiga formiga, int iteracao) {
		Estatistica estatistica = new Estatistica();

		estatistica.setIteracao(iteracao);
		estatistica.setFormigaId(formiga.getId());
		estatistica.setDistanciaPercorrida(formiga.getDistanciaPercorrida());
		estatistica.setTempoGasto(formiga.getTempoFinal() - formiga.getTempoInicial());
		estatistica.setCidadeInicial(formiga.getLocalizacaoCidadeInicial());

		estatistica.getCaminhoPercorrido().addAll(formiga.getTrajetoCidades());
		estatisticas.add(estatistica);
	}

	public long getTempoExecucao() {
		return horarioFinal - horarioInicial;
	}

	public List<Caminho> getMelhorCaminho() {
		return melhorCaminho;
	}

	public double getMenorCaminhoPercorrido() {
		return menorCaminhoPercorrido;
	}

	public Long getTempoGastoMelhorCaminho() {
		return tempoGastoMelhorCaminho;
	}

	public List<Estatistica> getEstatisticas() {
		return estatisticas;
	}

	public void setHorarioFinal(long horarioFinal) {
		this.horarioFinal = horarioFinal;
	}

	public void setHorarioInicial(long horarioInicial) {
		this.horarioInicial = horarioInicial;
	}

	public long getHorarioFinal() {
		return horarioFinal;
	}

	public long getHorarioInicial() {
		return horarioInicial;
	}

	public void setNumeroIteracoes(int numeroIteracoes) {
		this.numeroIteracoes = numeroIteracoes;
	}

	public int getNumeroIteracoes() {
		return numeroIteracoes;
	}

	public int getQntSolucoesEncotradas() {
		return qntSolucoesEncotradas;
	}

	public synchronized void loggerEstatisticas(boolean multiThread) {
		BufferedOutputStream outputStream = gerarArquivoEstatistica(multiThread);
		print("***********************************************************************************************", outputStream);
		print("**************************************Estatisticas*******************************************", outputStream);
		print("***********************************************************************************************", outputStream);
		print(String.format("Tempo gasto na execucao do algoritmo: %s",
				new SimpleDateFormat("mm:ss:SSS").format(new Date(EstatisticasColetorController.getEstatisticaColetor().getHorarioFinal() - EstatisticasColetorController.getEstatisticaColetor().getHorarioInicial()))), outputStream);
		print(String.format("Quantidade de Iteracoes: %s", numeroIteracoes), outputStream);
		print(String.format("Menor caminho: %s", menorCaminhoPercorrido), outputStream);
		print("", outputStream);
		print("Melhor trajeto: ", outputStream);
		print("", outputStream);
		for (Caminho c : melhorCaminho) {
			print(String.format("%s ====== %s =====> %s", c.getCidadeOrigem(), c.getDistancia(), c.getCidadeDestino()), outputStream);
		}
		print("", outputStream);
		print(String.format("Tempo Gasto no melhor caminho: %s ms", tempoGastoMelhorCaminho), outputStream);
		print(String.format("Quantidade de solucoes encontradas: %s", estatisticas.size()), outputStream);
		print("***********************************************************************************************", outputStream);
		for (Estatistica e : estatisticas) {
			print("", outputStream);
			print(String.format("FormigaID: %s", e.getFormigaId()), outputStream);
			print(String.format("Cidade Inicial: %s", e.getCidadeInicial()), outputStream);
			print(String.format("Tempo Gasto: %s ms", e.getTempoGasto()), outputStream);
			print(String.format("Distancia Percorrida: %s ", e.getDistanciaPercorrida()), outputStream);
			print("", outputStream);
			print("Trajeto: ", outputStream);
			print("", outputStream);
			for (Caminho c : e.getCaminhoPercorrido()) {
				print(String.format("%s ====== %s =====> %s", c.getCidadeOrigem(), c.getDistancia(), c.getCidadeDestino()), outputStream);
			}
			print("***********************************************************************************************", outputStream);
		}

		try {
			outputStream.close();
		} catch (IOException e1) {
		}

		outputStream = null;

	}

	public BufferedOutputStream gerarArquivoEstatistica(boolean multiThread) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String path = multiThread ? "EstatisticasMultithread" : "EstatisticasMonotrhread";
		path += sdf.format(new Date()) + ".txt";

		File file = new File(path);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);

			return outputStream;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("O arquivo nao pode ser encontrado.", e);
		}
	}

	public void print(String text, BufferedOutputStream outputStream) {
		try {
			outputStream.write((text + "\n").getBytes());
		} catch (IOException e) {
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setEstatisticas(List<Estatistica> estatisticas) {
		this.estatisticas = estatisticas;
	}
}
