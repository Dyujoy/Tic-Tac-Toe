import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * This program is used to make the GUI part of the game Tic-Tac-Toe and according to the input send information to the server.   
 * 
 * @author dyujo 
 *
 */
public class TicTacToeClient {

    public static JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel topHeading = new JLabel("");
    private static String symb;
    private static String otherSymb;
    
    
    JMenuBar menuBar = new JMenuBar();
	JMenu control = new JMenu("Control");
	JMenuItem exit = new JMenuItem("Exit");
	JMenu help = new JMenu("Help");
	JMenuItem instructions = new JMenuItem("Instructions");
	static JLabel heading = new JLabel("Enter Your Player Name."+"\t");
	JPanel panelTop = new JPanel();
	static JTextField inp_name = new JTextField(20);
	static JButton submitBtn = new JButton("Submit");
	JPanel panelName = new JPanel();
	static int flag = 0;
	static JPanel boardPanel;

    private static GRID[] board = new GRID[9];
    private static GRID mainGrid;

    private Socket socket;
    private BufferedReader in;
    private static PrintWriter out;

    /**
     * 
     * Starts the program.
     * 
     * @param args	argument of the main function
     * @throws Exception 	throws exception if there is an error during the program
     */
    // Constructs the client by connecting to a server, laying out the GUI and registering GUI listeners.
    @SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
            TicTacToeClient client = new TicTacToeClient();

