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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Estatistica;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.NumberUtil;

public class EstatisticasControler {

		  private List<Caminho>				melhorCaminho = new LinkedList<Caminho>();
		  private double					   menorCaminhoPercorrido;
		  private Long						 menorTempo;

		  private List<Estatistica>			estatisticas  = new ArrayList<Estatistica>();

		  private static EstatisticasControler instance;

		  public static EstatisticasControler getInstance() {
					if (instance == null) {
							  instance = new EstatisticasControler();
					}

					return instance;
		  }

		  private EstatisticasControler() {
		  }

		  public void coletarEstatisticas(Formiga formiga) {
					this.addEstatistica(formiga);

					if (menorCaminhoPercorrido == 0 || formiga.getDistanciaPercorrida() < menorCaminhoPercorrido) {
							  menorCaminhoPercorrido = formiga.getDistanciaPercorrida();

							  // Copiando a o caminho percorrido para o melhor caminho.
							  melhorCaminho.addAll(formiga.getTrajetoCidades());

							  // Setando o menor tempo.
							  menorTempo = (formiga.getTempoFinal() - formiga.getTempoInicial());
					}
		  }

		  private void addEstatistica(Formiga formiga) {
					Estatistica estatistica = new Estatistica();
					estatistica.setFormigaId(formiga.getId());
					estatistica.setDistanciaPercorrida(formiga.getDistanciaPercorrida());
					estatistica.setTempoGasto(formiga.getTempoFinal() - formiga.getTempoInicial());

					estatistica.getCaminhoPercorrido().addAll(formiga.getTrajetoCidades());
					estatisticas.add(estatistica);
		  }

		  public List<Caminho> getMelhorCaminho() {
					return melhorCaminho;
		  }

		  public double getMenorCaminhoPercorrido() {
					return menorCaminhoPercorrido;
		  }

		  public Long getMenorTempo() {
					return menorTempo;
		  }

		  public List<Estatistica> getEstatisticas() {
					return estatisticas;
		  }

		  public String printEstatisticas() {
					StringBuffer buffer = new StringBuffer();
					buffer.append("\n");
					buffer.append("***********************************************************************************************\n");
					buffer.append("**************************************Estatisticas*******************************************\n");
					buffer.append("***********************************************************************************************\n");
					buffer.append("Menor Tempo: ");
					buffer.append(menorTempo);
					buffer.append(" ms");
					buffer.append("\n");
					buffer.append("Menor caminho: ");
					buffer.append(NumberUtil.getInstance().doubleToString(menorCaminhoPercorrido));
					buffer.append("\n");
					buffer.append("Melhor trajeto: ");
					buffer.append(Arrays.toString(melhorCaminho.toArray()));
					buffer.append("\n");
					buffer.append("***********************************************************************************************\n");
					for (Estatistica e : estatisticas) {
							  buffer.append("FormigaID: ");
							  buffer.append(e.getFormigaId());
							  buffer.append("\n");
							  buffer.append("Tempo Gasto: ");
							  buffer.append(e.getTempoGasto());
							  buffer.append("ms");
							  buffer.append("\n");
							  buffer.append("Distancia Percorrida: ");
							  buffer.append(NumberUtil.getInstance().doubleToString(e.getDistanciaPercorrida()));
							  buffer.append("\n");
							  buffer.append("Trajeto: ");
							  buffer.append(Arrays.toString(e.getCaminhoPercorrido().toArray()));
							  buffer.append("\n");
							  buffer.append("***********************************************************************************************\n");
					}

					return buffer.toString();
		  }

}
