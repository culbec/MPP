using System.Net;
using System.Net.Sockets;
using log4net;

namespace Network.NetworkUtils;

public abstract class AbstractServer
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(AbstractServer));
    private TcpListener? _server;
    private readonly string _host;
    private readonly int _port;

    protected AbstractServer(string host, int port)
    {
        _host = host;
        _port = port;
    }

    public void Start()
    {
        try
        {
            Log.Info("Initializing the IP address...");
            var ipAddress = IPAddress.Parse(_host);

            Log.InfoFormat("Initializing with {0}:{1}", _host, _port);
            var endPoint = new IPEndPoint(ipAddress, _port);

            Log.InfoFormat("Initializing the server with {0}", endPoint);
            _server = new TcpListener(endPoint);

            Log.Info("Starting the server...");
            _server.Start();

            Log.Info("Server started on port " + _port);
            while (true)
            {
                Log.Info("Waiting for clients...");
                var client = _server.AcceptTcpClient();

                Log.Info("Client connected. Processing the request...");
                ProcessRequest(client);
            }
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An exception occurred: {0}", e.Message);
        }
        finally
        {
            Log.InfoFormat("Stopping the server...");
            Stop();
        }
    }

    private void Stop()
    {
        if (_server == null)
        {
            Log.Warn("The server was not initialized!");
            return;
        }

        try
        {
            _server.Stop();
            Log.Info("Server stopped.");
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Couldn't stop the server: {0}", e.Message);
        }
    }

    protected abstract void ProcessRequest(TcpClient client);
}

public abstract class AbstractConcurrentServer : AbstractServer
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(AbstractConcurrentServer));
    protected AbstractConcurrentServer(string host, int port) : base(host, port)
    {
    }

    protected override void ProcessRequest(TcpClient client)
    {
        Log.Info("Creating a worker...");
        var thread = CreateWorker(client);

        Log.Info("Starting the worker...");
        thread.Start();
    }

    protected abstract Thread CreateWorker(TcpClient client);
}