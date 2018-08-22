package sudoku_package;

import java.util.ArrayList;
import java.util.List;

class BoardTile {

	private int val;
	private List<Integer> legalNums;

	private int x;
	private int y;
	
	public BoardTile(int val, int x, int y) {
		setVal(val);
		legalNums = new ArrayList<Integer>();
		this.x = x;
		this.y = y;
	}

	public String getId() {
		return "" + x + y;
	}

	public int getBlock() {
		return x/3 + (y/3)*3;
	}

	public int getVal() {
		return val;
	}

	public List<Integer> getLegalNums() {
		return new ArrayList<>(legalNums);
	}
	
	public void setLegalNums(List<Integer> legalNums) {
		this.legalNums = new ArrayList<>(legalNums);
	}
	
	public void setVal(int val) {
		if (val < 0 || val > 9)
			val = 0;
		this.val = val;
	}

	public boolean equals_int(int i) {
		return getVal() == i;
	}

	public boolean equals(Object o) {
		if (o instanceof BoardTile) {
			BoardTile other = (BoardTile)o;
			return other.getId().equals(getId());
		}
		return false;
	}

	public BoardTile clone() {
		BoardTile nt = new BoardTile(val, x, y);
		nt.setLegalNums(legalNums);
		return nt;
	}
}
