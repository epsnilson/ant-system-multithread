package br.com.ant.system.util;

public class AntSystemUtil {

		  private static AntSystemUtil instance;

		  public static synchronized AntSystemUtil getIntance() {
					if (instance == null) {
							  instance = new AntSystemUtil();
					}

					return instance;
		  }

		  private AntSystemUtil() {
		  }

		  public int getAleatorio(int min, int max) {
					int aleatorio = min + (int) ((max - min) * Math.random());
					return aleatorio;
		  }
}
