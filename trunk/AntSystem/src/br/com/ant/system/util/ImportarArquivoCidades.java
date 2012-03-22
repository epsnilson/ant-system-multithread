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
package br.com.ant.system.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;

/**
 * Classe para importar arquivos de cidades.
 * 
 * @author j.duarte
 * 
 */
public class ImportarArquivoCidades {
	private String[][]	matrizCidades;

	public Set<Caminho> importarAquivo(String filePath) {
		File file = new File(filePath);

		if (!file.exists() || file.length() == 0) {
			throw new RuntimeException("Arquivo invalido ou vazio.");
		}

		try {
			// Recupera as linhas do arquivo.
			List<String[]> linhas = this.getListLinhas(file);

			// Preenche a matriz com as linhas retornadas.
			this.preencherMatriz(linhas);

			Set<Caminho> caminhos = this.getCaminhos();

			return caminhos;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Arquivo nao encotrado.");
		} catch (IOException e) {
			throw new RuntimeException("Erro ao ler o arquivo");
		}

	}

	private Set<Caminho> getCaminhos() {
		Set<String> cidadesIncluidas = new HashSet<String>();
		Set<Caminho> caminhos = new HashSet<Caminho>();
		for (int i = 1; i < matrizCidades.length; i++) {
			String[] linhaArray = matrizCidades[i];
			if (linhaArray.length != matrizCidades.length) {
				throw new RuntimeException("Numero de colunas diferente de numeros de linhas");
			}
			for (int j = 1; j < linhaArray.length; j++) {
				String distanciaStr = matrizCidades[i][j];
				if (StringUtils.isNotEmpty(distanciaStr)) {

					String cidadeOrigem = matrizCidades[i][0];
					String cidadeDestino = matrizCidades[0][j];
					String distancia = matrizCidades[i][j];

					System.out.println("Iteracao " + i + " cida " + cidadeOrigem);
					if (cidadesIncluidas.contains(cidadeDestino)) {
						continue;
					}

					Caminho caminho = new Caminho(new Cidade(cidadeOrigem), new Cidade(cidadeDestino), Double.parseDouble(distancia));

					cidadesIncluidas.add(cidadeOrigem);

					caminhos.add(caminho);
				}
			}
		}
		return caminhos;
	}

	private void preencherMatriz(List<String[]> linhas) {
		String[] linhaArray;
		for (int i = 0; i < linhas.size(); i++) {
			linhaArray = linhas.get(i);
			if (i == 0) {
				matrizCidades = new String[linhas.size()][linhaArray.length];
			}

			for (int j = 0; j < linhaArray.length; j++) {
				matrizCidades[i][j] = linhaArray[j];
			}
		}
	}

	private List<String[]> getListLinhas(File file) throws FileNotFoundException, IOException {
		FileReader fileReader;
		List<String[]> linhas = new ArrayList<String[]>();
		fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while (bufferedReader.ready()) {
			linhas.add(bufferedReader.readLine().split(";"));
		}
		return linhas;
	}

}
