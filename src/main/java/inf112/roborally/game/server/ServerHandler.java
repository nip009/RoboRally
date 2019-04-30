package inf112.roborally.game.server;

import inf112.roborally.game.RoboRallyGame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private final RoboRallyGame game;

    public ServerHandler(RoboRallyGame game) {
        this.game = game;
    }

    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] -  " + incoming.remoteAddress() + "has left\n");
        }
        channels.remove(ctx.channel());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        Channel incoming = channelHandlerContext.channel();
        System.out.println(msg);
        for (Channel channel : channels) {
            if (channel != incoming)
                channel.writeAndFlush("[" + incoming.remoteAddress() + "] " + msg + "\n");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String packet = msg.toString();
        String[] split = packet.split(" ");

        if (split[0].equals("HANDSHAKE")) {
            System.out.println(split[1] + " has connected!");
            game.playerNames.add(split[1]);
            for (Channel channel : channels) {
                channel.writeAndFlush(split[1] + " has connected!");
            }
        }
        if (split[0].equals("PLAY")) {
            for (Channel channel :
                    channels) {
                channel.writeAndFlush("PLAY" + split[1]);
            }
        }
        if (split[0].equals("START")) {
            for (Channel channel :
                    channels) {
                channel.writeAndFlush("START" + split[1]);

                }
            }
    }
}
