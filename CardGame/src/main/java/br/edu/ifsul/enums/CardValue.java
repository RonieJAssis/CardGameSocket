package br.edu.ifsul.enums;

/**
 *
 * @author Roniê Julian de Assis
 * @author Eliel Alves da Silva
 * Instituto Federal Sul-rio-grandense
 * Sistemas Distribuídos 1
 * Trabalho 2ª Etapa
 *
 */

public enum CardValue
{
  TWO(2),
  THREE(3),
  FOUR(4),
  FIVE(5),
  SIX(6),
  SEVEN(7),
  EIGHT(8),
  NINE(9),
  TEN(10),
  JACK(11),
  QUEEN(12),
  KING(13),
  ACE(14);

  private int cardValue;

  // Inicializa a carta com valor numérico
  private CardValue (int value) {
    this.cardValue = value;
  }

  // Retorna o valor numérico da carta
  public int getCardValue() {
    return cardValue;
  }
  
}
