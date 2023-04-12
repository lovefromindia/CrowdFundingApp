package org.crowdfund.db;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBDriver {
    int port;
    private final ExecutorService threadPool;
    private DBDriver(int port)
    {
        this.port = port;
        threadPool = Executors.newFixedThreadPool(16);
    }

    private void start()
    {

        //TODO remove below log line
        System.out.println("DB started on " + port);

        try(ServerSocket socket = new ServerSocket(port))
        {

            while(!socket.isClosed())
            {

                Socket connection = socket.accept();

                if(connection != null)
                {
                    threadPool.execute(new DBRequestHandler(connection));
                }

            }

        }catch (Exception exception)
        {
            System.out.println(exception.getMessage());
        }
    }


    public static void main(String[] args)
    {

        //TODO convert hardcoded arguments into CLI args
        new DBDriver(2222).start();

    }

}

