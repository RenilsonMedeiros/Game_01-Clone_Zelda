package com.gcstudios.world;

// Classe importante que faz parte do algoritmo A*
public class Node {
	
	//Cada objeto node que eu vou instanciar vai ter um parent dessa classe Node
	public Vector2i tile;
	public Node parent;
	public double fCost, gCost, hCost; //Variáveis de Custos
	
	public Node(Vector2i tile, Node parent, double gCost, double hCost) {
		this.tile = tile;
		this.parent = parent;
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = gCost + hCost;
	}
	
}
