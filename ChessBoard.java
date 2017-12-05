import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;
import java.util.HashSet;
import java.util.Scanner;

public class ChessBoard {
	//all members of ChessBoard are declared static
	//so that they can be referenced from the main method

	private static final int SIZE = 8;
	private static final int NOT_POSSIBLE = -1;

	private static HashSet<String> visited = new HashSet<String>();

	//this class represents a coordinate on the chess board
	public static class Square { 
		public final int x;
		public final int y; 
		public int moves; //number of moves away from the 'start' square 	
	
		public Square (int x, int y) {
			this.x = x;
			this.y = y;
			this.moves = 0;
		}	
	}

	//this enum is used to specify the type of chess piece
	public enum Piece {
		KNIGHT,
		BISHOP,
		KING
	};

	//this method creates a String 'key' from a Square object 
	private static String createKey(Square s) {
		return s.x + "," + s.y;
	}

	//this method verifies whether a particular coordinate is on the chess board 
	private static boolean isWithinBounds(int x, int y) {
		if ((x >= SIZE) || (y >= SIZE) || (x < 0) || (y < 0)) return false;
		return true;
	}

	//this method verifies that a potential 'next' square has not already been visited
	private static void addIfNotVisited(List<Square> possibleMoves, int x, int y) {
		Square s = new Square(x, y);
		if (!visited.contains(createKey(s))) {
			possibleMoves.add(s);
		}
	}

	//this method gets all the possible unvisited squares that a particular piece can move to in a single turn given its starting square 
	private static List<Square> getPossibleNextMoves(Square start, Piece p) throws RuntimeException {
		List<Square> out = new ArrayList<Square>();
		int x = start.x;
		int y = start.y;
		switch(p) {
			case KNIGHT:
				for (int i = 1; i < 3; i++) {
					addIfNotVisited(out, x+i, y+3-i);
					addIfNotVisited(out, x-i, y+3-i);
					addIfNotVisited(out, x+i, y-3+i);
					addIfNotVisited(out, x-i, y-3+i);
				}
				break;
			case BISHOP:
					int x_i;
					int y_i;
					//NE direction
					x_i = x+1;
					y_i = y+1;
					while (isWithinBounds(x_i, y_i)) {
						addIfNotVisited(out, x_i, y_i);
						x_i++;
						y_i++;
					}
					//NW direction
					x_i = x-1;
					y_i = y+1;
					while (isWithinBounds(x_i, y_i)) {
						addIfNotVisited(out, x_i, y_i);
						x_i--;
						y_i++;
					}						
					//SE direction
					x_i = x+1;
					y_i = y-1;
					while (isWithinBounds(x_i, y_i)) {
						addIfNotVisited(out, x_i, y_i);
						x_i++;
						y_i--;
					}
					//SW direction
					x_i = x-1;
					y_i = y-1;
					while (isWithinBounds(x_i, y_i)) {
						addIfNotVisited(out, x_i, y_i);
						x_i--;
						y_i--;
					}
				break;
			case KING:
					addIfNotVisited(out, x-1, y-1);
					addIfNotVisited(out, x, y-1);
					addIfNotVisited(out, x+1, y-1);
					addIfNotVisited(out, x-1, y);
					addIfNotVisited(out, x+1, y);
					addIfNotVisited(out, x-1, y+1);
					addIfNotVisited(out, x, y+1);
					addIfNotVisited(out, x+1, y+1);
			break;
			default:
				throw new RuntimeException("Error! Unsupported piece!");
		}
		return out;
	}

	public static int findMinMoves(Piece p, int startX, int startY, int endX, int endY) {
		// use breadth-first search algorithm to find the mininum number of moves	
		boolean found = false;
		Queue<Square> q = new LinkedList<Square>();
		Square start = new Square(startX, startY);
		q.add(start);
		Square curr = null;
		while(!q.isEmpty()) {
			curr = q.remove(); //get a square from the queue
			visited.add(createKey(curr)); //mark as visited
			if ((curr.x == endX) && (curr.y == endY)) {
				found = true;	
				break;
			}

			//add all valid next moves to queue with a distance of +1
			List<Square> nextSquares = getPossibleNextMoves(curr, p);
			for (Square s : nextSquares) {
				s.moves = curr.moves+1; //keep track of number of moves from start square
				q.add(s); // add subsequent unvisted squares to queue
			} 
		}

		if (found) {
			return curr.moves;
		}
		return NOT_POSSIBLE; //not possible to reach the end square with the given start square and piece 
	}

	public static void main(String [] args) {
		Piece p;
		Scanner s = new Scanner(System.in);
	
		//display welcome message
		System.out.println("\nWelcome! This program can calculate the minimum number of moves that a Knight, Bishop or King will take to reach a destination square from a starting square on a 8x8 chess board.");

		//get start position input from user 
		int srt_x;
		int srt_y;	
		do {
			System.out.println("\nWhat start position would you like to choose? Enter the coordinates in the format 'x y', where both x and y are numbers from 0 to " + String.valueOf(SIZE-1) + ". 0 0 corresponds to the top left corner of the chess board.");	
			srt_x = s.nextInt();
			srt_y = s.nextInt();
		}
		while(!isWithinBounds(srt_x, srt_y));	

		//get end position input from user 
		int end_x;
		int end_y;	
		do {
			System.out.println("\nWhat end position would you like to choose? Enter the coordinates in the format 'x y', where both x and y are numbers from 0 to " + String.valueOf(SIZE-1) + ".");	
			end_x = s.nextInt();
			end_y = s.nextInt();
		}
		while(!isWithinBounds(end_x, end_y));

		//get piece input from user
		String in;
		do {
			System.out.println("\nWhat piece would you like to use? Enter 'b' for Bishop, 'n' for Knight or 'k' for King.");  
			in = s.next();
		} while(!(in.equals("b") || in.equals("n") || in.equals("k")));
		if (in.equals("b")) { 
			p = Piece.BISHOP;
		}
		else if (in.equals("n")) { 
			p = Piece.KNIGHT;
		}
		else { 
			p = Piece.KING;	
		}
		
		int result = findMinMoves(p, srt_x, srt_y, end_x, end_y );
		if (result == NOT_POSSIBLE) {
			System.out.println("\nIt is not possible to reach that sqaure with the given piece!");
		} else {
			System.out.println("\nThe mimimum turns needed is " + result + ".");
		} 	
	}
}
