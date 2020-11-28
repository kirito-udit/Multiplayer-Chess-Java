/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

/**
 *
 * @author Uditk
 */

//package chess;

import java.awt.Point;

public class Cell {

	public char id;
	public final boolean whitePiece;
	public boolean firstMove = true;

	public Cell(char p) {
		boolean whiteStatus = true;
		id = p;
		if(id==0x265A || id==0x265B || id==0x265C || id==0x265D || id==0x265E || id==0x265F) {
			whiteStatus = false;
		}
		whitePiece = whiteStatus;
	}






	public boolean[][] possibleMoves(Point selectedCell, Cell[][] grid) {
		int x = selectedCell.x;
		int y = selectedCell.y;

		boolean[][] moves = {
			new boolean[8], 
			new boolean[8], 
			new boolean[8], 
			new boolean[8], 
			new boolean[8], 
			new boolean[8], 
			new boolean[8], 
			new boolean[8] 
		};
		
		byte whiteStatus = 1;
		if(whitePiece) {
			whiteStatus*=-1;
		}
		switch(id) {
		case 0x265F:
		case 0x2659:
			if(y>0 && y<7 && grid[x][y+whiteStatus].id == ' ') {
				moves[x][y+whiteStatus] = true;
				if(firstMove) {
					if(grid[x][y+whiteStatus*2].id == ' ') {
						moves[x][y+whiteStatus*2] = true;
					}
				}
			}
			if(x<7 && grid[x+1][y+whiteStatus].id != ' ' && grid[x+1][y+whiteStatus].whitePiece != whitePiece) {
				moves[x+1][y+whiteStatus] = true;
			}
			if(x>0 && grid[x-1][y+whiteStatus].id != ' ' && grid[x-1][y+whiteStatus].whitePiece != whitePiece) {
				moves[x-1][y+whiteStatus] = true;
			}
			break;
		case 0x265c:
		case 0x2656:
			byte d = (byte) (x+1);
			while(d<=7) {
				if(grid[d][y].id == ' ') {
					moves[d][y] = true;
				}
				else {
					if(grid[d][y].whitePiece!=whitePiece) {
						moves[d][y] = true;
					}
					d=7;
				}
				d++;
			}
			
			d = (byte) (x-1);
			while(d>=0) {
				if(grid[d][y].id == ' ') {
					moves[d][y] = true;
				}
				else { 
					if(grid[d][y].whitePiece!=whitePiece) {
						moves[d][y] = true;
					}
					d=0;
				}
				d--;
			}
			
			d = (byte) (y-1);
			while(d>=0) {
				if(grid[x][d].id == ' ') {
					moves[x][d] = true;
				}
				else {
					if(grid[x][d].whitePiece!=whitePiece) {
						moves[x][d] = true;
					}
					d=0;
				}
				d--;
			}
			
			d = (byte) (y+1);
			while(d<=7) {
				if(grid[x][d].id == ' ') {
					moves[x][d] = true;
				}
				else {
					if(grid[x][d].whitePiece!=whitePiece) {
						moves[x][d] = true;
					}
					d=7;
				}
				d++;
			}
			break;
		case 0x2657:
		case 0x265D:
			byte dx = (byte) (x+1);
			byte dy = (byte) (y+1);
			while(dx<=7 && dy<=7) {
				if(grid[dx][dy].id == ' ') {
					moves[dx][dy] = true;
				}
				else {
					if(grid[dx][dy].whitePiece!=whitePiece) {
						moves[dx][dy] = true;
					}
					dx=7;
					dy=7;
				}
				dx++;
				dy++;
			}
			dx = (byte) (x+1);
			dy = (byte) (y-1);
			while(dx<=7 && dy>=0) {
				if(grid[dx][dy].id == ' ') {
					moves[dx][dy] = true;
				}
				else {
					if(grid[dx][dy].whitePiece!=whitePiece) {
						moves[dx][dy] = true;
					}
					dx=7;
					dy=0;
				}
				dx++;
				dy--;
			}
			dx = (byte) (x-1);
			dy = (byte) (y+1);
			while(dx>=0 && dy<=7) {
				if(grid[dx][dy].id == ' ') {
					moves[dx][dy] = true;
				}
				else {
					if(grid[dx][dy].whitePiece!=whitePiece) {
						moves[dx][dy] = true;
					}
					dx=0;
					dy=7;
				}
				dx--;
				dy++;
			}
			dx = (byte) (x-1);
			dy = (byte) (y-1);
			while(dx>=0 && dy>=0) {
				if(grid[dx][dy].id == ' ') {
					moves[dx][dy] = true;
				}
				else {
					if(grid[dx][dy].whitePiece!=whitePiece) {
						moves[dx][dy] = true;
					}
					dx=0;
					dy=0;
				}
				dx--;
				dy--;
			}
			break;
		case 0x2658:
		case 0x265E:
			Point offsets[] = {new Point(2,1),new Point(1,2),new Point(-2,1),new Point(2,-1),
					new Point(-1,2),new Point(-1,-2),new Point(1,-2),new Point(-2,-1)};
			for(Point p : offsets) {
				if(x+p.x<8 && x+p.x>-1 && y+p.y<8 && y+p.y>-1) {
					if(grid[p.x+x][p.y+y].id == ' ') {
						moves[p.x+x][p.y+y] = true;
					}
					else {
						if(grid[p.x+x][p.y+y].whitePiece!=whitePiece) {
							moves[p.x+x][p.y+y] = true;
						}
					}
				}
			}
			break;
		case 0x265A:
		case 0x2654:
			offsets = new Point[] {new Point(1,1),new Point(1,-1),new Point(-1,0),new Point(0,1),
					new Point(0,-1),new Point(-1,-1),new Point(-1,1),new Point(1,0)};
			for(Point p : offsets) {
				if(x+p.x<8 && x+p.x>-1 && y+p.y<8 && y+p.y>-1) {
					if(grid[p.x+x][p.y+y].id == ' ') {
						moves[p.x+x][p.y+y] = true;
					}
					else {
						if(grid[p.x+x][p.y+y].whitePiece!=whitePiece) {
							moves[p.x+x][p.y+y] = true;
						}
					}
				}
			}
			break;
			

		case 0x265B:
		case 0x2655:
			d = (byte) (x+1);
			while(d<=7) {
				if(grid[d][y].id == ' ') {
					moves[d][y] = true;
				}
				else {
					if(grid[d][y].whitePiece!=whitePiece) {
						moves[d][y] = true;
					}
					d=7;
				}
				d++;
			}
			
			d = (byte) (x-1);
			while(d>=0) {
				if(grid[d][y].id == ' ') {
					moves[d][y] = true;
				}
				else { 
					if(grid[d][y].whitePiece!=whitePiece) {
						moves[d][y] = true;
					}
					d=0;
				}
				d--;
			}
			
			d = (byte) (y-1);
			while(d>=0) {
				if(grid[x][d].id == ' ') {
					moves[x][d] = true;
				}
				else {
					if(grid[x][d].whitePiece!=whitePiece) {
						moves[x][d] = true;
					}
					d=0;
				}
				d--;
			}
			
			d = (byte) (y+1);
			while(d<=7) {
				if(grid[x][d].id == ' ') {
					moves[x][d] = true;
				}
				else {
					if(grid[x][d].whitePiece!=whitePiece) {
						moves[x][d] = true;
					}
					d=7;
				}
				d++;
			}
			dx = (byte) (x+1);
			dy = (byte) (y+1);
			while(dx<=7 && dy<=7) {
				if(grid[dx][dy].id == ' ') {
					moves[dx][dy] = true;
				}
				else {
					if(grid[dx][dy].whitePiece!=whitePiece) {
						moves[dx][dy] = true;
					}
					dx=7;
					dy=7;
				}
				dx++;
				dy++;
			}
			dx = (byte) (x+1);
			dy = (byte) (y-1);
			while(dx<=7 && dy>=0) {
				if(grid[dx][dy].id == ' ') {
					moves[dx][dy] = true;
				}
				else {
					if(grid[dx][dy].whitePiece!=whitePiece) {
						moves[dx][dy] = true;
					}
					dx=7;
					dy=0;
				}
				dx++;
				dy--;
			}
			dx = (byte) (x-1);
			dy = (byte) (y+1);
			while(dx>=0 && dy<=7) {
				if(grid[dx][dy].id == ' ') {
					moves[dx][dy] = true;
				}
				else {
					if(grid[dx][dy].whitePiece!=whitePiece) {
						moves[dx][dy] = true;
					}
					dx=0;
					dy=7;
				}
				dx--;
				dy++;
			}
			dx = (byte) (x-1);
			dy = (byte) (y-1);
			while(dx>=0 && dy>=0) {
				if(grid[dx][dy].id == ' ') {
					moves[dx][dy] = true;
				}
				else {
					if(grid[dx][dy].whitePiece!=whitePiece) {
						moves[dx][dy] = true;
					}
					dx=0;
					dy=0;
				}
				dx--;
				dy--;
			}
			break;
		}
		return moves;
	}
}