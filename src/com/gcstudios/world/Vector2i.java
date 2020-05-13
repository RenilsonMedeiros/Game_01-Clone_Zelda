package com.gcstudios.world;

// Serve para guardar posi��es afim de obter mais controle.
public class Vector2i {
	
	public int x, y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	// M�todo para comparar duas posi��es
	public boolean equals(Object object) {
		Vector2i vec = (Vector2i) object;
		if(vec.x == this.x && vec.y == this.y) {
			return true;
		}
		
		return false;
	}
}
