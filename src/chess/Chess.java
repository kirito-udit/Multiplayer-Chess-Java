/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Chess extends JPanel implements Runnable, MouseListener {
	
	
	
	private static final long serialVersionUID = -8316910605042033620L;
	private JFrame frame;
	private JFrame grave;
	private JFrame chat;
	private Container messages;
	private JPanel WGY;
	private JPanel BGY;
	private BufferedImage buffer;
	private Graphics g3;
	private final Dimension DIM;
	private byte refresh = 0;
	private final byte refreshRate = 50;
	private Cell board[][];
	private boolean availableMoves[][] = new boolean[8][8];
	private Point selectedCell = new Point(8,8);
	private boolean yourTurn = true;
	private boolean isWhite;
	private User player1;
	private User player2;
	private ArrayList<Cell> whiteGY = new ArrayList<Cell>();
	private ArrayList<Cell> blackGY = new ArrayList<Cell>();
	private ArrayList<String> transcript = new ArrayList<String>();
	
	
	public static void main(String[] args) {
		ServerSocket player1;
		Socket socket2 = null;
		Socket socket1 = null;
		try {
			player1 = new ServerSocket(0);
			socket2 = new Socket("localhost", player1.getLocalPort());
			socket1 = player1.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Chess(new User("Jack",socket1), new User("Jill",socket2), true).frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new Chess(new User("Jill",socket2), new User("Jack",socket1), false).frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public Chess(User p1, User p2, boolean wp) {
		frame = new JFrame("Chess");
		DIM = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize((int)(DIM.height/1.5),(int)(DIM.height/1.5));
		frame.add(this);
		addMouseListener(this);
		//frame.setUndecorated(true);
		//frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setFocusable(true);
		requestFocus();
		buffer = new BufferedImage((int)(DIM.height/1.5),(int)(DIM.height/1.5), BufferedImage.TYPE_INT_ARGB);
		g3 = buffer.getGraphics();
		player1 = p1;
		player2 = p2;
		isWhite = wp;
		resetBoard();
		Graveyard();
		Chat();
		Thread t = new Thread(this);
		t.start();
		
		if(!wp) {
			new Thread(new Move(null)).start();
		}
	}
	
	public void Graveyard(){
		grave = new JFrame("Graveyards");
		grave.setSize(300,(int)(DIM.height/1.5));
		grave.setLocation((int) (frame.getX()+10+(DIM.height/1.5)),frame.getY());
		grave.setVisible(true);
		grave.setResizable(false);
		grave.setLayout(new GridLayout(2,1));
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
		JLabel WGYLabel = new JLabel(player1.Username + "'s Graveyard");
		WGYLabel.setFont(new Font(getFont().getFontName(),Font.PLAIN,20));
		top.add(WGYLabel);
		WGYLabel.setAlignmentX(CENTER_ALIGNMENT);
		WGY = new JPanel(new GridLayout(4,4));
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom,BoxLayout.Y_AXIS));
		JLabel BGYLabel = new JLabel(player2.Username + "'s Graveyard");
		BGYLabel.setFont(new Font(getFont().getFontName(),Font.PLAIN,20));
		bottom.add(BGYLabel);
		BGYLabel.setAlignmentX(CENTER_ALIGNMENT);
		BGY = new JPanel(new GridLayout(4,4));
		if(isWhite) {
			top.add(WGY);
			bottom.add(BGY);
		} else {
			bottom.add(WGY);
			top.add(BGY);
		}
		grave.add(top);
		grave.add(bottom);
	}
	
	public void Chat(){
		chat = new JFrame("Chat");
		chat.setSize(300,(int)(DIM.height/1.5));
		chat.setLocation((int) (frame.getX()-310),frame.getY());
		chat.setVisible(true);
		chat.setResizable(false);
		JTextField inputChat = new JTextField();
		messages = new Container();
		messages.setLayout(new BoxLayout(messages, BoxLayout.Y_AXIS));
		chat.add(messages, BorderLayout.NORTH);
		inputChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextPane message = new JTextPane();
				SimpleAttributeSet attribs = new SimpleAttributeSet();
				StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_LEFT);
				message.setParagraphAttributes(attribs, true);
				message.setText(inputChat.getText());
				message.setEditable(false);
				message.setBackground(Color.GREEN);
				messages.add(message);
            	messages.revalidate();
            	messages.repaint();
            	if(isWhite!=yourTurn) {
            		transcript.add("MESSAGE||"+inputChat.getText()+"||");
            	} else {
            		SendMessage("MESSAGE||"+inputChat.getText()+"||");
            	}
				inputChat.setText("");
			}
		});
		chat.add(inputChat, BorderLayout.SOUTH);
    	chat.revalidate();
    	chat.repaint();
		
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(buffer,0,0,null);
	}
	
	private void make() {
		g3.setFont(new Font(Font.MONOSPACED, Font.BOLD, getHeight()/14));
		g3.setColor(Color.ORANGE);
		g3.fillRect(0, 0, getWidth(), getHeight());
		for(byte y=0; y<8; y++) {
			for(byte x=0; x<8; x++) {
				if(availableMoves[x][y] || x==selectedCell.x && y==selectedCell.y) {
					g3.setColor(Color.CYAN);
					g3.fillRect(x*(getWidth()/8),y*(getWidth()/8),(getWidth()/8),(getWidth()/8));
					g3.setColor(Color.BLUE);
					g3.drawRect(x*(getWidth()/8),y*(getWidth()/8),(getWidth()/8),(getWidth()/8));
				} else if(x%2==0 && y%2==0 || x%2==1 && y%2==1) {
					g3.setColor(new Color(163,64,29));
					g3.fillRect(x*(getWidth()/8),y*(getWidth()/8),(getWidth()/8),(getWidth()/8));
				}
				if(board[x][y].whitePiece) g3.setColor(Color.WHITE);
				else g3.setColor(Color.BLACK);
				g3.drawString(""+board[x][y].id,(int)((x*(getWidth()/8))+(getWidth()/32)),(int)((y*(getHeight()/7.8))+(getHeight()/10)));
			}
		}
		repaint();
	}
	
	public void run() {
		while(frame.isVisible()) {
			if(refresh>refreshRate) {
				make();
				refresh=0;
			}refresh++;
			
			try {
				Thread.sleep(1);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		SendMessage("CLOSE||");
        try {
			player1.Socket.close();
	        player1.input.close();
	        player1.output.close();
	        player2.Socket.close();
	        player2.input.close();
	        player2.output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		grave.setVisible(false);
		chat.setVisible(false);
		grave.dispose();
		chat.dispose();
		frame.dispose();
		new Multiplayer(player1, Account.server);
	}
	
	public void movePiece(byte x, byte y) {
		Cell temp = board[selectedCell.x][selectedCell.y];
		if((x==selectedCell.x && y==selectedCell.y) || !availableMoves[x][y]) {
			selectedCell = new Point(8,8);
		} else {
			temp.firstMove=false;
			if(temp.id!=' ') {
				if((temp.whitePiece!=board[x][y].whitePiece) || board[x][y].id==' ') {
					board[selectedCell.x][selectedCell.y] = new Cell(' ');
	            	
					if(board[x][y].id!=' ') {
						if(board[x][y].whitePiece){
							JLabel tempLabel = new JLabel(""+board[x][y].id);
							tempLabel.setFont(new Font(getFont().getFontName(),Font.PLAIN,20));
							WGY.add(tempLabel);
							whiteGY.add(board[x][y]);
						}
						else{
							JLabel tempLabel = new JLabel(""+board[x][y].id);
							tempLabel.setFont(new Font(getFont().getFontName(),Font.PLAIN,20));
							BGY.add(tempLabel);
							blackGY.add(board[x][y]);
						}
						grave.revalidate();
						grave.repaint();
					}
					board[x][y] = temp;
					if(isWhite==yourTurn) {
						JTextPane message = new JTextPane();
						SimpleAttributeSet attribs = new SimpleAttributeSet();
						StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_LEFT);
						message.setParagraphAttributes(attribs, true);
						message.setText(PositionName((byte)selectedCell.x,(byte)selectedCell.y) +
								" moves to " +
								PositionName(x, y));
						message.setEditable(false);
						message.setBackground(Color.GREEN);
		            	messages.add(message);
		            	messages.revalidate();
		            	messages.repaint();
		            	
						new Thread(new Move(selectedCell.x + "||" + selectedCell.y + "||" + x + "||" + y + "||")).start();
					}
					selectedCell = new Point(8,8);
				}
			}
		}
		availableMoves = new boolean[8][8];
	}
	
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==1 && isWhite==yourTurn) {
			byte x = (byte) (((e.getX()+0.0)/frame.getWidth())*8), y = (byte) (((e.getY()+0.0)/frame.getWidth())*8);
			if(selectedCell.x==8) {
				if(board[x][y].whitePiece==yourTurn) {
					selectedCell = new Point(x,y);
					availableMoves = board[x][y].possibleMoves(selectedCell, board);
				}
			}
			else if(board[selectedCell.x][selectedCell.y].whitePiece==yourTurn) {
				movePiece(x,y);
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		
	}
	public void mouseClicked(MouseEvent e) {
		
	}
	public void mouseEntered(MouseEvent e) {
		
	}
	public void mouseExited(MouseEvent e) {
		
	}
	
	public void resetBoard() {
		board = new Cell[8][8];
		final short pieces[][] = {
				{0x265c,0x265E,0x265D,0x265A,0x265B,0x265D,0x265E,0x265c},
				{0x265F,0x265F,0x265F,0x265F,0x265F,0x265F,0x265F,0x265F},
				{0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20},
				{0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20},
				{0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20},
				{0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20},
				{0x2659,0x2659,0x2659,0x2659,0x2659,0x2659,0x2659,0x2659},
				{0x2656,0x2658,0x2657,0x2654,0x2655,0x2657,0x2658,0x2656}
		};
		for(byte y=0; y<8; y++) {
			for(byte x=0; x<8; x++) {
				board[x][y] = new Cell((char) pieces[y][x]);
			}
		}
	}

	
	public void SendMessage(String data) {
		try {
			player1.output.writeBytes(data + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	class Move implements Runnable {
		
		private String data; 
		
		public Move(String d) {
			data = d;
		}
		
		public void run() {
			if(data==null) {
				WaitMove();
			}else {
				MoveSend(data);
				WaitMove();
			}
		}
		
		public void MoveSend(String data) {
			for(String s : transcript) {
				SendMessage(s);
			}
			transcript.clear();
			try {
				player1.output.writeBytes(data + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			yourTurn = !yourTurn;
			make();
			frame.revalidate();
		}

		
		
		public void WaitMove() {
			try {
				String data = player1.input.readLine();
	            String inputData[] = data.split("\\|\\|");
	            switch(inputData[0]) {
        		case "CLOSE":
		            player1.Socket.close();
		            player1.input.close();
		            player1.output.close();
		            player2.Socket.close();
		            player2.input.close();
		            player2.output.close();
	            	frame.setVisible(false);
	            	break;
            	case "MESSAGE":
    				JTextPane message = new JTextPane();
    				SimpleAttributeSet attribs = new SimpleAttributeSet();
    				StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
    				message.setParagraphAttributes(attribs, true);
    				message.setText(inputData[1]);
    				message.setEditable(false);
					message.setBackground(Color.GRAY);
	            	messages.add(message);
	            	messages.revalidate();
	            	messages.repaint();
	            	WaitMove();
	            	break;
	            default:
		            selectedCell = new Point(Integer.parseInt(inputData[0]),Integer.parseInt(inputData[1]));
					availableMoves = board[selectedCell.x][selectedCell.y].possibleMoves(selectedCell, board);
		            movePiece(Byte.parseByte(inputData[2]), Byte.parseByte(inputData[3]));
		            
    				message = new JTextPane();
    				attribs = new SimpleAttributeSet();
    				StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
    				message.setParagraphAttributes(attribs, true);
    				message.setText(PositionName(Byte.parseByte(inputData[0]),Byte.parseByte(inputData[1])) +
    						" moves to " +
    						PositionName(Byte.parseByte(inputData[2]), Byte.parseByte(inputData[3])));
    				message.setEditable(false);
					message.setBackground(Color.GRAY);
	            	messages.add(message);
	            	messages.revalidate();
	            	messages.repaint();
	            	
					yourTurn = !yourTurn;
					make();
					frame.revalidate();
	            }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String PositionName(byte a, byte b) {
		char column = (char)(a+65);
		return column + "" + (b+1);
	}
	
}

