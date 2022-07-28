// 18/07/2021 - Para Laboratório de Algoritmos e Estruturas de Dados II, 2021/1
// Guilherme Moreira de Carvalho & Marcus Vinícius Diniz Ribeiro
// Implemetação de uma Árvore Patricia
// Referências: Projeto de Algoritmos com implementações em Java e C++ (2006), Nivio Ziviani

import java.util.ArrayList;

public class ArvorePatricia {
    private static abstract class PatNo { }
    private static class PatNoInt extends PatNo {
        int index; PatNo esq, dir;
    }  
    private static class PatNoExt extends PatNo {
        String chave;
        int[] bin;
        int count;  // Quantas vezes a chave foi inserida
        ArrayList<Integer> _linha, _coluna; // Onde a chave foi lida
    }
    
    private PatNo raiz;
    private int nbitsChave;
    public int linha, coluna;   // Armazenam onde cada palavra do texto foi lida

    public ArvorePatricia (int nbitsChave) {
        this.raiz = null;
        this.nbitsChave = nbitsChave;
        this.linha = 1; this.coluna = 0;
    }
    
    // Retorna o i-ésimo bit da chave k a partir da esquerda
    private int bit (int i, int[] bin) {
        if (i == 0)
            return 0;
        int j;
        for (j = 1; j <= this.nbitsChave - i; j++){}
        return bin[j-1];
    }

    // Expande uma sequência de até 16 caracteres para bits
    // Cada caractere corresponde a 8 bits - 128 bits
    // Espaços vazios preenchidos com 0s
    public int[] toBin (String k) {
        int c, n = 0, j = 0;
        int[] bin = new int[this.nbitsChave];
        for (int i = 1; i <= k.length() && i < 16; i++){
            c = (int)k.toCharArray()[i-1];
            if (c < 0 || c > 127)   // Considera apenas caracteres ASCII
                continue;
            j = i*8-1;  // Última posição referente ao caractere i
            n = j+1;    // Primeria posição referente ao caractere i+1
            
            // Conversão
            while ( c/2 >= 0 && j >= i*8-8) {
                bin[j] = c%2;
                c /= 2;
                j--;
            }
            // Preenche as posições vazias referentes ao caractere i
            while (j >= i*8-8) {
                bin[j] = 0;
                j--;
            }
        }

        // Preenche o restante dos bits
        while (n < this.nbitsChave){
            bin[n] = 0;
            n++;
        }
        return bin;
    }

    // Verifica se p é nó externo
    private boolean eExterno (PatNo p) {    
        Class classe = p.getClass();
        return classe.getName().equals(PatNoExt.class.getName());
    }

    private PatNo criaNoInt (int i, PatNo esq, PatNo dir) {
        PatNoInt p = new PatNoInt();
        p.index = i; p.esq = esq; p.dir = dir;
        return p;
    }

    private PatNo criaNoExt (String k, int[] bin) {
        PatNoExt p = new PatNoExt();
        p.chave = k; p.bin = bin;
        p.count = 1;
		p._linha = new ArrayList<>(); p._linha.add(this.linha);
		p._coluna = new ArrayList<>(); p._coluna.add(this.coluna);
        return p;
    }
    
    private String pesquisa (String k, int[] bin, PatNo t) {
        String out = "";
        // Se estiver em um nó externo
        if (this.eExterno(t)) {
            PatNoExt aux = (PatNoExt)t;
            // Compara as chaves
            if (aux.chave.equals(k)) {
                out += "Elemento encontrado - " + aux.count + "\n";
                out += "Linhas - " + aux._linha.toString() + "\n";
                out += "Colunas - " + aux._coluna.toString() + "\n";
            } else
                out += "Elemento nao encontrado\n";
        } else { 
            // Procura na subárvore
            PatNoInt aux = (PatNoInt)t;
            if (this.bit(aux.index, bin) == 0) { out = pesquisa(k, bin, aux.esq); }
            else { out = pesquisa(k, bin, aux.dir); }
        }
        return out;
    }

    private PatNo insereEntre (String k, int[] bin, PatNo t, int i) {
        PatNoInt aux = null; 
        if (!this.eExterno(t)) { aux = (PatNoInt)t; }
        // Se está em um nó externo ou em uma subávore vazia
        if (this.eExterno(t) || (i < aux.index)) {  // Cria um novo nó externo
            PatNo p = this.criaNoExt(k, bin);
            if (this.bit(i, bin) == 1) {    // Cria um novo nó interno
                // Com a chave inserida à direita
                return this.criaNoInt(i, t, p);
            } else {
                // Com a chave inserida à esquerda
                return this.criaNoInt(i, p, t);
            }
        } else {
            if (this.bit(aux.index, bin) == 1)
                aux.dir = this.insereEntre(k, bin, aux.dir, i);
            else
                aux.esq = this.insereEntre(k, bin, aux.esq, i);
            return aux;
        }
    }
    
    private PatNo insere (String k, int[] bin, PatNo t) {
        // Condição de parada : nó externo vazio
        if (t == null) { return this.criaNoExt(k, bin); }
        else {
            PatNo p = t;
            // Percorre até um nó externo
            while (!this.eExterno(p)) {
                PatNoInt aux = (PatNoInt)p;
                if (this.bit(aux.index, bin) == 1) { p = aux.dir; }
                else { p = aux.esq; }
            }
            PatNoExt aux = (PatNoExt)p;
            int i = 1;  // Acha o primeiro bit diferente
            while ((i <= this.nbitsChave) && (this.bit(i, bin) == this.bit(i, aux.bin)))
                i++;
            if (i > this.nbitsChave) {  // Todos os bits iguais
                aux.count++;
                aux._linha.add(this.linha); aux._coluna.add(this.coluna);
                // System.out.println ("Erro: chave ja esta na arvore"); 
                return t;
            } else {
                // Cria uma subárvore
                return this.insereEntre(k, bin, t, i);
            }
        }
    }
    
    private void central (PatNo pai, PatNo filho, String msg) {
        if (filho != null) {
            if (!this.eExterno(filho)) {
                PatNoInt aux = (PatNoInt)filho; 
                central(filho, aux.esq, "ESQ");
                if (pai != null)
                    System.out.println("Pai: " + ((PatNoInt)pai).index + " " + msg + " Int: " + aux.index);
                else
                    System.out.println("Pai: " + pai + " " + msg + " Int: " + aux.index);
                central(filho, aux.dir, "DIR");
            } else {
                PatNoExt aux = (PatNoExt)filho;
                if (pai != null)
                    System.out.println("Pai: " + ((PatNoInt)pai).index + " " + msg + " Ext: " + aux.chave);
                else
                    System.out.println("Pai: " + pai + " " + msg + " Ext: " + aux.chave);
            }
        }
    }

    public void imprime () {
        this.central(null, this.raiz, "RAIZ");
    }

    public String pesquisa (String k) {
        int[] bin = this.toBin(k);
        return this.pesquisa(k, bin, this.raiz);
    }
    public void insere (String k) {
        int[] bin = this.toBin(k);
        this.raiz = this.insere(k, bin, this.raiz);
    }
}
