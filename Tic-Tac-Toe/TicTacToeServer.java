import java.net.ServerSocket;

/**
 * 
 * Server of the game.
 * 
 * @author dyujo
 *
 */
public class TicTacToeServer {
	/**
	 * 
	 * Is the main of the program.
	 * 
	 * @param args		arguments of the main.
	 * @throws Exception	throws exception if theres an error
	 */
    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(8901);
        System.out.println("Running");
        try {
            while (true) {
                TicTacToe game = new TicTacToe();
                TicTacToe.User playX = game.new User(listener.accept(), 'X');
                TicTacToe.User playO = game.new User(listener.accept(), 'O');
                playX.setOpponent(playO);
                playO.setOpponent(playX);
                game.mainUser = playX;
                playX.start();
                playO.start();
            }
        } finally {
            listener.close();
        }
    }
}
