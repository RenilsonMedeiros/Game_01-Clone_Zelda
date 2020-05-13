package com.gcstudios.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AStar {
	
	public static double lastTime = System.currentTimeMillis();
	public static Comparator<Node> nodeSorter = new Comparator<Node>() {
		//Coloquei a pr�pria classe dentro dessa inst�ncia que eu criei para dizer como eu quero o meu comparador
		
		@Override
		public int compare(Node n0, Node n1) {
			if(n1.fCost < n0.fCost) return +1;
			if(n1.fCost > n0.fCost) return -1;
			
			return 0;
		}
		
	};
	
	/* OTIMIZA��O: Quando passar de uma certa quantidades de segundos poder eu retornar, pq pode ser que o meu 
	 * player possa est� em uma situa��o complicada que vai fazer o algortimo calcular in�meras possibilidades 
	 * e acabar travando o algoritmo:
	 */
	public static boolean clear() { 
		if(System.currentTimeMillis() - lastTime > 1000) {
			return true;
		}
		
		return false;
	}
	
	/* Vamos agora para o m�todo "acharCaminho":
	 *	   World: onde vai est� os meus tiles; start: posInicial do meu inimigo; end: posFinal do meu inimigo
	 */
	public static List<Node> findPath(World world, Vector2i start, Vector2i end) {
		lastTime = System.currentTimeMillis();
		
		//Posi��es poss�veis que meu inimigo vai ter que percorrer para chegar no meu player
		List<Node> openList = new ArrayList<Node>();
		
		//Posi��es que eu j� verifiquei e eu sei que n�o s�o v�lidas
		List<Node> closedList = new ArrayList<Node>();
		
		// O hCost est� relacionado com o valor retornado no getDistance, isto �, a dist do posInicial ao Final
		Node current = new Node(start, null, 0, getDistance(start, end));
		openList.add(current);
		while(openList.size() > 0) { // Significa que eu ainda tenho chance de achar o caminho 
			
			/* Agora eu vou organizar a minha lista com base no m�todo compare(), para isso, eu passo 
			 * os seguintes Par�metros ao m�todo sort do obj Colletions: A lista que eu quero organizar(openList)
			 * e o objeto que possui o m�todo personalizado de compara��o(nodeSorter).
			 */
			Collections.sort(openList, nodeSorter); 
			
			current = openList.get(0); //Depois de organizado, eu pego o primeiro item da lista.
			if(current.tile.equals(end)) { 
				//Significa que eu j� cheguei ao meu destino. Basta retornar o valor!
				List<Node> path = new ArrayList<Node>(); //Lista: caminho
				while(current.parent != null) { 
					//Significa que eu ainda posso adicionar esses itens na minha lista
					
					path.add(current);
					current = current.parent;
				}
				//Limpar todas as listas da mem�ria e retornar o "caminho"
				openList.clear();
				closedList.clear();
				return path;
			}
			
			/* Se n�o conseguiu ainda ver que ta na posDestino, isto �, a pos que eu quero.
			 * Eu removo da lista aberta e adiciono na lista fechada.
			 */
			openList.remove(current);  //Porque j� sei que n�o � a posi��o que eu quero
			closedList.add(current);			
			
			//Esse looping percorre todas as posi��es vizinhas da posi��o atual do meu current.
			for(int i = 0; i < 9; i++) {
				if(i == 4) continue; // � a posi��o que o meu inimigo est�.
				int x = current.tile.x;
				int y = current.tile.y;
				int xi = (i%3) - 1;
				int yi = (i/3) - 1;
				Tile tile = World.tiles[x+xi + ((y+yi)*World.WIDTH)];
				
				if(tile == null) continue;
				if(tile instanceof WallTile) continue;
				
				/** +-- Adapta��es feitas a fim do nosso inimigo andar na diagonal --+ **/
				if(i == 0) { //xi = -1   yi = -1
					Tile test = World.tiles[x+xi+1 + ((y+yi)*World.WIDTH)]; 
					Tile test2 = World.tiles[x+xi + ((y+yi+1)*World.WIDTH)];
					if(test instanceof WallTile || test2 instanceof WallTile) continue;
					
				} else if(i == 2) { //xi = 1   yi = -1
					Tile test = World.tiles[x+xi-1 + ((y+yi)*World.WIDTH)];   
					Tile test2 = World.tiles[x+xi + ((y+yi+1)*World.WIDTH)];
					if(test instanceof WallTile || test2 instanceof WallTile) continue;
					
				} else if(i == 6) { //xi = -1   y1 = 1
					Tile test = World.tiles[x+xi + ((y+yi-1)*World.WIDTH)];   
					Tile test2 = World.tiles[x+xi+1 + ((y+yi)*World.WIDTH)];
					if(test instanceof WallTile || test2 instanceof WallTile) continue;
					
				} else if(i == 8) { //xi = 1   y1 = 1
					Tile test = World.tiles[x+xi + ((y+yi-1)*World.WIDTH)];   
					Tile test2 = World.tiles[x+xi-1 + ((y+yi)*World.WIDTH)];
					if(test instanceof WallTile || test2 instanceof WallTile) continue;
					
				}
				/** +----------------------------------------------------------------+ **/
				
				Vector2i a = new Vector2i(x+xi, y+yi); // Ponto onde eu quero ir.
				double gCost = current.gCost + getDistance(current.tile, a); //Calcular o custo desse movimento.
				double hCost = getDistance(a, end);
				
				Node node = new Node(a, current, gCost, hCost);
				
				/*Verifica se o vetor "a" que eu criei j� existe na closedList (posi��es impossiveis), 
				 * e se o gCost que eu acabei de criar � maio ou igual ao gCost atual.
				 */
				if(vecInList(closedList, a) && gCost >= current.gCost) continue;
				
				if(!vecInList(openList, a)) openList.add(node);
				else if(gCost < current.gCost) { //Significa que ainda h� um caminho ainda mais barato.
					openList.remove(current);   //Ent�o eu removo o atual
					openList.add(node);        //E adiciono o que eu acabei de criar.
					
				}
			}
		}
		
		closedList.clear();
		return null;
	}
	
	/* pra ver se a posi��o que estou verificando j� existe na lista, isto �, Verificar se o "vector" 
	 * j� existe dento da "list" de Node, j� que a list<Node> guarda objetos cujo atributos(tile) s�o objetos 
	 * da classe Vector2i.
	 */
	private static boolean vecInList(List<Node> list, Vector2i vector) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).tile.equals(vector)) return true;
		}
		
		return false;
	}
	
	private static double getDistance(Vector2i tile, Vector2i goal) {
		double dx = tile.x - goal.x;
		double dy = tile.y - goal.y;
		
		return Math.sqrt(dx*dx + dy*dy); //Dist�ncia de um ponto a outro.
	}
	
}
