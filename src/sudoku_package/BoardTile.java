package sudoku_package;

import java.util.ArrayList;
import java.util.List;

class BoardTile {

	private int val;
	private List<Integer> legalNums;
	
	public BoardTile() {
		this(0);
	}
	
	public BoardTile(int val) {
		setVal(val);
		legalNums = new ArrayList<Integer>();
	}
	
	public int getVal() {
		return val;
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
			if (other.getVal() == getVal())
				return true;
		}
		return false;
	}
}
