package sudoku_package;

import java.util.*;
import java.util.stream.Collectors;

public class SudokuBoard {
	
	private Map<String, BoardTile> board;
	
	/**
	 * Creates a new blank board.
	 */
	public SudokuBoard() {
		board = new HashMap<>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				BoardTile tile = new BoardTile(0, i, j);
				board.put(tile.getId(), tile);
			}
		}
	}
	
	/**
	 * Copies a given board unless that
	 * board's dimensions are not 9x9.
	 *
	 * @param board a 2x2 array of ints representing a board
	 */
	public SudokuBoard(int[][] board) {
		this();
		if (board.length == 9 && board[0].length == 9) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					this.board.get(""+i+j).setVal(board[i][j]);
				}
			}
		} else {
			throw new IllegalArgumentException("board is not 9x9");
		}
	}

	/**
	 * Copies a given board from a BoardTile map
	 *
	 * @param board the map to copy from
	 */
	private SudokuBoard(Map<String, BoardTile> board) {
		this();
		for(String key : board.keySet()) {
			this.board.put(key, board.get(key).clone());
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
	 * Moves are made by executing strategies in order of logic and efficiency
	 * until a move is made, or not if there are no possible moves.
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
		for (BoardTile bt : getAll(true)) {
			if (bt.getLegalNums().size() == 1) {
				bt.setVal(bt.getLegalNums().get(0));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines the next move by checking if any blank
	 * tiles have a legal number that no other blank tiles
	 * in the same row, column, or block have.
	 * 
	 * @return true if a move was successfully made, otherwise false.
	 */
	private boolean solveByElimination() {
		for (BoardTile bt : getAll(true)) {
			List<BoardTile> row = getRow(bt, false);
			List<BoardTile> col = getCol(bt, false);
			List<BoardTile> blk = getBlk(bt, false);
			for (int k : bt.getLegalNums()) {
				boolean a = row.stream()
						.map(BoardTile::getLegalNums)
						.noneMatch(lst -> lst.contains(k));
				boolean b = col.stream()
						.map(BoardTile::getLegalNums)
						.noneMatch(lst -> lst.contains(k));
				boolean c = blk.stream()
						.map(BoardTile::getLegalNums)
						.noneMatch(lst -> lst.contains(k));
				if(a || b || c) {
					bt.setVal(k);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Determines the next move by checking if any blank
	 * tiles have only two legal options, then guesses one
	 * and determines if the board is solvable.
	 * 
	 * If the board is still solvable, that move is made,
	 * otherwise the other option is used.
	 * 
	 * @return true if a move was successfully made, otherwise false.
	 */
	private boolean solveByGuessing() {
		for (BoardTile bt : getAll(true)) {
			List<Integer> legal = bt.getLegalNums();
			if (legal.size() == 2) {
				bt.setVal(legal.get(0));

				SudokuBoard temp = new SudokuBoard(board);
				temp.solve();

				if (!temp.fullBoard()) bt.setVal(legal.get(1));

				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines the legal numbers for each tile
	 * (numbers that could be played there given the
	 * current board state) for the current board state.
	 */
	private void updateLegalNums() {
		for(BoardTile bt : getAll(false)) {
			updateLegalNums(bt);
		}
	}
	
	/**
	 * Determines the legal numbers for a specific tile and updates the tile's legalNums list
	 *
	 * @param t the tile whose legal numbers will be updated
	 */
	private void updateLegalNums(BoardTile t) {
		t.setLegalNums(Arrays.stream(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9})
				.filter(r -> getRow(t, true).stream().noneMatch(e -> e.equals_int(r)))
				.filter(c -> getCol(t, true).stream().noneMatch(e -> e.equals_int(c)))
				.filter(s -> getBlk(t, true).stream().noneMatch(e -> e.equals_int(s)))
				.collect(Collectors.toList()));
	}

	/**
	 * Returns all tiles in the board
	 *
	 * @param only_empty_tiles option to only include tiles whose value is 0 in the list
	 * @return a List<BoardTile> of all tiles in the board
	 */
	private List<BoardTile> getAll(boolean only_empty_tiles) {
		return board.values().stream()
				.filter(t -> !only_empty_tiles || t.getVal() == 0)
				.collect(Collectors.toList());
	}

	/**
	 * Returns the row of tiles of the given BoardTile
	 *
	 * @param t origin tile
	 * @param include_origin option to include the origin in the returned list
	 * @return a List<BoardTile> of tiles in the origin's row
	 */
	private List<BoardTile> getRow(BoardTile t, boolean include_origin) {
		char x = t.getId().charAt(0);
		char y = t.getId().charAt(1);
		return board.keySet().stream()
				.filter(k -> k.charAt(0) == x)
				.filter(k -> include_origin || k.charAt(1) != y)
				.map(board::get)
				.collect(Collectors.toList());
	}

	/**
	 * Returns the column of tiles of the given BoardTile
	 *
	 * @param t origin tile
	 * @param include_origin option to include the origin in the returned list
	 * @return a List<BoardTile> of tiles in the origin's column
	 */
	private List<BoardTile> getCol(BoardTile t, boolean include_origin) {
		char x = t.getId().charAt(0);
		char y = t.getId().charAt(1);
		return board.keySet().stream()
				.filter(k -> k.charAt(1) == y)
				.filter(k -> include_origin || k.charAt(0) != x)
				.map(board::get)
				.collect(Collectors.toList());
	}

	/**
	 * Returns the block of tiles of the given BoardTile
	 *
	 * @param t origin tile
	 * @param include_origin option to include the origin in the returned list
	 * @return a List<BoardTile> of tiles in the origin's block
	 */
	private List<BoardTile> getBlk(BoardTile t, boolean include_origin) {
		return board.values().stream()
				.filter(bt -> bt.getBlock() == t.getBlock())
				.filter(bt -> include_origin || !bt.equals(t))
				.collect(Collectors.toList());
	}
	
	/**
	 * Determines if a board is full (has no blank tiles)
	 * 
	 * @return true if the board is full, otherwise false
	 */
	private boolean fullBoard() {
		return getAll(true).isEmpty();
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
				int v = board.get(""+i+j).getVal();
				System.out.print(v == 0 ? "." : v);
			}
			System.out.println("|");
		}
	}
}