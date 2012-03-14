package br.com.ant.system.model;

public class Caminho {
		  private Cidade	cidadeOrigem;
		  private Cidade	cidadeDestino;
		  private Feromonio feromonio;
		  private float	 distancia;

		  public Caminho(Cidade cidadeOrigem, Cidade cidadeDestino, float distancia) {
					this.cidadeOrigem = cidadeOrigem;
					this.cidadeDestino = cidadeDestino;

					this.distancia = distancia;
					this.feromonio = new Feromonio();
		  }

		  public void setDistancia(float distancia) {
					this.distancia = distancia;
		  }

		  public float getDistancia() {
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
}
