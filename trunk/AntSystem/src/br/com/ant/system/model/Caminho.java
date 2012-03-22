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

public class Caminho {
	private Cidade		cidadeOrigem;
	private Cidade		cidadeDestino;
	private Feromonio	feromonio;
	private double		distancia;

	public Caminho(Cidade cidadeOrigem, Cidade cidadeDestino, double distancia) {
		this.cidadeOrigem = cidadeOrigem;
		this.cidadeDestino = cidadeDestino;

		this.distancia = distancia;
		this.feromonio = new Feromonio();
	}

	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}

	public double getDistancia() {
		return distancia;
	}

	public Feromonio getFeromonio() {
		return feromonio;
	}

	public Cidade getCidadeOrigem() {
		return cidadeOrigem;
	}

	public void setCidadeOrigem(Cidade cidadeOrigem) {
		this.cidadeOrigem = cidadeOrigem;
	}

	public Cidade getCidadeDestino() {
		return cidadeDestino;
	}

	public void setCidadeDestino(Cidade cidadeDestino) {
		this.cidadeDestino = cidadeDestino;
	}

	public void setFeromonio(Feromonio feromonio) {
		this.feromonio = feromonio;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append("cidadeOrigem: " + cidadeOrigem);
		buffer.append(", cidadeDestino: " + cidadeDestino);
		buffer.append(", distancia: " + distancia);
		buffer.append("]");

		return buffer.toString();
	}

	@Override
	public boolean equals(Object obj) {
		Caminho c = (Caminho) obj;
		if (c.getCidadeDestino().getNome().equals(cidadeDestino.getNome()) && c.getCidadeOrigem().getNome().equals(cidadeOrigem.getNome())) {
			return true;
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		return getCidadeDestino().getNome().hashCode() ^ getCidadeOrigem().getNome().hashCode();
	}
}
