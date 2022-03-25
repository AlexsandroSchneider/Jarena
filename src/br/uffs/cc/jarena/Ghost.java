package br.uffs.cc.jarena;

import java.security.Principal;

public class Ghost extends Agente
{
	private Boolean onMyWay = false, healing = false;

	public Ghost(Integer x, Integer y, Integer energia) {
		super(x, y, energia);
		setDirecao(geraDirecaoAleatoria());
	}

	public void goToXY(int X, int Y) {
		int absX = Math.abs(getX() - X);
		int absY = Math.abs(getY() - Y);
		if (Math.random() < 0.5 && absX > 2) {
			setDirecao(getX() > X ? ESQUERDA : DIREITA);
		} else if (absY > 2) setDirecao(getY() > Y ? CIMA : BAIXO);
		else para();
	}

	public String GPS() {
		String xyID = getX() + "@" + getY() + "@" + getId();
		return xyID;
	}

	public void testaMovimentacao() {
		if (!podeMoverPara(getDirecao())) { 
			setDirecao(geraDirecaoAleatoria());
		}
	}
	
	public void pensa() {

		double aleat = Math.random();
		double probab = Math.random();
		if (!healing) {
			if (!onMyWay) {
				if (!(Math.abs(getX()-(Constants.LARGURA_MAPA/2)) < 200 && Math.abs(getY()-(Constants.ALTURA_MAPA/2)) < 200)) {
					if (getEnergia() < 200 || (probab < 0.1075 && probab > 0.075)) {
						para();
					} else if (probab <= 0.075 && probab > 0.015){
						if (getX() < Constants.LARGURA_MAPA/2) {
							if (getY() < Constants.ALTURA_MAPA/2) {
								setDirecao(aleat > 0.5 ? DIREITA : BAIXO);
							} else setDirecao(aleat > 0.5 ? DIREITA : CIMA);
						} else if (getY() < Constants.ALTURA_MAPA/2) {
							setDirecao(aleat > 0.5 ? ESQUERDA : BAIXO);
						} else setDirecao(aleat > 0.5 ? ESQUERDA : CIMA);
					} else if (probab <= 0.015) {
						setDirecao(geraDirecaoAleatoria());
					} else testaMovimentacao();
				} else if (probab <= 0.025) setDirecao(geraDirecaoAleatoria());
			} else onMyWay = false;
		} else {
			para();
			healing = false;
		}
	}
	
	public void recebeuEnergia() {
		healing = true;
		para();
		enviaMensagem(("energia@" + GPS()));
	}
	
	public void tomouDano(int energiaRestanteInimigo) {
		if (getEnergia() > energiaRestanteInimigo){
			para();
			enviaMensagem(("dano@" + GPS()));	
		} else testaMovimentacao();
	}
	
	public void ganhouCombate() {
		enviaMensagem(("vitoria@" + GPS()));
		testaMovimentacao();
	}

	public void recebeuMensagem(String msg) {
		String[] in = msg.split("@");
		if (in[0].equals("energia") || in[0].equals("dano")) {
			int X = Integer.parseInt(in[1]);
			int Y = Integer.parseInt(in[2]);
			if (!healing && getId() != Integer.parseInt(in[3])) {
				if ((getEnergia() <= 200 && getEnergia() >= 100 && Math.abs(getX() - X) < 100 && Math.abs(getY() - Y) < 100)|| getEnergia() > 200){
					onMyWay = true;
					goToXY(X, Y);
				}
			}
		}
	}
	
	public String getEquipe() {
		return "Ghost//";
	}
}
