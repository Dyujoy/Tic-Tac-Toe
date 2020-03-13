import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * 
 * The program where the input form the Client side is assessed and sends the information back. 
 * 
 * @author dyujo
 *
 */
class TicTacToe {
	private static Set<PrintWriter> writers = new HashSet<>();
//	Socket [] sockets = new Socket[2];
	ArrayList<Socket> sockets = new ArrayList<Socket>();
	ArrayList<String> names = new ArrayList<String>();
	int flag = 0;
	
    private User[] Grid = { null, null, null, null, null, null, null, null, null};

    User mainUser;
    
    /**
     * 
     * Checks if the move is valid
     * 
     * @param pos	is the position of the clicked grid
     * @param player	shows which player is playing
     * @return		returns boolean if the move is valid or not
     * 
     */
    public synchronized boolean checkMove(int pos, User player) {
        if (player == mainUser && Grid[pos] == null) {
            Grid[pos] = mainUser;
            mainUser = mainUser.otherUser;
            mainUser.otherPlayerMoved(pos);
            return true;
        }
        return false;
    }
    /**
     * 
     * Checks if the position clicked by the user is filled or not
     * 
     * @return		returns boolean if the position is filled or not
     */
    public boolean checkFilled() {
        for (int i = 0; i < Grid.length; i++) {
            if (Grid[i] == null) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * 
     * Checks if the user won.
     * 
     * @return 		returns boolean if the user won.
     */
    public boolean winCheck() { return
            (Grid[0] != null && Grid[0] == Grid[1] && Grid[0] == Grid[2]) ||(Grid[3] != null && Grid[3] == Grid[4] && Grid[3] == Grid[5])
          ||(Grid[6] != null && Grid[6] == Grid[7] && Grid[6] == Grid[8]) ||(Grid[0] != null && Grid[0] == Grid[3] && Grid[0] == Grid[6])
          ||(Grid[1] != null && Grid[1] == Grid[4] && Grid[1] == Grid[7]) ||(Grid[2] != null && Grid[2] == Grid[5] && Grid[2] == Grid[8])
          ||(Grid[0] != null && Grid[0] == Grid[4] && Grid[0] == Grid[8]) ||(Grid[2] != null && Grid[2] == Grid[4] && Grid[2] == Grid[6]);
    }
    
    
    /**
     * 
     * Class of the user.
     * 
     * @author dyujo
     *
     */
    class User extends Thread {
        
        User otherUser;
        Socket socket;
        BufferedReader inp;
        PrintWriter out;
        char symb;
        
        
        /**
         * 
         * constructor for the class.
         * 
         * @param socket
         * @param symb
         */
        public User(Socket socket, char symb) {
            this.socket = socket;
            this.symb = symb;
            sockets.add(socket);
            writers.add(out);
            try {
                inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("WELCOME " + symb);
                out.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
            	out.println("DIED");
              System.out.println("Player died1: " + e);						//Use to check if one player has left or not
            }
        }
        /**
         * 
         * Sets the other user.
         * 
         * @param otherUser		is the other user.
         */
        public void setOpponent(User otherUser) {
            this.otherUser = otherUser;
        }

        
        /**
         * 
         * Sends the position where the other player moved.
         * 
         * @param position		position of the other user.
         */
        public void otherPlayerMoved(int position) {
            out.println("OPPONENT_MOVED " + position);
            out.println(winCheck() ? "DEFEAT" : checkFilled() ? "TIE" : "");
        }
    
        /**
         * 
         * Starts the thread.
         * 
         */
        public void run() {
            try {
                out.println("MESSAGE All players connected");

                if (symb == 'X') {
                    out.println("MESSAGE Your move");
                }

                while (true) {
                    String command = inp.readLine();
                    if (command.startsWith("MOVE")&& names.size()==2) {
                        int position = Integer.parseInt(command.substring(5));
                        if (checkMove(position, this)) {
                            out.println("VALID_MOVE");
                            out.println(winCheck() ? "VICTORY": checkFilled() ? "TIE": "");
                        } else {
//                            output.println("MESSAGE Invalid Move");			
                        }
                    } else if (command.startsWith("QUIT")) {
                    	out.println("DIED");
                    	System.out.println("Died11");
                        return;
                    }else if(command.startsWith("GET")) {
                    	names.add(command.substring(3));
                    }                    
                }
                
            } catch (IOException e) {
            	
            	for(Socket sock : sockets) {
            		try {
            			System.out.println("BYEBYE");
						PrintWriter outp= new PrintWriter(sock.getOutputStream(), true);
						outp.println("DIED");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}finally {
						try {
							sock.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
            	}
            	
            	System.out.println("BYE BYE");
                System.out.println("Player died2: " + e);
            } finally {
            	
                try {socket.close();} catch (IOException e) {}
            }
        }
    }
}
