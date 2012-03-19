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
package br.com.ant.system.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Formiga implements Cloneable {
	private int				id;

	private Cidade			localizacaoCidadeAtual;
	private Cidade			localizacaoCidadeInicial;
	private Caminho			ultimoCaminho;
	private Set<Cidade>		cidadesVisitadas	= new HashSet<Cidade>();
	private List<Caminho>	trajetoCidades		= new LinkedList<Caminho>();
	private double			distanciaPercorrida;

	private long			tempoInicial;
	private long			tempoFinal;

	public Formiga(int number, Cidade localizacaoAtual) {
		if (localizacaoAtual == null) {
			throw new RuntimeException("Localizacao atual nao pode ser nula");
		}

		this.cidadesVisitadas.add(localizacaoAtual);
		this.localizacaoCidadeAtual = localizacaoAtual;
		this.localizacaoCidadeInicial = localizacaoAtual;
		this.id = number;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Cidade getLocalizacaoCidadeAtual() {
		return localizacaoCidadeAtual;
	}

	public void addCaminho(Caminho caminho) {
		this.ultimoCaminho = caminho;
		this.cidadesVisitadas.add(caminho.getCidadeDestino());
		this.trajetoCidades.add(caminho);
		this.distanciaPercorrida += caminho.getDistancia();

		this.localizacaoCidadeAtual = caminho.getCidadeDestino();
	}

	public List<Caminho> getTrajetoCidades() {
		return trajetoCidades;
	}

	public void clear(Cidade localizacaoAtual) {
		this.trajetoCidades.clear();
		this.cidadesVisitadas.clear();
		this.distanciaPercorrida = 0.0;
		this.tempoInicial = 0;
		this.tempoFinal = 0;

		this.localizacaoCidadeAtual = localizacaoAtual;
		this.localizacaoCidadeInicial = localizacaoAtual;
	}

	public boolean isCidadeVisitada(Cidade cidade) {
		return cidadesVisitadas.contains(cidade);
	}

	public Set<Cidade> getCidadesVisitadas() {
		return cidadesVisitadas;
	}

	public Cidade getLocalizacaoCidadeInicial() {
		return localizacaoCidadeInicial;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Numero:");
		buffer.append(id);
		buffer.append("\n");
		buffer.append("Trajetos: ");
		buffer.append(Arrays.toString(trajetoCidades.toArray()));
		buffer.append("DistanciaPercorrida: ");
		buffer.append(distanciaPercorrida);
		buffer.append("\n");

		return buffer.toString();
	}

	public double getDistanciaPercorrida() {
		return distanciaPercorrida;
	}

	public long getTempoFinal() {
		return tempoFinal;
	}

	public void setTempoFinal(long tempoFinal) {
		this.tempoFinal = tempoFinal;
	}

	public long getTempoInicial() {
		return tempoInicial;
	}

	public void setTempoInicial(long tempoInicial) {
		this.tempoInicial = tempoInicial;
	}

	public Caminho getUltimoCaminho() {
		return ultimoCaminho;
	}
}
