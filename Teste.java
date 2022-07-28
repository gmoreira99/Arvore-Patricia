// 18/07/2021 - Para Laboratório de Algoritmos e Estruturas de Dados II, 2021/1
// Guilherme Moreira de Carvalho & Marcus Vinícius Diniz Ribeiro
// Programa que, dado um texto passado por parâmetro, insere e busca palavras na Árvore Patricia.
// Imprime o resultado da busca na saída padrão e escreve em um arquivo.
// Busca pré-determinada; por meio de um arquivo passado por parâmetro; pelo usuário.

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
// import java.util.InputMismatchException;

public class Teste {

    // Busca pré-determinada
    static String[] ex1 = {"trabalho", "computacao", "governo", "educacao", "tecnologia", "formacao", "desenvolvimento", "que", "informatica", "em", "crise"};
    static String[] ex2 = {"sociedade", "software", "ideia", "pessoa", "Informatica", "etica", "muito", "ciencia", "computacao", "que", "area", "moral"};

    public static void main(String[] args) {
        FileReader in = null;
        Scanner input = null;   // lê as linhas do arquivo
        Scanner _input = null;  // lê as palavras da linha
        String aux = "";    // linha do arquivo
        String _aux = null; // palavra da linha
        String out = "";
        ArvorePatricia ap = new ArvorePatricia(128);

        try {
            in = new FileReader(args[0]);
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            System.out.println("ERRO : NAO FOI POSSIVEL ABRIR O ARQUIVO");
            System.exit(0);
        }

        input = new Scanner(in);
        input.useDelimiter("\n");

        // Inserção na Árvore
        while (input.hasNext()) {
            aux = input.next();
            _input = new Scanner(aux);
            _input.useDelimiter(" +|-|/|\\|\\(|\\)|\\.|\\,|\\?|\\!|\\:|\\;|\\t+\\r+");
            while (_input.hasNext()) {
                _aux = _input.next();
                // linha vazia
                if (_aux.length() <= 0) {
                    continue;
                }
                ap.coluna++;
                // Considera apenas sequências de letras e dígitos
                // Começando por uma letra
                if (!Character.isLetter(_aux.toCharArray()[0])) {
                    continue;
                }
                ap.insere(_aux);
            }
            ap.linha++;
            ap.coluna = 0;
        }

        try {
            in.close();
        } catch (IOException e) {
            System.out.println("ERRO : NAO FOI POSSIVEL FECHAR O ARQUIVO");
        }

        _input.close();
        
        // Busca na Árvore
        int i = 1;
        do {
            try {
                in = new FileReader(args[i]);
                input = new Scanner(in);
                input.useDelimiter("\\n+");

                out += "EXEMPLO " + i + "\n";
                while (input.hasNext()) {
                    aux = input.next();
                    out += aux + ": ";
                    out += ap.pesquisa(aux);
                    out += "\n";
                }
                in.close();
            } catch (IOException e) {
                System.out.println("ERRO : NAO FOI POSSIVEL ABRIR O ARQUIVO");
                System.exit(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                out += "EXEMPLO 1\n";
                for (int j = 0; j < ex1.length; j++) {
                    out += ex1[j] + ": ";
                    out += ap.pesquisa(ex1[j]);
                    out += "\n";
                }
                out += "EXEMPLO 2\n";
                for (int j = 0; j < ex2.length; j++) {
                    out += ex2[j] + ": ";
                    out += ap.pesquisa(ex2[j]);
                    out += "\n";
                }
            }
            i++;
        } while (i < args.length);

        /*input = new Scanner(System.in);
        int op = 0;
        boolean cond = true;
        while (cond) {
            for (int j = 0; j < 35; j ++ ){
                System.out.printf("=");
            }
            System.out.println();
            System.out.println("Selecione a opcao desejada:");
            System.out.println("1. Buscar\n0. Sair");
            System.out.printf("> ");

            try {
                op = input.nextInt();
            } catch (InputMismatchException e) {
                op = 2;
            }

            switch (op) {
                case 1:
                    System.out.printf("Digite a palavra: ");
                    System.out.println(ap.pesquisa(input.next()));
                    break;
                case 0:
                    cond = false;
                    break;
                default:
                    System.out.println("Digite uma Opcao Valida");
                    input.nextLine();
            }
        }*/

        input.close();

        /*escreve a saída do exmplo em um arquivo "TP1.txt"*/
        try {
            FileWriter fp = new FileWriter("ArvorePatricia_Saida.txt");
            fp.write(out);
            fp.close();
        } catch (IOException e) {
            System.out.println("ERRO:NAO FOI POSSIVEL CRIAR O ARQUIVO");
        }
    }
}
