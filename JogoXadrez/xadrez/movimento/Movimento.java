package xadrez.movimento;

import ui.Jogador;
import ui.Gui;
import ui.Icone;

import xadrez.tabuleiro.Casa;
import xadrez.tabuleiro.Tabuleiro;
import xadrez.tabuleiro.Simulador;
import xadrez.peca.Peca;
import xadrez.peca.Peao;
import xadrez.peca.Rei;
import xadrez.peca.Torre;
import xadrez.peca.PromoveuException;

import java.awt.Point;

import javax.swing.ImageIcon;

import java.io.Serializable;

public class Movimento implements Serializable {
	protected Casa donde, pronde; // Casas: donde saiu, pronde vai
	protected boolean posso; // Posso fazer esse movimento?
	
	protected String notacao_extra;
	
	private static int num; // Número total de jogadas
	
	/**
	 * Ctor: move o 'donde' para 'pronde'
	 */
	public Movimento (Casa donde, Casa pronde) {
		this.donde = donde;
		this.pronde = pronde;
		notacao_extra = "";
		posso = true;
	}
	/**
	 * Quando escolher qual movimento realmente fazer, chame o método 'Mover' dele
	 */
	public void mover (Jogador ator) {
		// mais um movimento!
		num++;
		
		// mata a peça, caso esteja no quadrado
		Peca aux = pronde.getPeca ();
		if (aux != null)
			aux.morre ();

		// pega a peça a ser movida e atualiza sua posição
		aux = donde.getPeca ();
		aux.setCoord (pronde.getCoord ());
		// Upgrade do peao
		if (aux instanceof Peao) {
			try {
				((Peao)aux).update (true);
			}
			// peao promovido - escolha do jogador
			catch (PromoveuException ex) {
				Peca nova = ex.getNova ();
				ator.addPeca (nova);
			}
		}
		if (aux instanceof Rei) {
			ator.setRoques (false, false);
		}
		if (aux instanceof Torre) {
			if (aux.getCoord ().getX () == 7)
				ator.setRoques (ator.getRoqueMaior (), false);
			else
				ator.setRoques (false, ator.getRoqueMenor ());
		}
		
		
		pronde.setPeca (aux);
		pronde.atualizaIcone ();
		donde.setPeca (null);
		donde.atualizaIcone ();
	}
	// pro simulador, pra não afetar nenhum dos reais jogadores, nem a GUI
	public void mover () {
		// pega a peça a ser movida e atualiza sua posição
		Peca aux = donde.getPeca ();
		aux.setCoord (pronde.getCoord ());

		pronde.setPeca (aux);
		donde.setPeca (null);
	}
	
	/**
	 * Simula o movimento, pra ver se pode (se não deixa o rei em xeque)
	 */
	public Simulador simula () {
		Simulador sim = new Simulador (donde, pronde);
		return sim;
	}
	
	/**
	 * Quadrado fica colorido, assim aparece como uma possível jogada
	 */
	public void printPossivel () {
		if (posso) {
			ImageIcon possivel = Icone.possibilita ((ImageIcon) pronde.getBotao ().getIcon (), pronde.estaOcupada ());
			pronde.getBotao ().setIcon (possivel);
		}
	}
	/**
	 * Quadrado não fica mais colorido
	 */
	public void unPrintPossivel () {
		if (posso)
			pronde.atualizaIcone ();
	}
	
	/**
	 * Formula e retorna a notação escrita da jogada
	 * @note Aqui não estão incluídas as notações de xeque e mate
	 */
	public String notacaoEscrita () {
		Peca aux = pronde.getPeca ();
		// se a casa está ocupada, notação de 'tomou peça'
		if (aux != null) {
			notacao_extra = "x" + aux.getNotacao ();
		}

		String str = new String ();	// exemplo de notação
		str += donde.getPeca ().getNotacao (); // D (peça)
		str += notacao_extra;
		str += (char) (pronde.getColuna () + 'a');	// g (coluna)
		str += 8 - pronde.getLinha (); // 3 (linha)
		
		return str;
	}
	/**
	 * Adiciona o movimento no log de movimentos
	 */
	public void jogaNoLog () {
		Gui.getTela ().logMovimento (this);
	}
	
	/**
	 * Verifica, a partir da posição indicada (ou da casa), se a casa é daquele movimento mesmo
	 */
	public boolean ehEsseMovimento (Point P) {
		Casa aux = Tabuleiro.getTabuleiro ().getCasa (P);
		return aux == pronde;
	}
	public boolean ehEsseMovimento (Casa aux) {
		return aux == pronde;
	}
	
	/* SETTERS */
	public void naoPosso () {
		posso = false;
	}
	public static void novoJogo () {
		num = 0;
	}
	/* GETTERS */
	public boolean getPosso () {
		return posso;
	}
	public static int getNumMovs () {
		return num;
	}
	public static void incNumMovs () {
		num++;
	}
	public Casa getDonde () {
		return donde;
	}
}
