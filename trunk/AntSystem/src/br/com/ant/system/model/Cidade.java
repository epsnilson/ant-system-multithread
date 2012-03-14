package br.com.ant.system.model;

public class Cidade {
		  private int	number;
		  private String nome;

		  public Cidade(int number, String nome) {
					this.nome = nome;
					this.number = number;
		  }

		  public int getNumber() {
					return number;
		  }

		  public void setNumber(int number) {
					this.number = number;
		  }

		  @Override
		  public String toString() {
					return nome;
		  }

}
