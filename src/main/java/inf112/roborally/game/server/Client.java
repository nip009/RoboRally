package inf112.roborally.game.server;

import inf112.roborally.game.RoboRallyGame;
import inf112.roborally.game.player.ProgramCard;
import inf112.roborally.game.enums.Rotate;
import inf112.roborally.game.objects.Position;
import inf112.roborally.game.player.Player;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable{

    private final String host;
    private final int port;
    private RoboRallyGame game;
    private ArrayList<ProgramCard> chosenCards;
    private String name;
    private List<Player> playersConnected;
    Channel channel;

    public Client(String host, int port, RoboRallyGame game, String name) {
        this.host = host;
        this.port = port;
        this.game = game;
        this.name = name;
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();

            try{
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInit(game));
                channel = null;

            try {
                channel = bootstrap.connect(host, port).sync().channel();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.writeAndFlush("HANDSHAKE" + " " + name);

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));


            while (true) {

            }
        }
        finally {
            group.shutdownGracefully();
        }
    }
    public void sendMessage(String s){
        channel.writeAndFlush(s + "\r\n");
    }

    public Channel getChannel(){
        return this.channel;
    }

    public List<Player> getPlayers(){
        return playersConnected;
    }
}
