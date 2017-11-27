package main_package;

import java.util.Scanner;

import sudoku_package.*;

public class Main {

	public static void main(String[] args) {
		int[][] start = new int[][] {
			{ 6, 0, 3, 0, 0, 0, 2, 0, 9 },
			{ 0, 0, 0, 2, 0, 3, 0, 0, 0 },
			{ 4, 0, 0, 0, 8, 0, 0, 0, 6 },
			{ 0, 2, 0, 1, 0, 9, 0, 4, 0 },
			{ 0, 0, 1, 0, 0, 0, 5, 0, 0 },
			{ 0, 4, 0, 5, 0, 2, 0, 1, 0 },
			{ 2, 0, 0, 0, 9, 0, 0, 0, 1 },
			{ 0, 0, 0, 7, 0, 8, 0, 0, 0 },
			{ 9, 0, 8, 0, 0, 0, 7, 0, 3 }
		};
		SudokuBoard sb = new SudokuBoard(start);
		
		sb.printBoard();
		
		sb.solve();
		
		System.out.println("\nSolved:");
		sb.printBoard();
	}

}
