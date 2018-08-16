package sudoku_package;

import java.util.ArrayList;
import java.util.List;

class BoardTile {

	private int val;
	private List<Integer> legalNums;

	private int row;
	private int col;
	private int blk;
	
	public BoardTile(int val, int x, int y) {
		setVal(val);
		legalNums = new ArrayList<Integer>();
		row = y;
		col = x;
		blk = x/3 + (y/3)*3;
	}
	
	public int getVal() {
		return val;
	}

	public int[] getPos() {
		return new int[] {row, col, blk};
	}

	public List<Integer> getLegalNums() {
		return legalNums;
	}
	
	public void setLegalNums(List<Integer> legalNums) {
		this.legalNums = legalNums;
	}
	
	public void setVal(int val) {
		if (val < 0 || val > 9)
			val = 0;
		this.val = val;
	}
	
	public boolean equals(Object o) {
		if (o instanceof BoardTile) {
			BoardTile other = (BoardTile)o;
			return other.getVal() == getVal();
		}
		return false;
	}

	public boolean equals_int(int i) {
	    return getVal() == i;
    }
}
