package br.com.ant.system.model;

import java.util.HashSet;
import java.util.Set;

public class Cidade {
		  private int		  number;
		  private Set<Integer> visitado = new HashSet<Integer>();

		  public Cidade(int number) {
					this.number = number;
		  }

		  public int getNumber() {
					return number;
		  }

		  public void setNumber(int number) {
					this.number = number;
		  }

		  public boolean isVisitado(Formiga formiga) {
					return visitado.contains(formiga.getNumber());
		  }

		  public void setVisitado(Formiga formiga) {
					this.visitado.add(formiga.getNumber());
		  }

}
