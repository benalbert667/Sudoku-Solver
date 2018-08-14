package sudoku_package;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SudokuBoard {
	
	private BoardTile[][] board;
	
	/**
	 * Creates a new blank board.
	 */
	public SudokuBoard() {
		board = new BoardTile[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j] = new BoardTile();
			}
		}
	}
	
	/**
	 * Copies a given board unless that
	 * board's dimensions are not 9x9.
	 */
	public SudokuBoard(int[][] board) {
		this();
		if (board.length == 9 && board[0].length == 9) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					this.board[i][j].setVal(board[i][j]);;
				}
			}
		}
	}
	
	/**
	 * Copies a given board unless that
	 * board's dimensions are not 9x9.
	 */
	public SudokuBoard(BoardTile[][] board) {
		this();
		if (board.length == 9 && board[0].length == 9) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					this.board[i][j].setVal(board[i][j].getVal());
				}
			}
		}
	}
	
	/**
	 * Solves the entire board
	 * (assumes a legal board).
	 */
	public void solve() {
		boolean moveMade;
		do
			moveMade = makeNextMove();
		while (moveMade);
	}
	
	/**
	 * Makes a single correct move.
	 * 
	 * @return true if a move was successfully made, otherwise false.
	 */
	public boolean makeNextMove() {
		updateLegalNums();

		boolean moveMade = solveByLegalNums();
		
		if (!moveMade) moveMade = solveByElimination();
		
		if (!moveMade) moveMade = solveByGuessing();
		
		return moveMade;
	}
	
	/**
	 * Determines the next move by checking if any blank
	 * tiles have only one legal number (no other numbers
	 * could occupy that tile).
	 * 
	 * @return true if a move was successfully made, otherwise false.
	 */
	private boolean solveByLegalNums() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				List<Integer> legal = board[i][j].getLegalNums();
				if (board[i][j].getVal() == 0 && legal.size() == 1) {
					makeMove(i, j, legal.get(0));
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Determines the next move by checking if any blank
	 * tiles can have a value that no other blank tiles
	 * in the same row, column, or block can have.
	 * 
	 * @return true if a move was successfully made, otherwise false.
	 */
	private boolean solveByElimination() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				List<Integer> legal = board[i][j].getLegalNums();
				if (board[i][j].getVal() == 0) {
					searchRow:
						for (int k : legal) {
							for (int l = 0; l < 9; l++) {
								if (l!=i && board[l][j].getVal() == 0 &&
									board[l][j].getLegalNums().contains(k))
									continue searchRow;
							}
							makeMove(i, j, k);
							return true;
						}
					
					searchCol:
						for (int k : legal) {
							for (int l = 0; l < 9; l++) {
								if (l!=j && board[i][l].getVal() == 0 &&
									board[i][l].getLegalNums().contains(k))
									continue searchCol;
							}
							makeMove(i, j, k);
							return true;
						}
						
					searchBlock:
						for (int k : legal) {
							for (int l = 0; l < 9; l++) {
								int x = (i/3)*3 + (l%3);
								int y = (j/3)*3 + (l/3);
								if (!(x == i && y == j) && board[x][y].getVal() == 0 &&
									board[x][y].getLegalNums().contains(k))
									continue searchBlock;
							}
							makeMove(i, j, k);
							return true;
						}
					}
				}
		}
		return false;
	}
	
	/**
	 * Determines the next move by checking if any blank
	 * tiles have only two legal options, then guesses one
	 * and determines if the board is solveable.
	 * 
	 * If the board is still solveable, that move is made,
	 * otherwise the other option is used.
	 * 
	 * @return true if a move was successfully made, otherwise false.
	 */
	private boolean solveByGuessing() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				List<Integer> legal = board[i][j].getLegalNums();
				if (board[i][j].getVal() == 0 && legal.size() == 2) {
					SudokuBoard temp = new SudokuBoard(board);
					temp.makeMove(i, j, legal.get(0));
					temp.solve();
					
					if (temp.fullBoard())
						makeMove(i, j, legal.get(0));
					else
						makeMove(i, j, legal.get(1));
					
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Makes a move at the given position
	 * 
	 * If this methid is given parameters that either
	 * point to a tile outside of the board or attempt
	 * to insert a value that is negative or greater
	 * than 9, it will do nothing
	 * 
	 * @param posX x position on board (int 0-8 inclusive)
	 * @param posY y position on board (int 0-8 inclusive)
	 * @param val value to be placed in tile (int 0-9 inclusive) (0 value signifies a blank tile)
	 */
	public void makeMove(int posX, int posY, int val) {
		if (posX < 0 || posX > 8 ||
			posY < 0 || posY > 8)
			return;

		board[posX][posY].setVal(val);
	}
	
	/**
	 * Determines the legal numbers for each tile
	 * (numbers that could be played there given the
	 * current board state) for the current board state.
	 */
	private void updateLegalNums() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				updateLegalNums(i, j);
			}
		}
	}
	
	/**
	 * Determines the legal numbers for a specific tile
	 * @param x x-coord of tile
	 * @param y y-coord of tile
	 */
	private void updateLegalNums(int x, int y) {
		board[x][y].setLegalNums(Arrays.stream(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9})
				.filter(r -> getRow(x).stream().noneMatch(e -> e.equals_int(r)))
				.filter(c -> getCol(y).stream().noneMatch(e -> e.equals_int(c)))
				.filter(s -> getBlock(x, y).stream().noneMatch(e -> e.equals_int(s)))
				.collect(Collectors.toList()));
	}
	
	/**
	 * Determines the numbers in a certain row
	 * 
	 * @param posX number row (int 0-8 inclusive)
	 * @return A List<Integer> of numbers in the row
	 */
	private List<BoardTile> getRow(int posX) {
		List<BoardTile> toReturn = new ArrayList<>();
		for (BoardTile i : board[posX]) {
			toReturn.add(i);
		}
		return toReturn;
	}
	
	/**
	 * Determines the numbers in a certain column
	 * 
	 * @param posY number column (int 0-8 inclusive)
	 * @return A List<Integer> of numbers in the column
	 */
	private List<BoardTile> getCol(int posY) {
		List<BoardTile> toReturn = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			toReturn.add(board[i][posY]);
		}
		return toReturn;
	}
	
	/**
	 * Determines the numbers in a certain tile's block
	 * 
	 * @param posX tile's x position on board (int 0-8 inclusive)
	 * @param posY tile's y position on board (int 0-8 inclusive)
	 * @return A List<Integer> of numbers in the tile's block
	 */
	private List<BoardTile> getBlock(int posX, int posY) {
		List<BoardTile> toReturn = new ArrayList<>();
		int a = (posX/3)*3;
		int b = (posY/3)*3;
		
		for (int i = a; i < a + 3; i++) {
			for (int j = b; j < b + 3; j++) {
				toReturn.add(board[i][j]);
			}
		}
		return toReturn;
	}
	
	/**
	 * Determines if a board is full (has no blank tiles)
	 * 
	 * @return true if the board is full, otherwise false
	 */
	protected boolean fullBoard() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (board[i][j].getVal() == 0)
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Prints the board
	 */
	public void printBoard() {
		for (int i = 0; i < 9; i++) {
			if (i % 3 == 0 && i != 0) System.out.println();
			for (int j = 0; j < 9; j++) {
				System.out.print("|");
				if (j % 3 == 0 && j != 0) System.out.print(" |");
				System.out.print(board[i][j].getVal() == 0 ? "." : board[i][j].getVal());
			}
			System.out.println("|");
		}
	}
	
	/**
	 * Returns the value at a specific tile
	 * 
	 * @param posX x position on board (int 0-8 inclusive)
	 * @param posY y position on board (int 0-8 inclusive)
	 * @return value at tile (posX, posY)
	 */
	public int getVal(int posX, int posY) { return board[posX][posY].getVal(); }
}