        	client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(450, 450);
            client.frame.setVisible(true);
            client.frame.setResizable(false);
            client.play();
    }
    
    /**
     * 
     * This function sets the GUI part of the game.
     * 
     * @throws Exception	throws exception if there's an error.
     */
    public TicTacToeClient() throws Exception {

        socket = new Socket("127.0.0.1", 8901);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        topHeading.setBackground(Color.lightGray);
        panelTop.add(heading);
        panelTop.add(topHeading);
        frame.add(panelTop,BorderLayout.NORTH);
        
        panelName.add(inp_name);
        panelName.add(submitBtn);
        frame.add(panelName,BorderLayout.SOUTH);
        
        submitBtn.addActionListener(new NameListener());
        
        exit.addActionListener(new ExitListener());
        instructions.addActionListener(new InstructListener());
        
        control.add(exit);
        help.add(instructions);
        menuBar.add(control);
        menuBar.add(help);
        frame.setJMenuBar(menuBar);
        
        
        
        boardPanel = new JPanel();
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));

        
        for (int i = 0; i < board.length; i++) {
            board[i] = new GRID();
            boardPanel.add(board[i]);
            System.out.println("checkitout");
        }
        

        frame.add(boardPanel,BorderLayout.CENTER);
    }

   
    /**
     * 
     * Reads the input sent by the server and accordingly displays on the game window
     * 
     * @throws Exception	throws exception if there's an error
     * 
     */
    public void play() throws Exception {
        String response;
        try {
            response = in.readLine();
            if (response.startsWith("WELCOME")) {
                char mark = response.charAt(8);
                               
                System.out.println(mark);
                symb = (mark == 'X' ? "X" : "O");
                otherSymb  = (mark == 'X' ? "O" : "X");

                System.out.println(symb);

            }
            while (true) {
                response = in.readLine();
                if (response.startsWith("VALID_MOVE")) {
                    topHeading.setText("Valid move, wait for you opponent");
                    mainGrid.setIcon(symb);

                    System.out.println("XXX");
                } else if (response.startsWith("OPPONENT_MOVED")) {
                    int loc = Integer.parseInt(response.substring(15));
                    board[loc].setIcon(otherSymb);

                    topHeading.setText("Opponent has moved, it's your turn");
                } else if (response.startsWith("VICTORY")) {
                	heading.setText(inp_name.getText()+". ");
                    topHeading.setText("You win");
                	JOptionPane.showMessageDialog(frame, "You won.");
                	boardPanel.setEnabled(false);
                	
                    break;
                } else if (response.startsWith("DEFEAT")) {
                	heading.setText(inp_name.getText()+". ");
                    topHeading.setText("You lose");
                	JOptionPane.showMessageDialog(frame, "You Lost.");
                	boardPanel.setEnabled(false);

                    break;
                } else if (response.startsWith("TIE")) {
                    topHeading.setText("You tied");
                	JOptionPane.showMessageDialog(frame, "You Drew.");

                    break;
                } else if (response.startsWith("MESSAGE")) {
                	System.out.println("panel check1");
                    topHeading.setText(response.substring(8));
                    if(response.startsWith("MESSAGE Y")) {
                    	System.out.println("Panel false");

                    }
                }else if(response.startsWith("DIED")) {
                	JOptionPane.showMessageDialog(frame, "Game ends. One of the players left.");
                	boardPanel.setEnabled(false);
                	System.out.println("BYE BYE");

                }
            }
            out.println("QUIT");
        }
        finally {
            socket.close();
        }
        
    }


    @SuppressWarnings("serial")
    /**
     * 
     * Forms each individual grid for the game.
     * 
     * @author dyujo
     *
     */
    
	static class GRID extends JPanel {
        JLabel label = new JLabel("");
        
        /**
         * 
         * Constructor for the function.
         * 
         */
        public GRID() {
            setBackground(Color.white);
            add(label);

        }
        
        /**
         * 
         * sets the symbol for the player and the opponent.
         * 
         * @param symb	string type which contains the symbol that has to be set on the current grid.
         */
        public void setIcon(String symb) {
        	

        	if(symb=="O") {
        		label.setFont( new Font("Helvetica BOLD", Font.BOLD, 60));
    			label.setForeground(Color.RED);
    			System.out.println("OOOOOOO");
    			label.setText("O");
        	}else if(symb=="X") {
        		label.setFont( new Font("Helvetica BOLD", Font.BOLD, 60));
				label.setForeground(Color.BLUE);
    			System.out.println("XXXXXX");

				label.setText("X");
        	}
        	
        	
            System.out.println("M1");
        }
    }
    
    /**
     * 
     * When Exit Menu Item is clicked, the user exits the program.
     * 
     * @author dyujo
     *
     */
    static class ExitListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		System.exit(0);
    	}   	
    }
    
    /**
     * 
     * When Instruction Menu Item is clicked, the rules is shown.
     * 
     * @author dyujo
     *
     */
    static class InstructListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(frame, "Some information about the game\n" + "Criterion for a valid move:\n" +	"The move is not occupied by any mark.\n" +"The move is made in the players turn.\n" + "The move is made within the 3X3 board. \n"+"The Game shall reach any one of the following conditions: \n"+"Player 1 wins.\n"+"Player 2 wins.\n"+"Draw.");
    	}   	
    }
    
    /**
     * 
     * Checks if the textfield is empty. Sets the top Panel name.
     * 
     * @author dyujo
     *
     */
    static class NameListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		if(inp_name.getText().isEmpty()) {
    			JOptionPane.showMessageDialog(frame, "Please fill in your name");
    		}else {
  				flag++;
				
				inp_name.setText(inp_name.getText());
				inp_name.setEnabled(false);
				submitBtn.setEnabled(false);
				heading.setText("Welcome "+inp_name.getText()+". ");
				out.println("GET"+inp_name.getText());
				for (int i = 0; i < board.length; i++) {
		            final int j = i;
		            board[i].addMouseListener(new MouseAdapter() {
		                public void mousePressed(MouseEvent e) {
		                    mainGrid = board[j];
		                    System.out.println("check it out 22");
		                    out.println("MOVE " + j);}});
		            boardPanel.setEnabled(false);
		            System.out.println("check it out 22");
		            boardPanel.add(board[i]);
		        }
				boardPanel.setEnabled(false);
    		}
    	}   	
    }    
    
}



